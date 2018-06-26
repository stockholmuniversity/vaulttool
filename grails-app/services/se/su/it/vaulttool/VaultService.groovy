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
                }

            } else {
                returnResult.error = "No capabilities for creating path ${newPath}!"
                returnResult.success = null
            }
        }

        return returnResult
    }

}
