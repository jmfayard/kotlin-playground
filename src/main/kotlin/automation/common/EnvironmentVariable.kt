package automation.common

import kotlin.reflect.KProperty

data class EnvironmentVariable(val name: String, val value: String?) {
    override fun toString() =
        "export $name=$value\n"

    companion object {
        val environmentVariables = mutableSetOf<EnvironmentVariable>()
        fun print() {
            println("❯ Environment Variables")
            println(environmentVariables.joinToString(""))

        }
        operator fun getValue(any: Any, property: KProperty<*>): EnvironmentVariable =
            EnvironmentVariable(property.name, System.getenv(property.name))
                .also { environmentVariables += it }
    }
}
