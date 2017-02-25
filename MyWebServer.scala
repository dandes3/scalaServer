/* alvin alexander, scala god, says: "Scala methods that take a single
 * parameter can be invoked without dots or parentheses."
 *
 * also you can omit the parens in empty-argument-list functions */

import java.io._
import java.net._
import java.nio._
import java.text._
import java.util.Date

object MyWebServer {

	val version = "0.1"
	val server_name = "idk-not-java-tho"
	val http_date = new SimpleDateFormat("EEE MMM d hh:mm:ss zzz yyyy")

	var root: String = null
	var port: Int = 0
	var socket: ServerSocket = null

	def main(args: Array[String]) = {
		try {
			root = args(1)
			port = args(0).toInt
			if (port < 0) throw new NumberFormatException
			socket = new ServerSocket(port)
			Iterator continually None foreach ( _ => got_connection(socket.accept) )
		} catch {
			case e: ArrayIndexOutOfBoundsException => println("Not enough arguments!")
			case e: NumberFormatException => println(s"'${args(0)}' is not a port number!")
		}
	}

	def got_connection(conn: Socket): Unit = {
		parse_headers(new BufferedReader(
			new InputStreamReader(conn.getInputStream)))
		write_headers(new DataOutputStream(conn.getOutputStream))
		conn.close
	}

	def parse_headers(input: BufferedReader): Unit = {
		while (true) {
			val line = input.readLine
			if (line.isEmpty) return
			println(line)
		}
	}

	def write_headers(output: DataOutputStream) = {
		output.writeBytes(s"""HTTP/1.1 200 OK
Date: ${http_date.format(new Date)}
Server: ${server_name}/${version} (Arch GNU/Linux)
Last-Modified: ${http_date.format(new Date) /* TODO */}
Content-Length: 18
Content-Type: text/html

<h1>FUCK YOU</h1>
""")
		//Content-Type: ${Files.probeContentType(path)}
	}
}
