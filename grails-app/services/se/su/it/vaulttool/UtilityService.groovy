package se.su.it.vaulttool

import groovyx.net.http.HTTPBuilder
import org.apache.commons.mail.SimpleEmail

class UtilityService {
    def grailsApplication
    private HTTPBuilder http = null

    private synchronized HTTPBuilder getRestClient() {
        if (!http) {
            http = new HTTPBuilder(grailsApplication.config.suservicemix.restUrl)
        }
        return http
    }

    private Map doRest(String method, String restPath, String mediaType, Map theBody = [:], Map query = [:]) {
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
                response.'404' = { resp ->
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

    private Map getJsonByUrlAndType(String url, Map query = [:]) {
        String jsonUtf8Type = "application/json; charset=UTF-8"
        return doRest("GET", url, jsonUtf8Type, null, query)
    }

    private Map putJsonByUrlAndType(String url, Map body = [:], Map query = [:]) {
        String jsonUtf8Type = "application/json; charset=UTF-8"
        return doRest("PUT", url, jsonUtf8Type, body, query)
    }

    private Map postJsonByUrlAndType(String url, Map body = [:], Map query = [:]) {
        String jsonUtf8Type = "application/json; charset=UTF-8"
        return doRest("POST", url, jsonUtf8Type, body, query)
    }

    private Map deleteJsonByUrlAndType(String url, Map body = [:], Map query = [:]) {
        String jsonUtf8Type = "application/json; charset=UTF-8"
        return doRest("DELETE", url, jsonUtf8Type, body, query)
    }

    def sendSms(Sms sms) {
        Map response = postJsonByUrlAndType("/cxf/sms", sms.asMap(), null)
    }

    def sendEmail(String subject, String msg, String to) {
        SimpleEmail email = new SimpleEmail()
        email.setHostName("smtp.su.se")
        email.addTo(to)
        email.setFrom("noreply@su.se")
        email.setSubject(subject)
        email.setMsg(msg)
        //email.setAuthentication(username,password)
        //email.setSmtpPort(port)
        //email.send()
    }
}
