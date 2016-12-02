package se.su.it.vaulttool

class Entry {
    String key
    String title
    String description
    String pwd
    byte[] binaryData
    String fileName

    public Map asMap() {
        this.class.declaredFields.findAll { !it.synthetic }.collectEntries {
            [ (it.name):this."$it.name" ]
        }
    }
}
