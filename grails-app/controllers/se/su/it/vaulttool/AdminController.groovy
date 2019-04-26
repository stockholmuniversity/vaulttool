package se.su.it.vaulttool

import org.springframework.web.multipart.MultipartFile

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class AdminController {
    def vaultRestService

    def test() {
        vaultRestService.getTokenInfo(session.token)
        render("hej")
    }

    def index() {
        List<Map<String,List<String>>> appRoles = vaultRestService.getAppRoles(session.token)
        if(request.xhr){
            return render(template: 'admin', model: [approles: appRoles])
        }
        [approles: appRoles]
    }

    def sudo() {
        String sudo = params.sudo?:""
        if(sudo.empty) {
            String errorMsg = "Failed when trying to sudo. Error was: No group supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            
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

        if(request.xhr){
            return render(template: 'user', model: [secrets: secretUsers.sort{it.secret}])
        }

        [secrets: secretUsers.sort{it.secret}]
    }

    def createUser() {
        String eppn = params.eppn?:""
        String sms  = params.sms?:""
        if(eppn.empty) {
            String errorMsg = "Failed when trying to create/update user. Error was: No EPPN supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "user")
            return
        }
        if(sms.empty) {
            String errorMsg = "Failed when trying to create/update user. Error was: No Cellphone Number supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "user")
            return
        }
        String key = "${eppn}"
        User entry = new User()
        entry.key       = key
        entry.smsNumber = sms
        Map resp = vaultRestService.putUserSecret(session.token, key, entry)
        if(resp) {
            String errorMsg = "Failed when trying to create/update user ${key}. Error was: ${resp.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            return redirect(action: "user")
        }
        UserData userData   = UserData.findOrCreateByEppn(eppn)
        userData.secretKey  = key
        userData.eppn       = eppn

        userData.save(flush: true)
        flash.message = "Successfully created/updated user ${key}"
        redirect(action: "user")
    }

    def deleteUser() {
        String key = params?.key?:null
        if(!key) {
            String errorMsg = "Failed when trying to delete user. Error was: No secret supplied.}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "user")
            return
        }

        Map resp = vaultRestService.deleteUserSecret(session.token, key)
        if(resp) {
            String errorMsg = "Failed when trying to delete user ${key}. Error was: ${resp.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            return redirect(action: "user")
        }
        UserData userData = UserData.findBySecretKey(key)
        userData.delete(flush: true)
        flash.message = "Successfully deleted user ${key}"
        redirect(action: "user")
    }

    def policies() {
        List<String> paths = vaultRestService.getPaths(session.token)
        List<Map<String,String>> policies = vaultRestService.getPolicies(session.token)

        if(request.xhr){
            return render(template: 'policies', model: [policies: policies, paths: paths])
        }

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
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "policies")
            return
        }
        if(!create && !read && !update && !delete && !list) {
            String errorMsg = "Failed when trying to create/update policy. Error was: No capabilities selected."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "policies")
            return
        }

        policyPath += "*"

        Policy policy = new Policy(create: create, read: read, update: update, delete: delete, list: list)
        policy.setName(policyName)
        policy.setPath(policyPath)

        Map resp = vaultRestService.putPolicy(session.token, policy)
        if(resp) {
            String errorMsg = "Failed when trying to create/update policy ${policyName}. Error was: ${resp.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
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

        Map resp = vaultRestService.deletePolicy(session.token, policy)
        if(resp) {
            String errorMsg = "Failed when trying to delete policy ${policy}. Error was: ${resp.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            return redirect(action: "policies")
        }
        flash.message = "Successfully deleted policy ${policy}"

        redirect(action: "policies")
    }

    def approles() {
        List<Map<String,String>> policies = vaultRestService.getPolicies(session.token)
        List<Map<String,List<String>>> appRoles = vaultRestService.getAppRoles(session.token)

        if(request.xhr){
            return render(template: 'approles', model: [approles: appRoles, policies: policies])
        }

        [approles: appRoles, policies: policies]
    }

    def createApprole() {
        String          appRoleName  = params?.name?:""
        List<String>    policies  = []

        if(appRoleName.empty) {
            String errorMsg = "Failed when trying to create/update approle. Error was: No name supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
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
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "approles")
            return
        }
        Map resp = vaultRestService.postApprole(session.token, appRoleName, policies)
        if(resp) {
            String errorMsg = "Failed when trying to create/update approle ${appRoleName}. Error was: ${resp.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
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
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "approles")
            return
        }

        Map resp = vaultRestService.deleteApprole(session.token, approle)
        if(resp) {
            String errorMsg = "Failed when trying to delete approle ${approle}. Error was: ${resp.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
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
            ZipOutputStream zos     = new ZipOutputStream(baos)
            String creationDateTime = new Date().format("yyyyMMdd_hhmmss")
            String zipFileName      = "vaulttool-export_" + creationDateTime + ".zip"
            String secretsPath      = "vaulttool-export_" + creationDateTime + "/" + VaultRestService.VAULTTOOLSECRETSPATHNAME + "/"
            String usersPath        = "vaulttool-export_" + creationDateTime + "/" + VaultRestService.VAULTTOOLUSERSPATHNAME + "/"
            String policiesPath     = "vaulttool-export_" + creationDateTime + "/" + VaultRestService.VAULTTOOLPOLICIESPATHNAME + "/"
            String appRolesPath        = "vaulttool-export_" + creationDateTime + "/" + VaultRestService.VAULTTOOLAPPROLESPATHNAME + "/"


            vaultRestService.getSecretTree(session.token, "").each { String secret ->
                def obj = vaultRestService.getSecret(session.token, secret)
                MetaData metaData = MetaData.findBySecretKey(obj.entry.key)
                if(obj?.entry?.key && !obj.entry.key.empty && metaData?.secretKey && !metaData.secretKey.empty && obj.entry.key == metaData.secretKey){
                    ZipEntry entry = new ZipEntry(secretsPath+obj.entry.key + "/key.txt")
                    zos.putNextEntry(entry)
                    zos.write(obj.entry.key.getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(secretsPath+obj.entry.key + "/username.txt")
                    zos.putNextEntry(entry)
                    zos.write(obj.entry.userName?obj.entry.userName.getBytes():"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(secretsPath+obj.entry.key + "/password.txt")
                    zos.putNextEntry(entry)
                    zos.write(obj.entry.pwd?obj.entry.pwd.getBytes():"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(secretsPath+obj.entry.key + "/binarydata")
                    zos.putNextEntry(entry)
                    zos.write(obj.entry.binaryData?:"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(secretsPath+obj.entry.key + "/${metaData.fileName}")
                    zos.putNextEntry(entry)
                    zos.write(obj.entry.binaryData?:"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(secretsPath+obj.entry.key + "/title.txt")
                    zos.putNextEntry(entry)
                    zos.write(metaData.title?metaData.title.getBytes():"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(secretsPath+obj.entry.key + "/description.txt")
                    zos.putNextEntry(entry)
                    zos.write(metaData.description?metaData.description.getBytes():"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(secretsPath+obj.entry.key + "/filename.txt")
                    zos.putNextEntry(entry)
                    zos.write(metaData.fileName?metaData.fileName.getBytes():"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(secretsPath+obj.entry.key + "/updatedby.txt")
                    zos.putNextEntry(entry)
                    zos.write(metaData.updatedBy?metaData.updatedBy.getBytes():"".getBytes())
                    zos.closeEntry()
                }
            }
            vaultRestService.listUserSecrets(session.token).each{String secret ->
                User user = vaultRestService.getUserSecret(session.token, secret)
                UserData userData = UserData.findBySecretKey(secret)
                if(user && user.key && !user.key.empty) {
                    ZipEntry entry = new ZipEntry(usersPath+user.key + "/key.txt")
                    zos.putNextEntry(entry)
                    zos.write(user.key.getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(usersPath+user.key + "/smsnumber.txt")
                    zos.putNextEntry(entry)
                    zos.write(user.smsNumber?user.smsNumber.getBytes():"".getBytes())
                    zos.closeEntry()
                    entry = new ZipEntry(usersPath+user.key + "/eppn.txt")
                    zos.putNextEntry(entry)
                    zos.write(userData.eppn?userData.eppn.getBytes():"".getBytes())
                    zos.closeEntry()
                }
            }
            vaultRestService.getPolicies(session.token).each{Map<String,String> policy ->
                ZipEntry entry = new ZipEntry(policiesPath + policy.policy + "/policy.txt")
                zos.putNextEntry(entry)
                zos.write(policy.policy.getBytes())
                zos.closeEntry()
                entry = new ZipEntry(policiesPath + policy.policy + "/rules.txt")
                zos.putNextEntry(entry)
                zos.write(policy.rules.getBytes())
                zos.closeEntry()
            }
            vaultRestService.getAppRoles(session.token).each{Map<String,List<String>> appRole ->
                ZipEntry entry = new ZipEntry(appRolesPath + appRole.appRole + "/approle.txt")
                zos.putNextEntry(entry)
                zos.write(appRole.appRole.getBytes())
                zos.closeEntry()
                appRole.policies.eachWithIndex{ String policy, int i ->
                    entry = new ZipEntry(appRolesPath + appRole.appRole + "/policy${i}.txt")
                    zos.putNextEntry(entry)
                    zos.write(policy.getBytes())
                    zos.closeEntry()
                }
            }

            zos.close()
            response.setContentType("application/zip")
            response.setHeader("Content-disposition", "filename=${zipFileName}")
            response.setContentLength(baos.toByteArray().size())
            response.outputStream << baos.toByteArray()
            baos.close()
            return
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        response.setStatus(500)
    }

    def importZip() {
        MultipartFile f = request.getFile('importZipInputFileId')
        if (f.empty) {
            String errorMsg = "Failed when trying to import zip-file. Error was: File not found in request."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(actionName: "index")
            return
        }
        String secretsPath  = VaultRestService.VAULTTOOLSECRETSPATHNAME + "/"
        String usersPath    = VaultRestService.VAULTTOOLUSERSPATHNAME + "/"
        String policiesPath = VaultRestService.VAULTTOOLPOLICIESPATHNAME + "/"
        String appRolesPath = VaultRestService.VAULTTOOLAPPROLESPATHNAME + "/"

        List<ImportSecretHelper> secretList     = []
        List<ImportUserHelper> userList         = []
        List<ImportPolicyHelper> policyList     = []
        List<ImportAppRoleHelper> appRoleList   = []

        ZipInputStream zipStream = new ZipInputStream(f.inputStream)
        if(zipStream) {
            ZipEntry zipEntry
            while (zipEntry = zipStream.nextEntry) {
                if(zipEntry.isDirectory()) {
                    //create secret path
                } else {
                    if(zipEntry.getName().contains(secretsPath)) { //secrets
                        String keyWithFile = zipEntry.getName().substring(zipEntry.getName().indexOf(VaultRestService.VAULTTOOLSECRETSPATHNAME)).replace(secretsPath, "")
                        if(keyWithFile.lastIndexOf("/") > -1) {
                            String secretKey = keyWithFile.substring(0, keyWithFile.lastIndexOf("/"))
                            String file = keyWithFile.substring(keyWithFile.lastIndexOf("/") + 1)
                            if(["key.txt", "username.txt", "password.txt", "binarydata", "title.txt", "description.txt", "filename.txt", "updatedby.txt"].any{keyWithFile.contains(it)}) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream()
                                byte[] buffer = new byte[1024]
                                int n
                                while ((n = zipStream.read(buffer, 0, 1024)) > -1) {
                                    out.write(buffer, 0, n);
                                }
                                ImportSecretHelper importSecretHelper = secretList.find { it.entry.key == secretKey }
                                if (!importSecretHelper) {
                                    importSecretHelper = new ImportSecretHelper(entry: new Entry(key: secretKey),
                                            metaData: new MetaData(secretKey: secretKey))
                                    secretList << importSecretHelper
                                }
                                switch (file) {
                                    case "username.txt": importSecretHelper.entry.userName = out.toString()
                                        break
                                    case "password.txt": importSecretHelper.entry.pwd = out.toString()
                                        break
                                    case "binarydata": importSecretHelper.entry.binaryData = out.toByteArray()
                                        break
                                    case "title.txt": importSecretHelper.metaData.title = out.toString()
                                        break
                                    case "updatedby.txt": importSecretHelper.metaData.updatedBy = out.toString()
                                        break
                                    case "description.txt": importSecretHelper.metaData.description = out.toString()
                                        break
                                    case "filename.txt": importSecretHelper.metaData.fileName = out.toString()
                                        break
                                }
                                out.close()
                            }
                        }
                    } else if(zipEntry.getName().contains(usersPath)) {// Users
                        String keyWithFile = zipEntry.getName().substring(zipEntry.getName().indexOf(VaultRestService.VAULTTOOLUSERSPATHNAME)).replace(usersPath, "")
                        if(keyWithFile.lastIndexOf("/") > -1) {
                            String secretKey = keyWithFile.substring(0, keyWithFile.lastIndexOf("/"))
                            String file = keyWithFile.substring(keyWithFile.lastIndexOf("/") + 1)
                            if(["key.txt", "smsnumber.txt", "eppn.txt"].any{keyWithFile.contains(it)}) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream()
                                byte[] buffer = new byte[1024]
                                int n
                                while ((n = zipStream.read(buffer, 0, 1024)) > -1) {
                                    out.write(buffer, 0, n);
                                }
                                ImportUserHelper importUserHelper = userList.find { it.userData.secretKey == secretKey }
                                if (!importUserHelper) {
                                    importUserHelper = new ImportUserHelper(user: new User(key: secretKey),
                                            userData: new UserData(secretKey: secretKey))
                                    userList << importUserHelper
                                }
                                switch (file) {
                                    case "smsnumber.txt": importUserHelper.user.smsNumber = out.toString()
                                        break
                                    case "eppn.txt": importUserHelper.userData.eppn = out.toString()
                                        break
                                }
                                out.close()
                            }
                        }
                    } else if(zipEntry.getName().contains(policiesPath)) {// Policies
                        String keyWithFile = zipEntry.getName().substring(zipEntry.getName().indexOf(VaultRestService.VAULTTOOLPOLICIESPATHNAME)).replace(policiesPath, "")
                        if(keyWithFile.lastIndexOf("/") > -1) {
                            String policy = keyWithFile.substring(0, keyWithFile.lastIndexOf("/"))
                            String file = keyWithFile.substring(keyWithFile.lastIndexOf("/") + 1)
                            if(["policy.txt", "rules.txt"].any{keyWithFile.contains(it)}) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream()
                                byte[] buffer = new byte[1024]
                                int n
                                while ((n = zipStream.read(buffer, 0, 1024)) > -1) {
                                    out.write(buffer, 0, n);
                                }
                                ImportPolicyHelper importPolicyHelper = policyList.find { it.policy == policy }
                                if (!importPolicyHelper) {
                                    importPolicyHelper = new ImportPolicyHelper(policy: policy)
                                    policyList << importPolicyHelper
                                }
                                switch (file) {
                                    case "policy.txt": importPolicyHelper.policy = out.toString()
                                        break
                                    case "rules.txt": importPolicyHelper.rules = out.toString()
                                        break
                                }
                                out.close()
                            }
                        }
                    } else if(zipEntry.getName().contains(appRolesPath)) {// AppRoles
                        String keyWithFile = zipEntry.getName().substring(zipEntry.getName().indexOf(VaultRestService.VAULTTOOLAPPROLESPATHNAME)).replace(appRolesPath, "")
                        if(keyWithFile.lastIndexOf("/") > -1) {
                            String appRole = keyWithFile.substring(0, keyWithFile.lastIndexOf("/"))
                            String file = keyWithFile.substring(keyWithFile.lastIndexOf("/") + 1)
                            if(["approle.txt", "policy"].any{keyWithFile.contains(it)}) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream()
                                byte[] buffer = new byte[1024]
                                int n
                                while ((n = zipStream.read(buffer, 0, 1024)) > -1) {
                                    out.write(buffer, 0, n);
                                }
                                ImportAppRoleHelper importAppRoleHelper = appRoleList.find { it.appRole == appRole }
                                if (!importAppRoleHelper) {
                                    importAppRoleHelper = new ImportAppRoleHelper(appRole: appRole)
                                    appRoleList << importAppRoleHelper
                                }
                                switch (file) {
                                    case "approle.txt": importAppRoleHelper.appRole = out.toString()
                                        break
                                    case {String fileName -> fileName.startsWith("policy")}: importAppRoleHelper.policies << out.toString()
                                        break
                                }
                                out.close()
                            }
                        }
                    }

                }
                zipStream.closeEntry()
            }
            zipStream.close()
        }
        secretList.each{ImportSecretHelper ish ->
            vaultRestService.putSecret(session.token, ish.entry.key, ish.entry)
            MetaData metaData = MetaData.findOrCreateBySecretKey(ish.entry.key)
            metaData.secretKey      = ish.entry.key
            metaData.title          = ish.metaData.title
            metaData.description    = ish.metaData.description
            metaData.fileName       = ish.metaData.fileName
            metaData.updatedBy      = ish.metaData.updatedBy
            metaData.save(flush: true)
        }
        userList.each{ImportUserHelper iuh ->
            vaultRestService.putUserSecret(session.token, iuh.user.key, iuh.user)
            UserData userData = UserData.findOrCreateBySecretKey(iuh.user.key)
            userData.secretKey  = iuh.user.key
            userData.eppn       = iuh.userData.eppn
            userData.save(flush: true)
        }
        policyList.each{ImportPolicyHelper iph ->
            Map theMap = ["rules": iph.rules]
            vaultRestService.putPolicy(session.token, iph.policy, theMap)
        }
        appRoleList.each{ImportAppRoleHelper iarh ->
            vaultRestService.postApprole(session.token, iarh.appRole, iarh.policies)
        }
        redirect(action: "index")
    }
}
