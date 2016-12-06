databaseChangeLog = {

    changeSet(author: "jqvar (generated)", id: "1481023399256-1") {
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

            column(name: "description", type: "VARCHAR(255)")

            column(name: "file_name", type: "VARCHAR(255)")

            column(name: "key", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "title", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "jqvar (generated)", id: "1481023399256-2") {
        addUniqueConstraint(columnNames: "key", constraintName: "UC_META_DATAKEY_COL", tableName: "meta_data")
    }
}
