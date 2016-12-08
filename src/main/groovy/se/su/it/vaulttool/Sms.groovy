package se.su.it.vaulttool

class Sms {
    String  rcpt
    String  msg
    Integer payload_version = 1

    public Map asMap() {
        this.class.declaredFields.findAll { !it.synthetic }.collectEntries {
            [ (it.name):this."$it.name" ]
        }
    }
}