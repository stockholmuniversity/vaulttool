package se.su.it.vaulttool

class MetaData {
    String secretKey
    Date dateCreated
    Date lastUpdated
    String title
    String description
    String fileName

    static mapping = {
        description(type: 'text')
    }

    static constraints = {
        secretKey(nullable: false, blank: false, unique: true)
        title(nullable:true)
        description(nullable:true)
        fileName(nullable:true)
    }
}
