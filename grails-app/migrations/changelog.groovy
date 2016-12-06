databaseChangeLog = {

    changeSet(author: "jqvar (generated)", id: "1481027867461-1") {
        createTable(tableName: "meta_data") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "meta_dataPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "CLOB")

            column(name: "file_name", type: "VARCHAR(255)")

            column(name: "last_updated", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "secret_key", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "title", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "jqvar (generated)", id: "1481027867461-2") {
        addUniqueConstraint(columnNames: "secret_key", constraintName: "UC_META_DATASECRET_KEY_COL", tableName: "meta_data")
    }
}