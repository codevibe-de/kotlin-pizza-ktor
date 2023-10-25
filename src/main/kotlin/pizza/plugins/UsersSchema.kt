package pizza.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedUser(
    val name: String,
    val age: Int
)

class UserService(database: Database) {

    object UsersTable : Table() {
        val idCol = integer("id").autoIncrement()
        val nameCol = varchar("name", length = 50)
        val ageCol = integer("age")

        override val primaryKey = PrimaryKey(idCol)
    }

    init {
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(user: ExposedUser): Int = dbQuery {
        UsersTable.insert {
            it[nameCol] = user.name
            it[ageCol] = user.age
        }[UsersTable.idCol]
    }

    suspend fun read(id: Int): ExposedUser? {
        return dbQuery {
            UsersTable
                .select { UsersTable.idCol eq id }
                .map { ExposedUser(
                    it[UsersTable.nameCol],
                    it[UsersTable.ageCol])
                }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: ExposedUser) {
        dbQuery {
            UsersTable.update({ UsersTable.idCol eq id }) {
                it[nameCol] = user.name
                it[ageCol] = user.age
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            UsersTable.deleteWhere { idCol.eq(id) }
        }
    }

    suspend fun readAll(): List<ExposedUser> {
        return dbQuery {
            UsersTable
                .selectAll()
                .map { ExposedUser(
                    it[UsersTable.nameCol],
                    it[UsersTable.ageCol])
                }
                .toList()
        }
    }
}
