package pizza.customer

import org.jetbrains.exposed.sql.transactions.transaction
import pizza.plugins.DatabaseSingleton

class CustomerService {

    private val customerRepository = CustomerRepository(DatabaseSingleton.database)

    suspend fun getAll() =
        customerRepository.readAll()


    suspend fun createCustomer(fullName:String, phoneNumber:String) =
        customerRepository.create(fullName, phoneNumber)

}