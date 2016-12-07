package se.su.it.vaulttool

import grails.util.Environment


class LoginInterceptor {
    int order = 30
    def utilityService
    def vaultRestService

    LoginInterceptor() {
        matchAll().excludes(controller: "public")
    }

    boolean before() {
        if (Environment.current == Environment.DEVELOPMENT && !request.getAttribute("REMOTE_USER")) {
            session.uid = "testl"
            session.displayname = "Testlisa Testsson"
            session.group = "sysadmin"
        } else {
            if (request.getAttribute("REMOTE_USER")) {
                session.uid = request.getAttribute("REMOTE_USER")
            } else if (request.eppn) {
                session.uid = (request.eppn.contains('@')) ? request.eppn.substring(0, request.eppn.indexOf('@')) : request.eppn
            }
            if (request.getAttribute("displayName")) {
                session.displayname = request.getAttribute("displayName")
            }
            if (request.getAttribute("mail")) {
                session.email = request.getAttribute("mail")
            }
            if (session.group) {

            } else if (request.getAttribute("entitlement")) {
                def entitlements = request.getAttribute("entitlement").split(";")
                def entitlement = entitlements.find { String ent -> ent.toLowerCase().startsWith("urn:mace:swami.se:gmai:su-vaulttool:") }
                if (!entitlement) {
                    log.error("User (${session.uid?:"Unknown User"}) does not have the right entitlement!")
                    redirect(controller: "public", action: "index")
                    return false
                }
                def entParts = entitlement.split(":")
                if (entParts.size() == 6) {
                    session.group = entParts[5]
                } else {
                    log.error("User (${session.uid?:"Unknown User"}) does not have the right entitlement!")
                    redirect(controller: "public", action: "index")
                    return false
                }
            } else {
                log.error("User (${session.uid?:"Unknown User"}) does not have the right entitlement!")
                redirect(controller: "public", action: "index")
                return false
            }
        }

        /*if(!session.email && request.getAttribute("REMOTE_USER")) {
            log.error("User (${session.uid?:"Unknown User"}) missing email, cant do second auth!")
            redirect(controller: "public", action: "index")
            return false
        }
        if(!session.secondauth && !session.secondauthkey && request.getAttribute("REMOTE_USER")) {
            session.secondauthkey = generator( (('A'..'Z')+('0'..'9')).join(), 5 )
            String message = """This is the verification code to enter at Vaulttool.\n${session.secondauthkey}\n\nHave a nice day!"""
            utilityService.sendEmail("Vaulttool verification code", message, session.email)
            redirect(controller: "public", action: "secondauth")
            return false
        } else if(!session.secondauth && session.secondauthkey && request.getAttribute("REMOTE_USER")) {
            redirect(controller: "public", action: "secondauth")
            return false
        }*/
        if(!session.token) {
            if(session.group == "sysadmin") {
                session.token = grailsApplication.config.vault.nekottoor
            } else {
                String entitlementToken = vaultRestService.getEntitlementToken(grailsApplication.config.vault.nekottoor, session.group)
                if (entitlementToken) {
                    session.token = entitlementToken
                }
            }
        }
        if(controllerName == "admin" && session.group != "sysadmin") {
            redirect(controller: "public", action: "index")
            return false
        }
        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }

    private generator = { String alphabet, int n ->
        new Random().with {
            (1..n).collect { alphabet[ nextInt( alphabet.length() ) ] }.join()
        }
    }
}
