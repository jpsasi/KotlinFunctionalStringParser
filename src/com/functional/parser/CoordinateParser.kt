package com.functional.parser

/*
* North South Parser
* */
val northSouthParser = Parser { parseString ->
    when (parseString.string.first()) {
        'N' -> {
            parseString.drop(1)
            return@Parser 1.0
        }
        'S' -> {
            parseString.drop(1)
            return@Parser -1.0
        }
        else -> {
            return@Parser null
        }
    }
}

/*
* East West Parser
* */
val eastWestParser = Parser { parseString ->
    when (parseString.string.first()) {
        'E' -> {
            parseString.drop(1)
            return@Parser 1.0
        }
        'W' -> {
            parseString.drop(1)
            return@Parser -1.0
        }
        else -> {
            return@Parser null
        }
    }
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

}