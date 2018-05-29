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
            session.eppn = "testl@su.se"
            session.displayname = "Testlisa Testsson"
            if(session.sudo) {
                session.group = session.sudo
            } else {
                session.group = "sysadmin"
            }
        } else {
            if (request.eppn) {
                session.eppn = request.eppn
            }
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
            if(session.sudo) {
                session.group = session.sudo
            }
            if(!session.groups) {
                def entitlements = request.getAttribute("entitlement")?request.getAttribute("entitlement").split(";"):null
                if(entitlements) {
                    def entitlementList = entitlements.findAll { String ent -> ent.toLowerCase().startsWith("urn:mace:swami.se:gmai:su-vaulttool:") }
                    if (!entitlementList || entitlementList.size() <= 0) {
                        log.error("User (${session.uid ?: "Unknown User"}) does not have the right entitlement!")
                        redirect(controller: "public", action: "index")
                        return false
                    }
                    entitlementList.each { String entitlement ->
                        def entParts = entitlement.split(":")
                        if (entParts.size() == 6) {
                            if (!session.groups) {
                                session.groups = []
                            }
                            session.groups << entParts[5]
                        }
                    }
                }
                if(!session.groups) {
                    log.error("User (${session.uid?:"Unknown User"}) does not have the right entitlement!")
                    redirect(controller: "public", action: "index")
                    return false
                }
            }
            if (!session.group && session.groups && session.groups.size() > 0) {
                session.group = session.groups[0]
            } else if(!session.group) {
                log.error("User (${session.uid?:"Unknown User"}) does not have the right entitlement!")
                redirect(controller: "public", action: "index")
                return false
            }
        }

        if(!session.eppn && request.getAttribute("REMOTE_USER")) {
            log.error("User (${session.uid?:"Unknown User"}) missing eppn, cant do second auth!")
            redirect(controller: "public", action: "index")
            return false
        }
        if(!session.eppn && !request.getAttribute("REMOTE_USER")) {
            log.error("User (${session.uid?:"Unknown User"}) missing eppn and REMOTE_USER, cant do second auth!")
            redirect(controller: "public", action: "index")
            return false
        }
        if(!session.secondauth && !session.secondauthkey && request.getAttribute("REMOTE_USER")) {
            User user = vaultRestService.getUserSecret(grailsApplication.config.vault.vaulttoken,session.eppn)
            String rootEppn = grailsApplication.config.vault.rooteppn?:null
            String rootSmsNumber = grailsApplication.config.vault.rootsms?:null
            if ((user && user.smsNumber && user.smsNumber.length() > 2) || (rootEppn && rootSmsNumber && rootSmsNumber.length() > 2 && rootEppn == session.eppn)) {
                session.secondauthkey = generator((('A'..'Z') + ('0'..'9')).join(), 5)
                String message = """This is the verification code to enter at Vaulttool.\n${session.secondauthkey}\n\nHave a nice day!"""
                Sms sms = new Sms(msg: message, rcpt: (user && user.smsNumber && user.smsNumber.length() > 2)?user.smsNumber:rootSmsNumber)
                utilityService.sendSms(sms)
                redirect(controller: "public", action: "secondauth")
                return false
            } else {
                log.error("User (${session.uid?:"Unknown User"}) does not have a registered cellphone number!")
                redirect(controller: "public", action: "index")
                return false
            }
        } else if(!session.secondauth && session.secondauthkey && request.getAttribute("REMOTE_USER")) {
            redirect(controller: "public", action: "secondauth")
            return false
        }
        if(!session.token) {
            //vaultRestService.enableApproleAuth(grailsApplication.config.vault.vaulttoken)
            if(session.group == "sysadmin" || session.group == grailsApplication.config.vault.sysadmdevgroup) {
                session.token = grailsApplication.config.vault.vaulttoken
            } else {
                String entitlementToken = vaultRestService.getEntitlementToken(grailsApplication.config.vault.vaulttoken, session.group)
                if (entitlementToken) {
                    session.token = entitlementToken
                }
            }
        }
        if(controllerName == "admin" && (session.group != "sysadmin" && session.group != grailsApplication.config.vault.sysadmdevgroup)) {
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
