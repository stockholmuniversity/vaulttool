package se.su.it.vaulttool

class Entry {
    String key
    String userName
    String pwd
    byte[] binaryData

    public Map asMap() {
        this.class.declaredFields.findAll { !it.synthetic }.collectEntries {
            [ (it.name):this."$it.name" ]
        }
    }
}
