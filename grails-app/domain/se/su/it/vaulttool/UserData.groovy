package se.su.it.vaulttool

class UserData {
    String secretKey
    Date dateCreated
    Date lastUpdated
    String eppn

    static constraints = {
        secretKey(nullable: false, blank: false, unique: true)
        eppn(nullable: false, blank: false, unique: true)
    }
}
