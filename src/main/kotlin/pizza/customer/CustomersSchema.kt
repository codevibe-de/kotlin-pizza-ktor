package pizza.customer

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Customer(
    val id: Long,
    val fullName: String,
    val phoneNumber: String,
)

class CustomerRepository(database: Database) {

    object CustomersTable : Table() {
        val idCol = long("id").autoIncrement()
        val fullNameCol = varchar("full_name", length = 50)
        val phoneNumberCol = varchar("phone_number", length = 20)

        override val primaryKey = PrimaryKey(idCol)
    }


    init {
        transaction(database) {
            SchemaUtils.create(CustomersTable)
        }
    }


    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }


    suspend fun create(fullName: String, phoneNumber: String): Long = dbQuery {
        CustomersTable.insert {
            it[fullNameCol] = fullName
            it[phoneNumberCol] = phoneNumber
        }[CustomersTable.idCol]
    }


    suspend fun readAll(): List<Customer> {
        return dbQuery {
            CustomersTable
                .selectAll()
                .map {
                    Customer(
                        it[CustomersTable.idCol],
                        it[CustomersTable.fullNameCol],
                        it[CustomersTable.phoneNumberCol]
                    )
                }
        }
    }
}
