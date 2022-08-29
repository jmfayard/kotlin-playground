package kotest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotest.DataSource.convertOrNull
import kotlin.reflect.KClass

/**
 * Playground for kotest issue <Read/write Table data to a file format maybe CSV #3179>
 * https://github.com/kotest/kotest/issues/3179
 */

class DataSourceTest : FunSpec({
    test("convert from string to a given type") {
        convertOrNull<String>("hello") shouldBe "hello"
        convertOrNull<Int>("42") shouldBe 42
        convertOrNull<Boolean>("true") shouldBe true
        convertOrNull<Long>("2147483647000") shouldBe 2147483647000L

        convertOrNull<Int>("hello") shouldBe null
        convertOrNull<Boolean>("hello") shouldBe null
        convertOrNull<RandomAccess>("hello") shouldBe null
    }
})

/* Sam's suggestion */
interface DataSource1<A>
interface DataSource2<A, B>
interface DataSource3<A, B, C>
interface DataSource4<A, B, C, D>
interface DataSource5<A, B, C, D, E>

object DataSource {
    /* Global register of types we can convert from String to the givenn type */
    private val datasourceConverters = mutableMapOf<KClass<*>, (String) -> Any>()

    inline fun <reified T : Any> convertOrNull(input: String): T? =
        convertOrNull(input, T::class)

    inline fun <reified T : Any> addConverter(noinline convert: (String) -> T): Unit =
        addConverter(convert, T::class)

    init {
        addConverter { it } // for String itself
        addConverter(String::toInt)
        addConverter(String::toBooleanStrict)
        addConverter(String::toDouble)
        addConverter(String::toFloat)
        addConverter(String::toLong)
        addConverter(String::toRegex)
        addConverter(String::toShort)
    }


    fun <T : Any> addConverter(convert: (String) -> T, kClass: KClass<T>) {
        datasourceConverters.put(kClass, convert)
    }

    fun <T : Any> convertOrNull(input: String, kClass: KClass<T>): T? =
        try {
            val converter = datasourceConverters[kClass]
            @Suppress("UNCHECKED_CAST")
            converter?.invoke(input) as? T
        } catch (e: Exception) {
            null
        }
}

