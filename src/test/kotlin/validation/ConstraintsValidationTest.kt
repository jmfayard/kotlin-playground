package validation

import arrow.core.Either
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class ConstraintsValidationTest : FunSpec({

    test("valid car") {
        val car = Car(Manufacturer("Volkswagen"), licensePlate = "CH-42-NFT", seatCount = 4, owner = "pat@example.com")

        val validation: ConstraintValidation<Car> = car.validation()

        validation.success shouldBe true

        validation.errors shouldBe emptyList()

        validation.errorMessage shouldBe null

        shouldNotThrowAny {
            validation.throwIfInvalid()
        }

        validation.either shouldBe Either.Right(car)
    }

    test("invalid car") {
        val invalidCar = Car(Manufacturer(""), licensePlate = "Z", seatCount = 1, owner = "not an email")

        val validation: ConstraintValidation<Car> = invalidCar.validation()

        validation.success shouldBe false
        val expectedError = """
            Car.licensePlate size must be between 2 and 14
            Car.manufacturer.name must not be blank
            Car.owner must be a well-formed email address
            Car.seatCount must be greater than or equal to 2
        """.trimIndent()

        validation.errors shouldBe expectedError.lines()

        validation.errorMessage shouldBe expectedError

        shouldThrow<ConstraintValidationException> {
            validation.throwIfInvalid()
        }.shouldHaveMessage(expectedError)

        validation.either shouldBe Either.Left(ConstraintValidationException(expectedError))
    }

    test("check constraints if parameters are non-null") {
        val parameters = NullableParameters("not an email", 23)
        parameters.validation().errorMessage shouldBe """
            NullableParameters.email must be a well-formed email address
            NullableParameters.size must be greater than or equal to 150
        """.trimIndent()
    }

    test("ignore constraints if parameters are null") {
        val parameters = NullableParameters(email = null, size = null)
        parameters.validation().success shouldBe true
    }

    test("crash if no constraints are present") {
        shouldThrow<IllegalArgumentException> {
            NoConstraint(42).validation()
        }.shouldHaveMessage("validation.NoConstraint.validation() called, but class doesn't have constraints")
    }

})


