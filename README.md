# KotlinFunctionalStringParser
Kotlin Functional Parser is an attempt to understand how to define the Functional Parser as explained in Pointfree episodes 
(https://www.pointfree.co/collections/parsing/what-is-parsing)

Map function is used to simplify the Parsing (NorthSouth / EastWest). 

### Advantages
It helps to define the new Parser from the existing Parser

#### Example:
`val evenParser = intParser.map { it % 2 == 0 }`

	val oddParser = intParser.map { it % 2 == 1 }
	
	
	oddParser.run

