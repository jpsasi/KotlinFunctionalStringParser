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

val charParser = Parser { parseString ->
    if (parseString.string.isEmpty()) { return@Parser null }
    val match = parseString.string.first()
    parseString.drop(1)
    return@Parser match
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

fun <A,B> zip(a: Parser<A>, b: Parser<B>) : Parser<Pair<A,B>> {
    return Parser { parseString ->
        val originalString = parseString
        a.run(parseString)?.let { matchA ->
            b.run(parseString)?.let { matchB ->
                return@Parser Pair(matchA, matchB)
            } ?: run {
                parseString.string = originalString.string
                return@Parser null
            }
        }
    }
}

fun <A, B, C> zip(
    a: Parser<A>,
    b: Parser<B>,
    c: Parser<C>
): Parser<Pair<A, Pair<B, C>>> {
    return zip(a, zip(b, c))
}

fun <A, B, C, D> zip(
    a: Parser<A>,
    b: Parser<B>,
    c: Parser<C>,
    d: Parser<D>
): Parser<Pair<A, Pair<B, Pair<C, D>>>> {
    return zip(a, zip(b, zip(c, d)))
}

fun <A,B,C,D, E> zip(a: Parser<A>,
                     b: Parser<B>,
                     c: Parser<C>,
                     d: Parser<D>,
                     e: Parser<E>): Parser<Pair<A,Pair<B,Pair<C,Pair<D, E>>>>> {
    return zip(a, zip(b, zip(c, zip(d, e))))
}

fun <A, B, C, D, E, F> zip(
    a: Parser<A>,
    b: Parser<B>,
    c: Parser<C>,
    d: Parser<D>,
    e: Parser<E>,
    f: Parser<F>
): Parser<Pair<A, Pair<B, Pair<C, Pair<D, Pair<E, F>>>>>> {
    return zip(a, zip(b, zip(c, zip(d, zip(e, f)))))
}

fun <A, B, C, D, E, F, G> zip(
    a: Parser<A>,
    b: Parser<B>,
    c: Parser<C>,
    d: Parser<D>,
    e: Parser<E>,
    f: Parser<F>,
    g: Parser<G>
): Parser<Pair<A, Pair<B, Pair<C, Pair<D, Pair<E, Pair<F, G>>>>>>> {
    return zip(a, zip(b, zip(c, zip(d, zip(e, zip(f, g))))))
}
