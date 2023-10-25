package pizza.customer

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.customerRoutes() {

    val customerService = CustomerService()

    routing {
        route("/customers") {
            get {
                val customers = customerService.getAll()
                call.respond(HttpStatusCode.OK, customers)
            }
            post {
                val user = call.receive<CreateCustomerRequest>()
                val id = customerService.createCustomer(user.fullName, user.phoneNumber)
                call.respond(HttpStatusCode.Created, id)
            }
        }
    }
}

data class CreateCustomerRequest(
    val fullName: String,
    val phoneNumber: String,
)