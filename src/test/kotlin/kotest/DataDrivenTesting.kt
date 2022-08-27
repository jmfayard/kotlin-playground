package kotest

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

/**
 * Doc: https://kotest.io/docs/framework/datatesting/data-driven-testing.html
 * Gradle: testImplementation(Testing.kotest.framework.datatest)
 */
class DataDrivenTesting : FunSpec({

    data class IntTriangle(val a: Int, val b: Int, val c: Int) : WithDataTestName {
        fun isRightAngled(): Boolean =
            a * a + b * b == c * c

        override fun dataTestName() = "$a, $b, $c"
    }


    context("IntTriangles that follow Pythagoras theorem: a^2 + b^2 = c^2") {
        withData(
            IntTriangle(3, 4, 5),
            IntTriangle(6, 8, 10),
            IntTriangle(8, 15, 17),
            IntTriangle(7, 24, 25)
        ) { triangle ->
            triangle.isRightAngled() shouldBe true
        }
    }

    context("IntTriangle that are not Pythagorean triples") {
        withData(
            IntTriangle(1, 4, 7),
            IntTriangle(2, 2, 2),
        ) { triangle ->
            triangle.isRightAngled() shouldBe false
        }
    }
})


