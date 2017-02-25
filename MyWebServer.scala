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
			case e: Exception => println(":( " + e.toString)
		}
	}

	def static_error(status: Int) = {
		val err_name = Map(
			403 -> "Access is forbidden",
			404 -> "Page not found",
			500 -> "Internal server error",
			501 -> "Not implemented"
		)

		f"""
		<!doctype html>
		<html lang="en">
		<head>
			<meta charset="utf-8">
			<title>${status}</title>
		</head>
		<body>
			<h1>${status}: ${err_name(status)}</h1>
		</body>
		</html>
		"""
	}

	def got_connection(conn: Socket): Unit = {
		var r = parse_headers(new BufferedReader(
			new InputStreamReader(conn.getInputStream)))

		var payload: Array[Byte] = null
		var status = 501
		if (r.verb == "GET") {
			var path = Path.get(r.path)
			var file = new File(path)
			while (file.isDirectory) {
				path = path.resolve("index.html")
				file = new File(path)
			}

			if (file.exists) {
				try {
					payload = Files.readAllBytes(path)
				} catch {
					case e: IOException => status = 403
				}

			} else {
				status = 404
				payload = "<h1>404: File not found!</h1>"
			}
		}

		respond(
			new DataOutputStream(conn.getOutputStream),
			Files.probeContentType(r.path),
			new File(r.path.toString).lastModified,
			status,
			payload)

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

	def respond(output: DataOutputStream, content_type: String, mtime: Long, status: int, data: Array[Byte]) = {
		output.writeBytes(s"""HTTP/1.1 200 OK
Date: ${http_date.format(new Date)}
Server: ${server_name}/${version} (Arch GNU/Linux)
Last-Modified: ${http_date.format(mtime) /* TODO */}
Content-Length: ${data.length + 1}
Content-Type: ${content_type}

${if (status >= 400) static_error(status) else new String(data)}
""")
	}
}

class Request {
	var verb: String = null
	var path: String = null
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
