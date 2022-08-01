# my-kotlin-scripts

This is a collection of my kotlin scripts - nothing much to see at the moment

## Kotlin Scripts ❤️ Unit Tests

What's cool with scripts is that you have all of your stuff in one file, easy to distribute.

What's not cool is that you throw away all good practices, starting with unit testing.

Let say my script [scripts/generate-urls.main.kts](bin/generate-urls.main.kts) needs to use complex Regexes

```kotlin
// start group regex
val URL_REGEX = "https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
val URL_TITLE_REGEX = "\\[([\\w\\s\\d]+)]\\((https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])\\)".toRegex()
// end group regex
```

What is this `// start group $GROUP$` snippet?

It comes from [src/test/kotlin/parsemarkown/MarkdownLinkTest.kt](src/test/kotlin/parsemarkown/MarkdownLinkTest.kt) where the regexes are unit tested.

Run `./gradlew run` and the scripts will be updated with the latest version from that file.

