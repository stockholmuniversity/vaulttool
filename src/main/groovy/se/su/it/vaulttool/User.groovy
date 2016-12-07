package se.su.it.vaulttool

class User {
    String key
    String smsNumber

    public Map asMap() {
        this.class.declaredFields.findAll { !it.synthetic }.collectEntries {
            [ (it.name):this."$it.name" ]
        }
    }
}
