package se.su.it.vaulttool

class AdminController {
    def vaultRestService

    def index() {
        List<Map<String,String>> policies = vaultRestService.getPolicies(session.token)
        List<Map<String,List<String>>> appRoles = vaultRestService.getAppRoles(session.token)

        [policies: policies, approles: appRoles]
    }
}
