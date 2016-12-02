package se.su.it.vaulttool

import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder

class VaultRestService {
    def grailsApplication
    private HTTPBuilder http = null

    private synchronized HTTPBuilder getRestClient() {
        if (!http) {
            http = new HTTPBuilder(grailsApplication.config.vault.url)
        }
        return http
    }

    private Map doRest(String token, String method, String restPath, String mediaType, Map theBody = [:], Map query = [:]) {
        Map result = [:]
        def internalMethod

        if (!method || !restPath /*|| !mediaType*/) {
            throw new Exception("method, restPath and mediaType are all mandatory arguments to doRest function!")
        }
        switch (method.toUpperCase()) {
            case "GET": internalMethod      = groovyx.net.http.Method.GET
                break
            case "POST": internalMethod     = groovyx.net.http.Method.POST
                break
            case "PUT": internalMethod      = groovyx.net.http.Method.PUT
                break
            case "DELETE": internalMethod   = groovyx.net.http.Method.DELETE
                break
            default: throw new Exception("Method '${method}' not recognized. Only GET, POST, PUT or DELETE may be used!")
        }
        try {
            getRestClient().request(internalMethod, mediaType) {
                headers."X-Vault-Token" = token//"78600c52-5062-1d55-7e50-6b88c8865e79"
                uri.path = restPath
                if (query) {
                    uri.query = query
                }
                if(theBody) {
                    body = theBody
                }
                response.success = { resp, reader ->
                    result = reader
                }
                response.'404' = { resp, reader ->
                    //log.warn("Entry not found 404 while doing REST REQUEST: ${resp.statusLine}, ${reader.getText()}")
                    result = null
                }
                response.failure = { resp, reader ->
                    log.warn("Failure while doing REST REQUEST: ${resp?.statusLine}, ${reader}")
                    result = ["status": resp.status]
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace()
        }
    }

    private Map getJsonByUrlAndType(String token, String url, Map query = [:]) {
        String jsonUtf8Type = "application/json; charset=UTF-8"
        return doRest(token, "GET", url, jsonUtf8Type, null, query)
    }

    private Map putJsonByUrlAndType(String token, String url, Map body = [:], Map query = [:]) {
        String jsonUtf8Type = "application/json; charset=UTF-8"
        return doRest(token, "PUT", url, jsonUtf8Type, body, query)
    }

    private Map postJsonByUrlAndType(String token, String url, Map body = [:], Map query = [:]) {
        String jsonUtf8Type = "application/json; charset=UTF-8"
        return doRest(token, "POST", url, jsonUtf8Type, body, query)
    }

    private Map deleteJsonByUrlAndType(String token, String url, Map body = [:], Map query = [:]) {
        String jsonUtf8Type = "application/json; charset=UTF-8"
        return doRest(token, "DELETE", url, jsonUtf8Type, body, query)
    }

    String getEntitlementToken(String rootToken, String entitlement) {
        String role_id = ""
        String secret_id = ""
        Map response = getJsonByUrlAndType(rootToken, "/v1/auth/approle/role/${entitlement}/role-id", null)
        role_id = response?.data?.role_id
        response = postJsonByUrlAndType(rootToken,"/v1/auth/approle/role/${entitlement}/secret-id", null, null)
        secret_id = response?.data?.secret_id
        response = postJsonByUrlAndType(rootToken,"/v1/auth/approle/login", ["role_id": role_id, "secret_id": secret_id], null)

        return response?.auth?.client_token?:null
    }

    Entry getSecret(String token, String key) {
        Map response = getJsonByUrlAndType(token, "/v1/secret/$key", null)
        Entry entry = new Entry()
        entry.key           = response?.data?.key?:null
        entry.title         = response?.data?.title?:null
        entry.description   = response?.data?.description?:null
        entry.pwd           = response?.data?.pwd?:null
        entry.binaryData    = response?.data?.binaryData?:null
        entry.fileName      = response?.data?.fileName?:null
        return entry
    }

    List<String> listSecrets(String token, String path = "") {
        Map query = ["list":true]
        Map response = getJsonByUrlAndType(token, "/v1/secret/${path}", query)

        return response?.data?.keys?:null
    }

    Map putSecret(String token, String key, Entry secret) {
        return putJsonByUrlAndType(token,"/v1/secret/${key}", secret.asMap(), null)
    }

    Map deleteSecret(String token, String key) {
        return deleteJsonByUrlAndType(token,"/v1/secret/${key}", null, null)
    }

    List<String> getSecretTree(String token, String path = "") {
        List<String> secrets = []
        listSecrets(token, path).each{
            String secret = path.length()<= 0?it:path+it
            if(secret.endsWith("/")) {
                secrets.addAll(getSecretTree(token, secret))
            } else {
                secrets << secret
            }
        }
        return secrets
    }

    List<String> getPaths(String token, String path = "") {
        List<String> paths = []
        listSecrets(token, path).each{
            String secret = path.length()<= 0?it:path+it
            if(secret.endsWith("/")) {
                paths << secret
                paths.addAll(getPaths(token, secret))
            }
        }
        return paths
    }
}
