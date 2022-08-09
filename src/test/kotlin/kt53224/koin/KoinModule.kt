package kt53224.koin

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

// imports are missing for both AnotherClass and SomeClass

val koinModule = module {
    // Quick fix > import works
    ::AnotherClass

    // Quick fix only propose to create a new function singleOf
    singleOf(::SomeClass)

    /**
     * This is the function that the quick fix proposes to create for some reason
    fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> singleOf(function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R) {
    TODO("Not yet implemented")
    }
     **/
}
