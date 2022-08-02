package validation

import arrow.core.Either
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.hibernate.validator.HibernateValidator
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator
import java.util.Locale

/*
 * Kotlin wrapper for jakarta.validation.* validator
 * powered by Hibernate Validator
 *
 * Add in build.gradle.kts:
 *```
  dependencies {
    implementation("org.hibernate.validator:hibernate-validator:_")
    implementation("io.arrow-kt:arrow-core:_")  // optional
  }
  ```
  *
  * This is the main entry point.
  * [AnnotatedClass] must be annotated with jakarta.validation.* annotations
 */
inline fun <reified AnnotatedClass : Any> AnnotatedClass.validation():
    ConstraintValidation<AnnotatedClass> = this.validation(AnnotatedClass::class.java)

data class ConstraintValidation<T>(
    val value: T,
    val errors: List<String>,
) {
    val success: Boolean = errors.isEmpty()

    val errorMessage: String? = when {
        success -> null
        else -> errors.joinToString("\n")
    }

    fun throwIfInvalid() {
        errorMessage?.let { throw ConstraintValidationException(errorMessage) }
    }
}

data class ConstraintValidationException(override val message: String) : Exception(message)

// Optional: Arrow.Either adapter
val <T> ConstraintValidation<T>.either: Either<ConstraintValidationException, T>
    get() = when (errorMessage) {
        null -> Either.Right(value)
        else -> Either.Left(ConstraintValidationException(errorMessage))
    }

fun <T : Any> T.validation(clazz: Class<T>): ConstraintValidation<T> {
    require(validator.getConstraintsForClass(clazz).isBeanConstrained) {
        "${clazz.canonicalName}.validation() called, but class doesn't have constraints"
    }
    val violations: List<ConstraintViolation<T>> = validator.validate(this)
        .filter { violation: ConstraintViolation<*> ->
            violation.invalidValue != null
        }
    val messages = violations.map {
        "${it.rootBeanClass.simpleName}.${it.propertyPath} ${it.message}"
    }.sorted()
    return ConstraintValidation(this, messages)
}

private val locale = Locale.ENGLISH

val validator: Validator =
    Validation.byProvider(HibernateValidator::class.java)
        .configure()
        .defaultLocale(locale)
        .messageInterpolator(
            ParameterMessageInterpolator(setOf(locale), locale, false)
        )
        .buildValidatorFactory()
        .validator
