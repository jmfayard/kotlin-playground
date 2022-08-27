package kotest

import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.inspectors.shouldForAll
import io.kotest.inspectors.shouldForNone
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.reflection.compose
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import org.junit.jupiter.api.Test

class KotestMatcher {

    @Test
    fun `regex match all`() {
        regexUrl
            .shouldMatchAll(validUrls.linesTrimmed())
            .shouldNotMatchAny(invalidUrls.linesTrimmed())
    }

    @Test
    fun `kotest should match regex`() {
        validUrls.linesTrimmed().shouldForAll { line ->
            regexUrl should match(line)
            regexUrl.shouldMatch(line)
        }
    }

    @Test
    fun `kotest should not match regex`() {
        invalidUrls.linesTrimmed().shouldForAll { line ->
            regexUrl shouldNot match(line)
            regexUrl.shouldNotMatch(line)
        }
    }

    @Test
    fun `kotest data class matcher`() {
        // https://kotest.io/docs/assertions/data-class-matchers.html
        Person("John", 21).shouldBePerson("John", 21)

        shouldThrowMessage("Name Sam should be John\nAge 22 should be 24") {
            Person("Sam", 22).shouldBePerson("John", 24)
        }
    }
}


// https://kotest.io/docs/assertions/custom-matchers.html
fun match(input: String) = Matcher { value: Regex ->
    MatcherResult(
        value.matches(input),
        { "input: <$input> is not matched by regex: $value" },
        { "input: <$input> is unexpectedly matched by regex: $value" },
    )
}

fun Regex.shouldMatch(input: String): Regex {
    this.should(match(input))
    return this
}

fun Regex.shouldNotMatch(input: String) = apply {
    this.shouldNot(match(input))
}

fun Regex.shouldMatchAll(inputs: List<String>) = apply {
    inputs.shouldForAll { input -> this.shouldMatch(input) }
}

fun Regex.shouldNotMatchAny(inputs: List<String>) = apply {
    inputs.shouldForNone { input -> this.shouldMatch(input) }
}

fun String.linesTrimmed() = trim().lines().map(String::trim)

val regexUrl = Regex("^https?://(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b[-a-zA-Z0-9()@:%_+.~#?&/=]*\$")

val validUrls = """
            http://example.com
            https://example.com
            https://api.github.com/v1/pull_request/7891?limit=3
            http://password@patrick:example.com/endpoint?a=bde#ab
        """

val invalidUrls = """
            contact@gmail.com
            https://example.com abc
            ssh://my-computer.aws.com/etc/passwords
        """.trimIndent()

data class Person(
    val name: String,
    val age: Int,
)

fun nameMatcher(name: String) = Matcher<String> { value ->
    MatcherResult(
        value == name,
        { "Name $value should be $name" },
        { "Name $value should not be $name" }
    )
}

fun ageMatcher(age: Int) = Matcher<Int> { value ->
    MatcherResult(
        value == age,
        { "Age $value should be $age" },
        { "Age $value should not be $age" }
    )
}

fun personMatcher(name: String, age: Int) = Matcher.compose(
    nameMatcher(name) to Person::name,
    ageMatcher(age) to Person::age,
)

fun Person.shouldBePerson(name: String, age: Int) = this shouldBe personMatcher(name, age)
