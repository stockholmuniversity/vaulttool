package se.su.it.vaulttool


class VaultService {
    def vaultRestService

    Map<String,String> createPath(String token, String sourcePath, String newPath) {
        Map returnResult = [success: "Successfully created new path.", error: null]
        String pathSoFar = sourcePath
        List<String> newPathList = newPath.split("/")
        newPathList.each {String pathPart ->
            if(!pathSoFar.endsWith("/") && pathSoFar.length() > 0) {
                pathSoFar += "/"
            }
            List<String> capabilities = vaultRestService.getCapabilities(token, pathSoFar)
            if(capabilities.contains("root") || capabilities.contains("create")) {
                pathSoFar += pathPart
                def result = vaultRestService.putSecret(token, pathSoFar + "/dummykeydontuse", new Entry(key: "dummykeydontuse"))
                if(result) {
                    returnResult.error = "Failed creating path ${newPath}"
                    returnResult.success = null
                } else {
                    MetaData metaData = new MetaData()
                    metaData.secretKey = pathSoFar + "/dummykeydontuse"
                    metaData.title = ""
                    metaData.description = ""
                    metaData.fileName = ""
                    metaData.save(flush: true)
                }
            } else {
                returnResult.error = "No capabilities for creating path ${newPath}!"
                returnResult.success = null
            }
        }

        return returnResult
    }

    Map<String, String> deletePath(String token, String path) {
        Map returnResult = [success: "Successfully deleted path.", error: null]
        List<String> secretTree = vaultRestService.getSecretTree(token)
        secretTree.removeAll {String pathKey ->
            !pathKey.toLowerCase().startsWith(path.toLowerCase())
        }
        secretTree = secretTree.sort{String pathKeyA, String pathKeyB ->
            pathKeyB.count("/") <=> pathKeyA.count("/")
        }

        secretTree.each{String pathKey ->
            List<String> capabilities = vaultRestService.getCapabilities(token, pathKey)
            if(capabilities.contains("root") || capabilities.contains("delete")) {
                def result = vaultRestService.deleteSecret(token, pathKey)
                if (result) {
                    returnResult.error = "Failed deleting path ${path}"
                    returnResult.success = null
                } else {
                    MetaData metaData = MetaData.findBySecretKey(pathKey)
                    if(metaData) {
                        metaData.delete()
                    }
                }
            } else {
                returnResult.error = "No capabilities for deleting path ${path}!"
                returnResult.success = null
            }
        }
        return returnResult
    }

}
