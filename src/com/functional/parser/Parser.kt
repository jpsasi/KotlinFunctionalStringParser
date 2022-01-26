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
* Parser extension
* */
fun Parser.Companion.never(): Parser<Unit> {
    return Parser {
        return@Parser null
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