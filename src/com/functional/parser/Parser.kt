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

val charParser = Parser { parseString ->
    if (parseString.string.isEmpty()) { return@Parser null }
    parseString.drop(1)
    return@Parser parseString
}

/*
* North South Parser using Map
* */
val northSouthParser = charParser.map {
    if (it.string == "N")
        1.0
    else
        -1.0
}

/*
* East West Parser using Map
* */
val eastWestParser = charParser.map {
    if (it.string == "E")
        1.0
    else
        -1.0
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
    /* Invalid coordinate value, still returns the result */
    println(parseLatLong("40.6782° A, 73.9442° W"))

    val evenParser = intParser.map { it % 2 == 0 }
    val oddParser = intParser.map { it % 2 == 1 }

    println("Even Parser : ${evenParser.run(ParseString("100"))}")
    println("Even Parser : ${evenParser.run(ParseString("101"))}")

    println("Odd Parser : ${oddParser.run(ParseString("100"))}")
    println("Odd Parser : ${oddParser.run(ParseString("101"))}")

}