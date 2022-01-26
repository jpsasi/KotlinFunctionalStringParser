package com.functional.parser

/*
* North South Parser using Map
* */
val northSouthParser = charParser.map {
    if (it == 'N')
        1.0
    else
        -1.0
}

/*
* East West Parser using Map
* */
val eastWestParser = charParser.map {
    if (it == 'E')
        1.0
    else
        -1.0
}

/*
* Parse Latitude And Longitude
* */
fun parseLatLong(string: String): Coordinate? {
    val parseString = ParseString(string)
    doubleParser.run(parseString)?.let { latitude ->
        literalParser("° ").run(parseString)?.let {
            northSouthParser.run(parseString)?.let { latSign ->
                literalParser(", ").run(parseString)?.let {
                    doubleParser.run(parseString)?.let { longitude ->
                        literalParser("° ").run(parseString).let {
                            eastWestParser.run(parseString)?.let { longSign ->
                                return Coordinate(latitude * latSign,
                                    longitude * longSign)
                            }
                        }
                    }
                }
            }
        }
    }
    return null
}

fun main() {
    val str = ParseString("100.1.2str")
    println(intParser.runParser(str))
    println(doubleParser.runParser(str))
    println(literalParser(".1").run(str))
    println(parseLatLong("40.6782° N, 73.9442° W"))
    /* Invalid coordinate value, still returns the result */
    println(parseLatLong("40.6782° A, 73.9442° W"))

    val evenParser = intParser.map { it % 2 == 0 }
    val oddParser = intParser.map { it % 2 == 1 }

    println("Even Parser : ${evenParser.run(ParseString("100"))}")
    println("Even Parser : ${evenParser.run(ParseString("101"))}")

    println("Odd Parser : ${oddParser.run(ParseString("100"))}")
    println("Odd Parser : ${oddParser.run(ParseString("101"))}")

}