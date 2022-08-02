package validation

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class Car(
    // Valid is needed to check constraint annotations recursively
    @get:Valid
    val manufacturer: Manufacturer,

    @get:Size(min = 2, max = 14)
    @get:NotNull
    val licensePlate: String,

    @get:Min(2)
    val seatCount: Int,

    @get:Email
    val owner: String,
)

data class Manufacturer(
    @get:NotBlank
    val name: String,
)

// noConstraint.validation() will always fail
data class NoConstraint(
    val age: Int
)

// We rely on Kotlin type system, not annotations, for nullability
data class NullableParameters(
    @get:Email
    val email: String?,

    @get:Min(150)
    val size: Int?,
)
