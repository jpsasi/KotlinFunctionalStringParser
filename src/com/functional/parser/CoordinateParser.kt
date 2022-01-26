package com.functional.parser


/*
* North South Parser using Map
* */
val northSouthParser = charParser.flatMap {
    when (it) {
        'N' -> alwaysParser(1.0)
        'S' -> alwaysParser(-1.0)
        else -> neverParser()
    }
}

/*
* East West Parser using Map
* */
val eastWestParser = charParser.flatMap {
    when (it) {
        'E' -> alwaysParser(1.0)
        'W' -> alwaysParser(-1.0)
        else -> neverParser()
    }
}

/*
* Parse Latitude And Longitude -  flatMap
* */
fun parseLatLong(string: String): Coordinate? {
    val parseString = ParseString(string)
    val zipCoordinateParser = zip(doubleParser,
        literalParser("° "),
        northSouthParser,
        literalParser(", "),
        doubleParser,
        literalParser("° "),
        eastWestParser
    ).map { pair ->
        Coordinate( latitude = pair.first * pair.second.second.first,
            longitude = pair.second.second.second.second.first * pair.second.second.second.second.second.second)
    }
    return zipCoordinateParser.run(parseString)
}

fun main() {
    println(parseLatLong("40.6782° N, 73.9442° W"))
    println(parseLatLong("40.6782° S, 73.9442° W"))
    println(parseLatLong("abc.6782° S, 73.9442° W"))
}