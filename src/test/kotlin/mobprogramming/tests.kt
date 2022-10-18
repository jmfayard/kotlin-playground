package mobprogramming

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import kotlin.math.absoluteValue
import kotlin.random.Random

class FizzBuzzTests : FunSpec({

    test("divisible by 3") {
        fizzBuzz(3) shouldBe "fizz"
        fizzBuzz(9) shouldBe "fizz"
    }

    test("divisible by 5") {
        fizzBuzz(5) shouldBe "buzz"
        fizzBuzz(10) shouldBe "buzz"
    }

    test("divisible by 15") {
        fizzBuzz(15) shouldBe "fizzbuzz"
        fizzBuzz(45) shouldBe "fizzbuzz"
    }

    val testCases = listOf(
        1,
        2,
        4,
        7,
        11,
    )

    test("others") {
        testCases.forAll {
            fizzBuzz(it) shouldBe it.toString()
        }
    }

    xtest("with random numbers") {
        val multipleOf15 = 125
        testCases.forEach {
            fizzBuzz(it +  multipleOf15) shouldBe it.toString()
        }
    }
})


