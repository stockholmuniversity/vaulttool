package se.su.it.vaulttool

class Policy {
    private String  name
    private String  path = "secret/${VaultRestService.VAULTTOOLSECRETSPATHNAME}"
    boolean create  = false
    boolean read    = false
    boolean update  = false
    boolean delete  = false
    boolean list    = false

    public void setName(String policyName) {
        name = policyName
    }

    public void setPath(String policyPath) {
        if(!policyPath.empty) {
            path = "secret/${VaultRestService.VAULTTOOLSECRETSPATHNAME}/${policyPath}"
        } else {
            path = "secret/${VaultRestService.VAULTTOOLSECRETSPATHNAME}"
        }
    }

    public void setSpecialPath(String policyPath) {
        path = policyPath
    }

    public Map asMap() {
        List<String> capabilities = []
        if(create){capabilities << '"create"'}
        if(read){capabilities << '"read"'}
        if(update){capabilities << '"update"'}
        if(delete){capabilities << '"delete"'}
        if(list){capabilities << '"list"'}
        String capabilitiesString = capabilities.join(", ")

        Map theStrangeMap = ['rules': 'path "'+ path +'" {capabilities = ['+ capabilitiesString +']}']
        return theStrangeMap
    }
}
