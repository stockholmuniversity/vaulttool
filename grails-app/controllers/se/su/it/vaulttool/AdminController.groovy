package se.su.it.vaulttool

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AdminController {
    def vaultRestService

    def index() {
        List<Map<String,List<String>>> appRoles = vaultRestService.getAppRoles(session.token)
        [approles: appRoles]
    }

    def sudo() {
        String sudo = params.sudo?:""
        if(sudo.empty) {
            String errorMsg = "Failed when trying to sudo. Error was: No group supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        }
        session.sudo = sudo
        session.token = null
        redirect(controller: "dashboard", action: "index")
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
            redirect(action: "user")
            return
        }

        Map response = vaultRestService.deleteUserSecret(session.token, key)
        if(response) {
            String errorMsg = "Failed when trying to delete user ${key}. Error was: ${response.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            return redirect(action: "user")
        }
        UserData userData = UserData.findBySecretKey(key)
        userData.delete()
        flash.message = "Successfully deleted user ${key}"
        redirect(action: "user")
    }

    def policies() {
        List<String> paths = vaultRestService.getPaths(session.token)
        List<Map<String,String>> policies = vaultRestService.getPolicies(session.token)

        [policies: policies, paths: paths]
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

        policyPath += "*"

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
        flash.message = "Successfully created policy ${policyName}"

        redirect(action: "policies")
    }

    def deletePolicy() {
        String policy = params?.policy?:null
        if(!policy) {
            String errorMsg = "Failed when trying to delete policy. Error was: No policy name supplied.}"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "policies")
            return
        }

        Map response = vaultRestService.deletePolicy(session.token, policy)
        if(response) {
            String errorMsg = "Failed when trying to delete policy ${policy}. Error was: ${response.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            return redirect(action: "policies")
        }
        flash.message = "Successfully deleted policy ${policy}"

        redirect(action: "policies")
    }

    def approles() {
        List<Map<String,String>> policies = vaultRestService.getPolicies(session.token)
        List<Map<String,List<String>>> appRoles = vaultRestService.getAppRoles(session.token)

        [approles: appRoles, policies: policies]
    }

    def createApprole() {
        String          appRoleName  = params?.name?:""
        List<String>    policies  = []

        if(appRoleName.empty) {
            String errorMsg = "Failed when trying to create/update approle. Error was: No name supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "approles")
            return
        }
        if(params.policies && params.policies instanceof String[]) {
            policies = params.policies
        } else if(params.policies && params.policies instanceof String) {
            policies = [params.policies]
        }
        if(policies.size() <= 0) {
            String errorMsg = "Failed when trying to create/update approle. Error was: No policy supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "approles")
            return
        }
        Map response = vaultRestService.postApprole(session.token, appRoleName, policies)
        if(response) {
            String errorMsg = "Failed when trying to create/update approle ${appRoleName}. Error was: ${response.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            return redirect(action: "approles")
        }

        flash.message = "Successfully created approle ${appRoleName}"
        redirect(action: "approles")
    }

    def deleteApprole() {
        String approle = params?.approle?:null
        if(!approle) {
            String errorMsg = "Failed when trying to delete approle. Error was: No approle name supplied.}"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "approles")
            return
        }

        Map response = vaultRestService.deleteApprole(session.token, approle)
        if(response) {
            String errorMsg = "Failed when trying to delete approle ${approle}. Error was: ${response.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            return redirect(action: "approles")
        }
        flash.message = "Successfully deleted approle ${approle}"

        redirect(action: "approles")
    }

    def policiesAndAppRoles() {
        List<Map<String,String>> policies = vaultRestService.getPolicies(session.token)
        List<Map<String,List<String>>> appRoles = vaultRestService.getAppRoles(session.token)

        [policies: policies, approles: appRoles]
    }

    def export() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream()
            ZipOutputStream zos = new ZipOutputStream(baos)

            vaultRestService.getSecretTree(session.token, "").each { String secret ->
                def obj = vaultRestService.getSecret(session.token, secret)
                MetaData metaData = MetaData.findBySecretKey(obj.entry.key)
                if(obj?.entry?.key && !obj.entry.key.empty && metaData?.secretKey && !metaData.secretKey.empty && obj.entry.key == metaData.secretKey){
                    ZipEntry entry = new ZipEntry(VaultRestService.VAULTTOOLSECRETSPATHNAME+"/"+obj.entry.key + "/key.txt")
                    zos.putNextEntry(entry)
                    zos.write(obj.entry.key.getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(VaultRestService.VAULTTOOLSECRETSPATHNAME+"/"+obj.entry.key + "/username.txt")
                    zos.putNextEntry(entry)
                    zos.write(obj.entry.userName?obj.entry.userName.getBytes():"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(VaultRestService.VAULTTOOLSECRETSPATHNAME+"/"+obj.entry.key + "/password.txt")
                    zos.putNextEntry(entry)
                    zos.write(obj.entry.pwd?obj.entry.pwd.getBytes():"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(VaultRestService.VAULTTOOLSECRETSPATHNAME+"/"+obj.entry.key + "/binarydata")
                    zos.putNextEntry(entry)
                    zos.write(obj.entry.binaryData?:"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(VaultRestService.VAULTTOOLSECRETSPATHNAME+"/"+obj.entry.key + "/title.txt")
                    zos.putNextEntry(entry)
                    zos.write(metaData.title?metaData.title.getBytes():"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(VaultRestService.VAULTTOOLSECRETSPATHNAME+"/"+obj.entry.key + "/description.txt")
                    zos.putNextEntry(entry)
                    zos.write(metaData.description?metaData.description.getBytes():"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(VaultRestService.VAULTTOOLSECRETSPATHNAME+"/"+obj.entry.key + "/filename.txt")
                    zos.putNextEntry(entry)
                    zos.write(metaData.fileName?metaData.fileName.getBytes():"".getBytes())
                    zos.closeEntry()
                }
            }
            vaultRestService.listUserSecrets(session.token).each{String secret ->
                User user = vaultRestService.getUserSecret(session.token, secret)
                if(user && user.key && !user.key.empty) {
                    ZipEntry entry = new ZipEntry(VaultRestService.VAULTTOOLUSERSPATHNAME+"/"+user.key + "/key.txt")
                    zos.putNextEntry(entry)
                    zos.write(user.key.getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(VaultRestService.VAULTTOOLUSERSPATHNAME+"/"+user.key + "/smsnumber.txt")
                    zos.putNextEntry(entry)
                    zos.write(user.smsNumber?user.smsNumber.getBytes():"".getBytes())
                    zos.closeEntry()
                }
            }

            zos.close()
            response.setContentType("application/zip")
            response.setHeader("Content-disposition", "filename=vaulttool-export.zip")
            response.setContentLength(baos.toByteArray().size())
            response.outputStream << baos.toByteArray()
            baos.close()
            return
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        response.setStatus(500)
    }
}
