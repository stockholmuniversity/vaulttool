package se.su.it.vaulttool

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


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
                        metaData.delete(flush: true)
                    }
                }
            } else {
                returnResult.error = "No capabilities for deleting path ${path}!"
                returnResult.success = null
            }
        }
        return returnResult
    }

    Byte[] copyPath(String token, String path) {
        Map returnResult = [success: "Successfully copied path.", error: null]
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ZipOutputStream zos     = new ZipOutputStream(baos)

        List<String> secretTree = vaultRestService.getSecretTree(token)
        secretTree.removeAll { String pathKey ->
            !pathKey.toLowerCase().startsWith(path.toLowerCase())
        }

        secretTree.each {String pathKey ->
            def obj = vaultRestService.getSecret(token, pathKey)
            MetaData metaData = MetaData.findBySecretKey(obj.entry.key)
            if(obj?.entry?.key && !obj.entry.key.empty && metaData?.secretKey && !metaData.secretKey.empty && obj.entry.key == metaData.secretKey){
                ZipEntry entry = new ZipEntry(pathKey + "/key.txt")
                zos.putNextEntry(entry)
                zos.write(obj.entry.key.getBytes())
                zos.closeEntry()
                entry = new ZipEntry(pathKey + "/username.txt")
                zos.putNextEntry(entry)
                zos.write(obj.entry.userName?obj.entry.userName.getBytes():"".getBytes())
                zos.closeEntry()
                entry = new ZipEntry(pathKey + "/password.txt")
                zos.putNextEntry(entry)
                zos.write(obj.entry.pwd?obj.entry.pwd.getBytes():"".getBytes())
                zos.closeEntry()
                entry = new ZipEntry(pathKey + "/binarydata")
                zos.putNextEntry(entry)
                zos.write(obj.entry.binaryData?:"".getBytes())
                zos.closeEntry()
                entry = new ZipEntry(pathKey + "/${metaData.fileName}")
                zos.putNextEntry(entry)
                zos.write(obj.entry.binaryData?:"".getBytes())
                zos.closeEntry()
                entry = new ZipEntry(pathKey + "/title.txt")
                zos.putNextEntry(entry)
                zos.write(metaData.title?metaData.title.getBytes():"".getBytes())
                zos.closeEntry()
                entry = new ZipEntry(pathKey + "/description.txt")
                zos.putNextEntry(entry)
                zos.write(metaData.description?metaData.description.getBytes():"".getBytes())
                zos.closeEntry()
                entry = new ZipEntry(pathKey + "/filename.txt")
                zos.putNextEntry(entry)
                zos.write(metaData.fileName?metaData.fileName.getBytes():"".getBytes())
                zos.closeEntry()
            }
        }
        zos.close()
        Byte[] zipByteArray = baos.toByteArray()
        baos.close()
        return zipByteArray
    }

    Map<String, String> pastePath(String token, String destinationPath, Byte[] zipByteArray) {
        if(!destinationPath.endsWith("/") && destinationPath.length() > 0) {
            destinationPath += "/"
        }
        Map returnResult = [success: "Successfully pasted path.", error: null]
        List<ImportSecretHelper> secretList = []
        ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(zipByteArray))

        if (zipStream) {
            ZipEntry zipEntry
            while (zipEntry = zipStream.nextEntry) {
                if (zipEntry.isDirectory()) {
                    //create secret path
                } else {
                    String keyWithFile = zipEntry.getName()
                    if (keyWithFile.lastIndexOf("/") > -1) {
                        String secretKey = destinationPath + keyWithFile.substring(0, keyWithFile.lastIndexOf("/"))
                        String file = keyWithFile.substring(keyWithFile.lastIndexOf("/") + 1)
                        if (["key.txt", "username.txt", "password.txt", "binarydata", "title.txt", "description.txt", "filename.txt"].any { keyWithFile.contains(it) }) {
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
                                case "description.txt": importSecretHelper.metaData.description = out.toString()
                                    break
                                case "filename.txt": importSecretHelper.metaData.fileName = out.toString()
                                    break
                            }
                            out.close()
                        }
                    }

                }
            }
        }

        secretList.each { ImportSecretHelper ish ->
            vaultRestService.putSecret(token, ish.entry.key, ish.entry)
            MetaData metaData = MetaData.findOrCreateBySecretKey(ish.entry.key)
            metaData.secretKey = ish.entry.key
            metaData.title = ish.metaData.title
            metaData.description = ish.metaData.description
            metaData.fileName = ish.metaData.fileName
            metaData.save(flush: true)
        }

        return returnResult
    }

}
