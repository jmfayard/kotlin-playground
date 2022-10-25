package automation.checkvist

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    with(CheckvistConfig) {
        println("user=$CHECKVIST_USER token=$CHECKVIST_TOKEN")
    }
    val checkvist = CheckvistConfig.api
    val token = checkvist.login()
    val checklists = checkvist.userChecklists(token)
    println(checklists)
}
