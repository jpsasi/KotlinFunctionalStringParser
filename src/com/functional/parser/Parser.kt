package com.functional.parser

/*
* Parser class which accepts the ParseString wrapper to perform the Parsing
* */
class Parser<A>(val run: (ParseString) -> A?) {
    companion object

    fun runParser(parseString: ParseString) : Pair<A?, ParseString> {
        val match = run(parseString)
        return Pair(match, parseString)
    }
}

/*
* Wrapper class for String to pass the argument to Parser class
* as Call by Reference
* */
class ParseString(var string: String) {

    fun drop(n: Int) {
        string = string.drop(n)
    }

    override fun toString(): String {
        return string
    }
}

/*
* Integer Parser
* */
val intParser = Parser { parseString ->
    val prefix = parseString.string.takeWhile { it.isDigit() }
    try {
        val match = prefix.toInt()
        parseString.drop(prefix.length)
        return@Parser match
    } catch (exception: NumberFormatException) {
        return@Parser null
    }
}

/*
* Double Parser
*/
val doubleParser = Parser { parseString ->
    val prefix = parseString.string.takeWhile { it.isDigit() || it == '.' }
    try {
        val match = prefix.toDouble()
        parseString.drop(prefix.length)
        return@Parser match
    } catch (exception: NumberFormatException) {
        return@Parser null
    }
}

/*
* Literal Parser
* */
fun literalParser(literal: String) : Parser<Unit> {
    return Parser { parseString ->
        if (parseString.string.commonPrefixWith(literal).isEmpty()) {
            return@Parser Unit
        }
        parseString.drop(literal.length)
        return@Parser Unit
    }
}

/*
* Always Parser (Always return success)
* */
fun <A> alwaysParser(a: A) : Parser<A> {
    return Parser {
        return@Parser a
    }
}

/*
* Never Parser (Never returns success)
* */
fun <A> neverParser(): Parser<A> {
    return Parser {
        return@Parser null
    }
}

fun <A,B> Parser<A>.map(f: (A) -> B) : Parser<B> {
    return Parser { parseString ->
        val a = run(parseString)
        if (a == null) {
            return@Parser null
        } else {
            return@Parser f(a)
        }
    }
}

fun <A,B> Parser<A>.flatMap(f: (A) -> Parser<B>) : Parser<B> {
    return Parser { parseString ->
        val originalString = parseString
        val matchA = run(parseString)
        if (matchA == null) {
            return@Parser null
        } else {
            val parserB = f(matchA)
            val parserBResult = parserB.run(parseString)
            if (parserBResult == null) {
                parseString.string = originalString.string
                return@Parser null
            } else {
                return@Parser parserBResult
            }
        }
    }
}

val charParser = Parser { parseString ->
    if (parseString.string.isEmpty()) { return@Parser null }
    val match = parseString.string.first()
    parseString.drop(1)
    return@Parser match
}

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