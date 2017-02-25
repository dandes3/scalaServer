/* alvin alexander, scala god, says: "Scala methods that take a single
 * parameter can be invoked without dots or parentheses."
 *
 * also you can omit the parens in empty-argument-list functions */

import java.io._
import java.net._
import java.nio._
import java.nio.file._
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
			case e: Exception => println(":( " + e.toString())
		}
	}

	def got_connection(conn: Socket): Unit = {
		var r = parse_headers(new BufferedReader(
			new InputStreamReader(conn.getInputStream)))

		var payload: Array[Byte] = new Array(_length=0)
		if (r.verb == "GET")
			payload = try
				Files.readAllBytes(r.path)
			catch {
				case e: IOException => {
					r.path = r.path.resolve("index.html")
					Files.readAllBytes(r.path)
				}
			}

		respond(new DataOutputStream(conn.getOutputStream),
			Files.probeContentType(r.path), new File(r.path.toString()).lastModified(), payload)

		// TODO: if-modified-since, what even are semantics really?
		if (r.close_requested)
			conn.close
	}

	def parse_headers(input: BufferedReader): Request = {
		var r = new Request
		while (true) {
			val line = input.readLine
			if (line.isEmpty) return r
			line match {

				case HTTPVerb(verb, path) => {
					r.verb = verb
					var p = path
					if (path.startsWith("http://"))
						p = p.substring(7)
					r.path = Paths.get(root + p)
				}

				case HTTPHeader(key, value) => {
					key match {
						case "Close" => r.close_requested = true
						case "If-Modified-Since" => r.if_modified_since = value
						case _ => ;
					}
				}

				case _ => println(f"Ah fuck I didn't recognize '${line}'")
			}
		}

	/*****\
 (*/ r /*)
  \*****/

	}

	def respond(output: DataOutputStream, content_type: String, mtime: Long, data: Array[Byte]) = {
		output.writeBytes(s"""HTTP/1.1 200 OK
Date: ${http_date.format(new Date)}
Server: ${server_name}/${version} (Arch GNU/Linux)
Last-Modified: ${http_date.format(mtime) /* TODO */}
Content-Length: ${data.length + 1}
Content-Type: ${content_type}

${new String(data)}
""")
	}
}

class Request {
	var verb: String = null
	var path: Path = null
	var if_modified_since: String = null
	var persistent: Boolean = true
	var close_requested: Boolean = false
}

object HTTPHeader {
	def unapply(s: String) = {
		if (s.contains(": ")) {
			val colon_pos = s.indexOf(":")
			Some(s.substring(0, colon_pos), s.substring(colon_pos + 2))
		} else {
			None
		}
	}
}

object HTTPVerb {
	def unapply(s: String) = {
		if (s.startsWith("GET ") || s.startsWith("HEAD ")) {
			val before_path = s.indexOf(' ')
			val after_path = s.lastIndexOf(' ')
			val verb = s.substring(0, before_path)
			val path = s.substring(before_path + 1, after_path)
			Some(verb, path)
		} else {
			None
		}
	}
}
