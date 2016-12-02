package se.su.it.vaulttool

import org.springframework.web.multipart.MultipartFile

class DashboardController {
    def vaultRestService

    def index() {
        String selectedPath = params?.selectedPath?:""

        def paths = vaultRestService.getPaths(session.token)
        def secrets = vaultRestService.listSecrets(session.token, selectedPath)
        secrets.removeAll {it.endsWith("/")}

        [selectedPath: selectedPath, paths: paths, secrets: secrets]
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

        Entry response = vaultRestService.getSecret(session.token, key)
        if(response && !response.key) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: 'Unknown Error'"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
        } else if(!response) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
        }

        [secret: response]
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

        Entry entry = vaultRestService.getSecret(session.token, key)
        if(!entry) {
            String errorMsg = "Failed when trying to update secret ${key}. Error was: Secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
            return
        }

        entry.key           = key
        entry.title         = params?.title?:""
        entry.description   = params?.description?:""
        entry.pwd           = params?.password?:""

        Map response = vaultRestService.putSecret(session.token, key, entry)
        if(response) {
            String errorMsg = "Failed when trying to update secret ${key}. Error was: ${response.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
        } else {
            flash.message = "Successfully updated secret ${key}"
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
        flash.message = "Successfully deletet secret ${key}"
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

        Entry entry = vaultRestService.getSecret(session.token, key)
        if(!entry) {
            String errorMsg = "Failed when trying to upload file for secret ${key}. Error was: Secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
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

        entry.binaryData = f.bytes
        entry.fileName = f.originalFilename
        Map response = vaultRestService.putSecret(session.token, key, entry)
        if(response) {
            String errorMsg = "Failed when trying to upload file for secret ${key}. Error was: ${response.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
        } else {
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

        Entry entry = vaultRestService.getSecret(session.token, key)
        if(!entry) {
            String errorMsg = "Failed when trying to download file for secret ${key}. Error was: Secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(actionName: "index")
            return
        }

        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "filename=\"${entry.fileName}\"")
        response.setContentLength(entry.binaryData.size())
        response.outputStream << entry.binaryData
    }
}