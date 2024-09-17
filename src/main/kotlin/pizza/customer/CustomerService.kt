package pizza.customer

import pizza.plugins.DatabaseSingleton

class CustomerService {

    private val customerRepository = CustomerRepository(DatabaseSingleton.database)

    suspend fun getAll() =
        customerRepository.readAll()


    suspend fun createCustomer(fullName: String, phoneNumber: String) =
        customerRepository.create(fullName, phoneNumber)

}