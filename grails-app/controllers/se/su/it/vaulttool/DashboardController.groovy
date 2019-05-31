package se.su.it.vaulttool

import grails.converters.JSON
import org.springframework.web.multipart.MultipartFile

class DashboardController {
    def vaultRestService
    def vaultService
    def utilityService

    /*def test(){
        def nisse = vaultService.deletePath(session.token, "testgroup/scrum3/")
        def byteArray = vaultService.copyPath(session.token, "systemutveckling/scrum3/")
        def result = vaultService.pastePath(session.token, "testgroup", byteArray)
        redirect(action: "index")
    }*/

    def index() {

        String selectedPath = params?.selectedPath?:""
        session.selectedPath = selectedPath
        def capabilities = vaultRestService.getCapabilities(session.token, selectedPath)

        if(request.xhr){
            return render(template: 'overview', model: [selectedPath: selectedPath, capabilities: capabilities])
        }
        [selectedPath: selectedPath, capabilities: capabilities]
    }

    def loadRootPaths(){
        def secrets = vaultRestService.listSecrets(session.token, "")

        def rootNodes = []

        def leafs = []
        def nodes = []
        def node = null

        def isAdmin = session.group == 'sysadmin' || session.group == grailsApplication.config.vault.sysadmdevgroup
        log.info "secrets: ${secrets}"
        
        secrets.each {secret ->
            if(secret.endsWith("/")){
                def sc = vaultRestService.listSecrets(session.token, secret)
                log.info "secret: ${sc}"

                node = ['id'        : secret.replace("/",""),
                        'text'      : secret.replace("/",""),
                        admin       : isAdmin,
                        type        : 'pathNode',
                        'children'  : !(sc.size() == 1 && sc.contains('dummykeydontuse')),
                        'icon'      : 'fa fa-folder',
                        'a_attr'    :  (sc.size() == 1 && sc.contains('dummykeydontuse')) ? ['class': 'path-no-children']:'']
                nodes.add(node)
            }  else {
                node =  ['id'       : 'leaf_' + secret,
                         'text'     : secret,
                         admin      : isAdmin,
                         type       : 'leafNode',
                         'children' : false,
                         'icon'     :'fa fa-lock',
                         'a_attr'   :['data-secretkey': secret]]
                leafs.add(node)
            }
            rootNodes = nodes + leafs
         }

        def rootNode = [['id': 'root', 'text': 'Root', admin: isAdmin, type: 'rootNode','children': rootNodes, 'icon':'fa fa-home fa-lg','state':['opened': true]]]

        return render(rootNode as JSON)
    }


    def loadChildren(){
        def secrets = vaultRestService.listSecrets(session.token, params['id'].toString().replaceAll("_","/"))
        def childNodes = []

        def isAdmin = session.group == 'sysadmin' || session.group == grailsApplication.config.vault.sysadmdevgroup

        secrets.each {secret ->
            def node = null
            if(secret.endsWith("/")){
                def sc = vaultRestService.listSecrets(session.token, params['id'].toString().replaceAll("_","/") + "/" + secret)

                node = ['id'        :   params['id'] + '_' + secret.replace("/",""),
                        parent      :   params['id'],
                        'text'      :   secret.replace("/",""),
                        admin       :   isAdmin,
                        'type'      :   'pathNode',
                        'children'  :   !(sc.size() == 1 && sc.contains('dummykeydontuse')),
                        'icon'      :   'fa fa-folder',
                        'a_attr'    :   (sc.size() == 1 && sc.contains('dummykeydontuse')) ? ['class': 'path-no-children']:'']
             }  else {
                node =  ['id'       :   'leaf_' + params['id'] + '_' + secret.replace("/",""),
                         parent     :   params['id'],
                         'text'     :   secret.replace("/",""),
                         admin      :   isAdmin,
                         type       :   'leafNode',
                         'children' :   false,
                         'icon'     :   'fa fa-lock',
                         'state'    :   ['hidden': (secret == 'dummykeydontuse')],
                         'a_attr'   :   ['data-secretkey': params['id'].toString().replaceAll("_","/") +'/' + secret ]]
            }

            childNodes.add(node)
        }
        return render (childNodes as JSON)
    }

    def overWriteCheck(){
        String fromPath = params.fromPath ? params.fromPath as String : ''
        String toPath = params.toPath ? params.toPath as String : ''
        def sToPath = vaultRestService.listSecrets(session.token, toPath)

        String key = fromPath.split("/").last() + "/"
        if(sToPath.contains(key)){
            return render(text: 'danger')
        }
        return render(text: 'proceed')
    }

    def handlePaths(){
        String fromPath = params.fromPath ? params.fromPath as String : ''
        String toPath = params.toPath ? params.toPath as String : ''
        Boolean deletePath = params.deletePath ? true : false

        Byte[] zipByteArray = []
        Map<String, String> result = [:]
        
        if(fromPath && toPath){
            zipByteArray = vaultService.copyPath(session.token, fromPath)
            result = vaultService.pastePath(session.eppn, session.token, toPath, zipByteArray)

            if(deletePath){
                result << vaultService.deletePath(session.token, fromPath)
            }
        }

        if(fromPath && !toPath){
            result << vaultService.deletePath(session.token, fromPath)
        }

        return render (result as JSON)
    }

    def search() {
        String secret = params?.secret?:""
        if(secret.empty) {
            redirect(actionName: "index")
            return
        }
        def keyTree = vaultRestService.getSecretTree(session.token)

        List<MetaData> metaDatas = MetaData.findAllBySecretKeyInList(keyTree).findAll{it.secretKey.contains(secret) || (it.title?it.title.contains(secret):false) || (it.description?it.description.contains(secret):false)}
        if(!metaDatas){
            String errorMsg = 'No secrets found'
            flash.warning = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "index")
            return                      
        }
        if(request.xhr){
            return render(template: 'search', model: [metadatas: metaDatas])
        }

        [metadatas: metaDatas]

    }

    def secret() {
        String key = params.key?:null
        if(!key) {
            String errorMsg = "Failed when trying to read secret. Error was: No key supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "index")
            return
        }

        Expando resp = vaultRestService.getSecret(session.token, key)
        if(resp.status) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: Permission denied."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "index")
            return
        } else if(!resp.entry) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "index")
            return
        }

        MetaData metaData = MetaData.findBySecretKey(key)
        return render (template: 'secret', model: [secret: resp.entry, metadata: metaData])
    }

    def updateSecret() {
        String key = params?.key?:null
        if(!key) {
            String errorMsg = "Failed when trying to update secret. Error was: No key supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(actionName: "index")
            return
        }
        Expando resp = vaultRestService.getSecret(session.token, key)
        if(resp.status) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: ${resp.status}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "index")
            return
        } else if(!resp.entry) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "index")
            return
        }
        MetaData metaData = MetaData.findOrCreateBySecretKey(key)
        metaData.secretKey      = key
        metaData.title          = params?.title?:""
        metaData.description    = params?.description?:""
        metaData.updatedBy      = session.eppn?:null

        Entry entry = resp.entry
        entry.key           = key
        entry.userName      = params?.userName?:""
        entry.pwd           = params?.password?:""

        Map resp2 = vaultRestService.putSecret(session.token, key, entry)
        if(resp2) {
            String errorMsg = "Failed when trying to update secret ${key}. Error was: ${resp2.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
        } else {
            flash.message = "Successfully updated secret ${key}"
            metaData.save(flush: true)
        }
        return redirect(action: "secret", params: [key: key])
    }

    def createPath(){
        String sourcePath = params?.selectedPath?:""
        String newPath = params?.path?:""
        
        Map<String,String> result = vaultService.createPath(session.token, sourcePath, newPath)

        if(result.error){
            String errorMsg = result.error
            log.error(errorMsg)
            response.status = 400
            return render(errorMsg)
        }
        return render (result as JSON)
    }

    def createSecret() {
        String key = params?.selectedPath?:""
        String path = params?.path?:""
        String secret = params?.secret?:""
        if(secret.empty) {
            String errorMsg = "Failed when trying to create secret. Error was: No key supplied."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
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
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(actionName: "index")
            return
        }
        Entry entry = new Entry()
        entry.key = key
        Map resp = vaultRestService.putSecret(session.token, key, entry)
        if(resp) {
            String errorMsg = "Failed when trying to create secret ${key}. Error was: ${resp.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            return redirect(action: "index")
        }
        MetaData metaData = new MetaData()
        metaData.secretKey = key
        metaData.title = ""
        metaData.description = ""
        metaData.fileName = ""
        metaData.updatedBy = session.eppn
        metaData.save(flush: true)
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

        Map resp = vaultRestService.deleteSecret(session.token, key)
        if(resp) {
            String errorMsg = "Failed when trying to delete secret ${key}. Error was: ${resp.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            return redirect(action: "secret", params: [key: key])
        }
        MetaData metaData = MetaData.findBySecretKey(key)
        if(metaData) {
            metaData.delete(flush: true)
        }
        flash.message = "Successfully deleted secret ${key}"
        redirect(actionName: "index")
    }

    def upload() {
        String key = params?.key?:null
        if(!key) {
            String errorMsg = "Failed when trying to upload file. Error was: No secret supplied"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(actionName: "index")
            return
        }

        Expando resp = vaultRestService.getSecret(session.token, key)
        if(resp.status) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: ${resp.status}"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        } else if(!resp.entry) {
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

        Entry entry = resp.entry
        entry.binaryData = f.bytes
        Map resp2 = vaultRestService.putSecret(session.token, key, entry)
        if(resp2) {
            String errorMsg = "Failed when trying to upload file for secret ${key}. Error was: ${resp2.status?:'Unknown Error'}"
            log.error(errorMsg)
            flash.error = errorMsg
        } else {
            MetaData metaData = MetaData.findBySecretKey(key)
            metaData.fileName = f.originalFilename
            metaData.save(flush: true)
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

        Expando resp2 = vaultRestService.getSecret(session.token, key)
        if(resp2.status) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: ${resp2.status}"
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        } else if(!resp2.entry) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            redirect(action: "index")
            return
        }
        Entry entry = resp2.entry
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
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(actionName: "index")
            return
        }

        Expando resp = vaultRestService.getSecret(session.token, key)
        if(resp.status) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: ${resp.status}"
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "index")
            return
        } else if(!resp.entry) {
            String errorMsg = "Failed when trying to read secret ${key}. Error was: secret not found."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "index")
            return
        }
        MetaData metaData = MetaData.findBySecretKey(key)
        if(!metaData) {
            String errorMsg = "Failed when trying to read metadata for secret ${key}. Error was: metadata not found."
            log.error(errorMsg)
            flash.error = errorMsg
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            redirect(action: "index")
            return
        }
        metaData.secretKey      = key
        metaData.fileName       = ""
        metaData.updatedBy      = session.eppn

        Entry entry = resp.entry
        entry.key           = key
        entry.binaryData    = "".getBytes()

        Map resp2 = vaultRestService.putSecret(session.token, key, entry)
        if(resp2) {
            String errorMsg = "Failed when trying to update secret ${key}. Error was: ${resp2.status?:'Unknown Error'}"
            log.error(errorMsg)
            if(request.xhr){
                response.status = 400
                return render(errorMsg)
            }
            flash.error = errorMsg
        } else {
            flash.message = "Successfully updated secret ${key}"
            metaData.save(flush: true)
        }
        return redirect(action: "secret", params: [key: key])
    }
}
