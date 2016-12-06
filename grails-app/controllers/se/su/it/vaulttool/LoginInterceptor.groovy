package se.su.it.vaulttool

import grails.util.Environment


class LoginInterceptor {
    int order = 30
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
            if (session.group) {

            } else if (request.getAttribute("entitlement")) {
                def entitlements = request.getAttribute("entitlement").split(";")
                def entitlement = entitlements.find { String ent -> ent.toLowerCase().startsWith("urn:mace:swami.se:gmai:su-vaulttool:") }
                if (!entitlement) {
                    redirect(controller: "public", action: "index")
                    return false
                }
                def entParts = entitlement.split(":")
                if (entParts.size() == 6) {
                    session.group = entParts[5]
                } else {
                    redirect(controller: "public", action: "index")
                    return false
                }
            } else {
                redirect(controller: "public", action: "index")
                return false
            }
        }
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
}
