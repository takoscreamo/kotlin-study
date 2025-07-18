package fizzbuzz

@Suppress("MISSING_DEPENDENCY_SUPERCLASS_IN_TYPE_ARGUMENT")
fun fizzBuzz(i: Int): String {
    return when {
        i % 15 == 0 -> {
            "FizzBuzz"
        }
        i % 3 == 0 -> {
            "Fizz"
        }
        i % 5 == 0 -> {
            "Buzz"
        }
        else -> {
            "$i"
        }
    }
}

fun main(args: Array<String>) {
    val n = 20
    for (i in 1..n) {
        println(fizzBuzz(i))
    }
}
