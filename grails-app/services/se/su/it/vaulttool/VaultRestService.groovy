package se.su.it.vaulttool

import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder

class VaultRestService {
    static final public VAULTTOOLUSERSPATHNAME = "vaulttoolusers"
    static final public VAULTTOOLSECRETSPATHNAME = "vaulttoolsecrets"
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
                    if(internalMethod == groovyx.net.http.Method.GET) {
                        result = null
                    } else {
                        result = ["status": resp?.status?:0, "msg": resp?.statusLine?:"", "returnbody": reader?:""]
                    }
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

    void enableApproleAuth(String rootToken) {
        try {
            postJsonByUrlAndType(rootToken, "/v1/sys/auth/approle", ["type": "approle"], null)
        } catch (Exception ex) {

        }
    }

    void checkAndRenewToken(String token) {
        Map query = ["token": token]
        Map response = putJsonByUrlAndType(token, "/v1/auth/token/lookup", query)

        if(response?.data?.renewable && response?.data?.ttl < 3600) {
            query = ["token": "11b4fd26-7230-0acb-e983-425daa4224f0", "increment": "24h"]
            response = putJsonByUrlAndType(token, "/v1/auth/token/renew", query)
        }
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

    Expando getSecret(String token, String key) {
        Map response = getJsonByUrlAndType(token, "/v1/secret/${VAULTTOOLSECRETSPATHNAME}/$key", null)
        if(response == null) {
            return new Expando(status: null, entry: null)
        } else if(response.status) {
            return new Expando(status: response.status, entry: null)
        }
        Entry entry = new Entry()
        entry.key           = response?.data?.key?:null
        entry.userName      = response?.data?.userName?:null
        entry.pwd           = response?.data?.pwd?:null
        entry.binaryData    = response?.data?.binaryData?:null
        return new Expando(status: null, entry: entry)
    }

    List<String> listSecrets(String token, String path = "") {
        Map query = ["list":true]
        Map response = getJsonByUrlAndType(token, "/v1/secret/${VAULTTOOLSECRETSPATHNAME}/${path}", query)

        return response?.data?.keys?:[]
    }

    Map putSecret(String token, String key, Entry secret) {
        return putJsonByUrlAndType(token,"/v1/secret/${VAULTTOOLSECRETSPATHNAME}/${key}", secret.asMap(), null)
    }

    Map deleteSecret(String token, String key) {
        return deleteJsonByUrlAndType(token,"/v1/secret/${VAULTTOOLSECRETSPATHNAME}/${key}", null, null)
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

    List<String> getCapabilities(String token, String path) {
        Map body = ["token": token, "path": "secret/vaulttoolsecrets/" + path + "*"]
        Map response = postJsonByUrlAndType(token, "/v1/sys/capabilities-self", body, null)

        return response?.data?.capabilities?:[]
    }

    List<Map<String,String>> getPolicies(String token) {
        List<Map<String,String>> policies = []
        Map response = getJsonByUrlAndType(token, "/v1/sys/policy", null)

        if(response?.data?.policies) {
            response.data.policies.each {String policy ->
                Map response2 = getJsonByUrlAndType(token, "/v1/sys/policy/${policy}", null)
                policies << [policy: response2?.data?.name?:"", rules: response2?.data?.rules?:""]
            }
        }
        policies.removeAll {it.policy == "default" || it.policy == "root" || !it.rules.contains("secret/${VAULTTOOLSECRETSPATHNAME}")}

        return policies
    }

    Map putDefaultVaultToolPolicy(String token) {
        Policy policy = new Policy()
        policy.setName("defaultvaulttool")
        policy.setSpecialPath("secret/*")
        policy.list = true
        return putJsonByUrlAndType(token,"/v1/sys/policy/${policy.name}", policy.asMap(), null)
    }

    Map putPolicy(String token, Policy policy) {
        return putJsonByUrlAndType(token,"/v1/sys/policy/${policy.name}", policy.asMap(), null)
    }

    Map deletePolicy(String token, String policy) {
        return deleteJsonByUrlAndType(token,"/v1/sys/policy/${policy}", null, null)
    }

    List<Map<String,List<String>>> getAppRoles(String token) {
        List<Map<String,List<String>>> appRoles = []
        Map query = ["list":true]
        Map response = getJsonByUrlAndType(token, "/v1/auth/approle/role", query)

        if(response?.data?.keys) {
            response.data.keys.each {String appRole ->
                Map response2 = getJsonByUrlAndType(token, "/v1/auth/approle/role/${appRole}", null)
                appRoles << [appRole: appRole, policies: response2?.data?.policies?:[]]
            }
        }
        return appRoles
    }

    Map postApprole(String token, String appRole, List<String> policies) {
        policies << "defaultvaulttool"
        Map body = ["policies": policies.join(",")]
        return putJsonByUrlAndType(token,"/v1/auth/approle/role/${appRole}", body, null)
    }

    Map deleteApprole(String token, String appRole) {
        return deleteJsonByUrlAndType(token,"/v1/auth/approle/role/${appRole}", null, null)
    }

    List<String> listUserSecrets(String token) {
        Map query = ["list":true]
        Map response = getJsonByUrlAndType(token, "/v1/secret/${VAULTTOOLUSERSPATHNAME}/", query)

        return response?.data?.keys?:[]
    }

    User getUserSecret(String token, String key) {
        Map response = getJsonByUrlAndType(token, "/v1/secret/${VAULTTOOLUSERSPATHNAME}/$key", null)
        User user = new User()
        user.key = response?.data?.key?:null
        user.smsNumber  = response?.data?.smsNumber?:null

        return user
    }

    Map putUserSecret(String token, String key, User secret) {
        return putJsonByUrlAndType(token,"/v1/secret/${VAULTTOOLUSERSPATHNAME}/${key}", secret.asMap(), null)
    }

    Map deleteUserSecret(String token, String key) {
        return deleteJsonByUrlAndType(token,"/v1/secret/${VAULTTOOLUSERSPATHNAME}/${key}", null, null)
    }
}
