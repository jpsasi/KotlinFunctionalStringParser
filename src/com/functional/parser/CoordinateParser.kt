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
    val coordinateParser = doubleParser.flatMap { latitude ->
        literalParser("° ").flatMap {
            northSouthParser.flatMap { latSign ->
                literalParser(", ").flatMap {
                    doubleParser.flatMap { longitude ->
                        literalParser("° ").flatMap {
                            eastWestParser.map { longSign ->
                                Coordinate(latitude = latitude * latSign, longitude = longitude * longSign)
                            }
                        }
                    }
                }
            }
        }
    }
    return coordinateParser.run(parseString)
}

fun main() {
    println(parseLatLong("40.6782° N, 73.9442° W"))
    /* Invalid coordinate value returns nil */
    println(parseLatLong("40.6782° S, 73.9442° W"))
    println(parseLatLong("abc.6782° S, 73.9442° W"))

}