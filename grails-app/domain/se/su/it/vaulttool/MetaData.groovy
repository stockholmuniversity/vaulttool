package se.su.it.vaulttool

class MetaData {
    String key
    Date dateCreated
    Date lastUpdated
    String title
    String description
    String fileName

    static constraints = {
        key(nullable: false, blank: false, unique: true)
        title(nullable:true)
        description(nullable:true)
        fileName(nullable:true)
    }
}
