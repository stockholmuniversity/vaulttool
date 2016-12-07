package se.su.it.vaulttool

class AdminController {
    def vaultRestService

    def index() {

    }

    def user() {
        List<Map<String, User>> secretUsers = []
        def secrets = vaultRestService.listUserSecrets(grailsApplication.config.vault.nekottoor)
        secrets.removeAll {it.endsWith("/")}
        if(secrets) {
            secrets.each {String secret ->
                secretUsers << [secret: secret, userdata: vaultRestService.getUserSecret(grailsApplication.config.vault.nekottoor, secret)]
            }
        }
        [secrets: secretUsers]
    }

    def createUser() {
        String eppn = params.eppn?:""
        String sms  = params.sms?:""
        if(eppn.empty) {
            String errorMsg = "Failed when trying to create/update user. Error was: No EPPN supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "user")
            return
        }
        if(sms.empty) {
            String errorMsg = "Failed when trying to create/update user. Error was: No Cellphone Number supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "user")
            return
        }
        String key = "${eppn}"
        User entry = new User()
        entry.key       = key
        entry.smsNumber = sms
        Map response = vaultRestService.putUserSecret(grailsApplication.config.vault.nekottoor, key, entry)
        if(response) {
            String errorMsg = "Failed when trying to create/update user ${key}. Error was: ${response.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            return redirect(action: "user")
        }
        UserData userData   = UserData.findOrCreateByEppn(eppn)
        userData.secretKey  = key
        userData.eppn       = eppn

        userData.save()
        flash.message = "Successfully created/updated user ${key}"
        redirect(action: "user")
    }

    def deleteUser() {
        String key = params?.key?:null
        if(!key) {
            String errorMsg = "Failed when trying to delete user. Error was: No secret supplied.}"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
            return
        }

        Map response = vaultRestService.deleteUserSecret(session.token, key)
        if(response) {
            String errorMsg = "Failed when trying to delete user ${key}. Error was: ${response.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            return redirect(action: "secret", params: [key: key])
        }
        UserData userData = UserData.findBySecretKey(key)
        userData.delete()
        flash.message = "Successfully deleted user ${key}"
        redirect(action: "user")
    }

    def policiesAndAppRoles() {
        List<Map<String,String>> policies = vaultRestService.getPolicies(session.token)
        List<Map<String,List<String>>> appRoles = vaultRestService.getAppRoles(session.token)

        [policies: policies, approles: appRoles]
    }
}
