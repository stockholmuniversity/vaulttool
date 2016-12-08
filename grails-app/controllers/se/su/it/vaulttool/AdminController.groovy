package se.su.it.vaulttool

class AdminController {
    def vaultRestService

    def index() {

    }

    def user() {
        List<Map<String, User>> secretUsers = []
        def secrets = vaultRestService.listUserSecrets(session.token)
        secrets.removeAll {it.endsWith("/")}
        if(secrets) {
            secrets.each {String secret ->
                secretUsers << [secret: secret, userdata: vaultRestService.getUserSecret(session.token, secret)]
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
        Map response = vaultRestService.putUserSecret(session.token, key, entry)
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

    def policies() {
        List<Map<String,String>> policies = vaultRestService.getPolicies(session.token)

        [policies: policies]
    }

    def createPolicy() {
        String      policyName  = params?.name?:""
        String      policyPath  = params?.path?:""
        boolean     create      = params?.create?true:false
        boolean     read        = params?.read?true:false
        boolean     update      = params?.update?true:false
        boolean     delete      = params?.delete?true:false
        boolean     list        = params?.list?true:false

        if(policyName.empty) {
            String errorMsg = "Failed when trying to create/update policy. Error was: No name supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "policies")
            return
        }
        if(!create && !read && !update && !delete && !list) {
            String errorMsg = "Failed when trying to create/update policy. Error was: No capabilities selected."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "policies")
            return
        }

        if(policyPath && policyPath.toLowerCase().startsWith("secret/${VaultRestService.VAULTTOOLSECRETSPATHNAME}".toLowerCase())) {
            if(policyPath.toLowerCase().startsWith("secret/${VaultRestService.VAULTTOOLSECRETSPATHNAME}/".toLowerCase())) {
                policyPath = policyPath.substring("secret/${VaultRestService.VAULTTOOLSECRETSPATHNAME}/".length())
            } else {
                policyPath = policyPath.substring("secret/${VaultRestService.VAULTTOOLSECRETSPATHNAME}".length())
            }
        }

        Policy policy = new Policy(create: create, read: read, update: update, delete: delete, list: list)
        policy.setName(policyName)
        policy.setPath(policyPath)

        Map response = vaultRestService.putPolicy(session.token, policy)
        if(response) {
            String errorMsg = "Failed when trying to create/update policy ${policyName}. Error was: ${response.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            return redirect(action: "policies")
        }

        redirect(action: "policies")
    }

    def policiesAndAppRoles() {
        List<Map<String,String>> policies = vaultRestService.getPolicies(session.token)
        List<Map<String,List<String>>> appRoles = vaultRestService.getAppRoles(session.token)

        [policies: policies, approles: appRoles]
    }
}
