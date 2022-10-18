package mobprogramming

const val fizz = "fizz"
const val buzz = "buzz"

fun fizzBuzz(nb: Int): String =
    when {
        nb.divisibleBy(15) -> "$fizz$buzz"
        nb.divisibleBy(3) -> fizz
        nb.divisibleBy(5) -> buzz
        else -> nb.toString()
    }

private fun Int.divisibleBy(other: Int): Boolean =
    mod(other) == 0
