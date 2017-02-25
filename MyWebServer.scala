/* alvin alexander, scala god, says: "Scala methods that take a single
 * parameter can be invoked without dots or parentheses."
 *
 * also you can omit the parens in empty-argument-list functions */

def main(args: Array[String]): Unit = {
  try {
    val root = args(1)
    val port = args(0).toInt
    if (port < 0) throw new NumberFormatException
  } catch {
    case e: ArrayIndexOutOfBoundsException => println("Not enough arguments!")
    case e: NumberFormatException => println(s"'${args(0)}' is not a port number!")
  }
}
