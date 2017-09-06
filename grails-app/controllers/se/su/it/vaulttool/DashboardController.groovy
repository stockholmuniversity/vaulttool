package se.su.it.vaulttool

import org.springframework.web.multipart.MultipartFile

class DashboardController {
    def vaultRestService
    def utilityService

    def index() {
        String selectedPath = params?.selectedPath?:""

        def paths = vaultRestService.getPaths(session.token)
        List<Map<String, MetaData>> secretMetaData = []
        def secrets = vaultRestService.listSecrets(session.token, selectedPath)

        secrets.removeAll {it.endsWith("/")}
        if(secrets) {
            secrets.each {String secret ->
                secretMetaData << [secret: secret, metadata: MetaData.findBySecretKey(selectedPath+secret)]
            }
        }
        def capabilities = vaultRestService.getCapabilities(session.token, selectedPath)
        [selectedPath: selectedPath, capabilities: capabilities, paths: paths, secrets: secretMetaData]
    }
    def search() {
        String secret = params?.secret?:""
        if(secret.empty) {
            redirect(actionName: "index")
            return
        }
        def keyTree = vaultRestService.getSecretTree(session.token)

        List<MetaData> metaDatas = MetaData.findAllBySecretKeyInList(keyTree).findAll{it.secretKey.contains(secret) || it.title.contains(secret) || it.description.contains(secret)}
        [metadatas: metaDatas]

    }

    def secret() {
        String key = params.key?:null
        if(!key) {
            String errorMsg = "Failed when trying to read secret. Error was: No key supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        }

        Expando response = vaultRestService.getSecret(session.token, key)
        if(response.status) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: ${response.status}"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        } else if(!response.entry) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        }

        MetaData metaData = MetaData.findBySecretKey(key)
        [secret: response.entry, metadata: metaData]
    }

    def updateSecret() {
        String key = params?.key?:null
        if(!key) {
            String errorMsg = "Failed when trying to update secret. Error was: No key supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
            return
        }
        Expando response = vaultRestService.getSecret(session.token, key)
        if(response.status) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: ${response.status}"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        } else if(!response.entry) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        }
        MetaData metaData = MetaData.findOrCreateBySecretKey(key)
        metaData.secretKey      = key
        metaData.title          = params?.title?:""
        metaData.description    = params?.description?:""

        Entry entry = response.entry
        entry.key           = key
        entry.userName      = params?.userName?:""
        entry.pwd           = params?.password?:""

        Map response2 = vaultRestService.putSecret(session.token, key, entry)
        if(response2) {
            String errorMsg = "Failed when trying to update secret ${key}. Error was: ${response2.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
        } else {
            flash.message = "Successfully updated secret ${key}"
            metaData.save()
        }
        return redirect(action: "secret", params: [key: key])
    }

    def createSecret() {
        String key = params?.selectedPath?:""
        String path = params?.path?:""
        String secret = params?.secret?:""
        if(secret.empty) {
            String errorMsg = "Failed when trying to create secret. Error was: No key supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
            return
        }
        if(path.length() > 0) {
            key += path  + "/"  + secret
        } else {
            key += secret
        }
        def secretTree = vaultRestService.getSecretTree(session.token)
        if(secretTree.contains(key)) {
            String errorMsg = "Failed when trying to create secret ${key}. Error was: Secret already exist"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
            return
        }
        Entry entry = new Entry()
        entry.key = key
        Map response = vaultRestService.putSecret(session.token, key, entry)
        if(response) {
            String errorMsg = "Failed when trying to create secret ${key}. Error was: ${response.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            return redirect(action: "index")
        }
        MetaData metaData = new MetaData()
        metaData.secretKey = key
        metaData.title = ""
        metaData.description = ""
        metaData.fileName = ""
        metaData.save()
        flash.message = "Successfully created secret ${key}"
        return redirect(action: "secret", params: [key: key])
    }

    def delete() {
        String key = params?.key?:null
        if(!key) {
            String errorMsg = "Failed when trying to delete secret. Error was: No secret supplied.}"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
            return
        }

        Map response = vaultRestService.deleteSecret(session.token, key)
        if(response) {
            String errorMsg = "Failed when trying to delete secret ${key}. Error was: ${response.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            return redirect(action: "secret", params: [key: key])
        }
        MetaData metaData = MetaData.findBySecretKey(key)
        metaData.delete()
        flash.message = "Successfully deleted secret ${key}"
        redirect(actionName: "index")
    }

    def upload() {
        String key = params?.key?:null
        if(!key) {
            String errorMsg = "Failed when trying to upload file. Error was: No secret supplied"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
            return
        }

        Expando response = vaultRestService.getSecret(session.token, key)
        if(response.status) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: ${response.status}"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        } else if(!response.entry) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        }

        MultipartFile f = request.getFile('attachment')
        if (f.empty) {
            String errorMsg = "Failed when trying to upload file for secret ${key}. Error was: File not found in request."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
            return
        }

        Entry entry = response.entry
        entry.binaryData = f.bytes
        Map response2 = vaultRestService.putSecret(session.token, key, entry)
        if(response2) {
            String errorMsg = "Failed when trying to upload file for secret ${key}. Error was: ${response2.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
        } else {
            MetaData metaData = MetaData.findBySecretKey(key)
            metaData.fileName = f.originalFilename
            flash.message = "Successfully uploaded file to secret ${key}"
        }
        return redirect(action: "secret", params: [key: key])
    }

    def download() {
        String key = params?.key?:null
        if(!key) {
            String errorMsg = "Failed when trying to download file. Error was: No secret supplied"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
            return
        }

        Expando response2 = vaultRestService.getSecret(session.token, key)
        if(response2.status) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: ${response2.status}"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        } else if(!response2.entry) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        }
        Entry entry = response2.entry
        MetaData metaData = MetaData.findBySecretKey(key)
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "filename=\"${metaData.fileName}\"")
        response.setContentLength(entry.binaryData.size())
        response.outputStream << entry.binaryData
    }

    def deleteFile() {
        String key = params?.key?:null
        if(!key) {
            String errorMsg = "Failed when trying to delete file. Error was: No secret supplied"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
            return
        }

        Expando response = vaultRestService.getSecret(session.token, key)
        if(response.status) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: ${response.status}"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        } else if(!response.entry) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        }
        MetaData metaData = MetaData.findBySecretKey(key)
        if(!metaData) {
            String errorMsg = "Failed when trying to read metadata for secret ${key}. Error was: metadata not found."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        }
        metaData.secretKey      = key
        metaData.fileName       = ""

        Entry entry = response.entry
        entry.key           = key
        entry.binaryData    = "".getBytes()

        Map response2 = vaultRestService.putSecret(session.token, key, entry)
        if(response2) {
            String errorMsg = "Failed when trying to update secret ${key}. Error was: ${response2.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
        } else {
            flash.message = "Successfully updated secret ${key}"
            metaData.save()
        }
        return redirect(action: "secret", params: [key: key])
    }
}
