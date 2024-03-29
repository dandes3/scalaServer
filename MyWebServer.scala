/* Scala Web Server
 * by Don Andes and Quint Guvernator
 *
 * Scala is a language that, like Java, runs on the JVM, but unlike Java, is
 * actually a good language that supports many programming styles. There are
 * several language-level features in Scala that make functional programming
 * easier and help reduce the inane boilerplate required by the Java standard
 * library.
 *
 * We understand that it will be difficult to assign partial credit if our code
 * fails tests. If you have any questions, we're happy to answer them.
 *
 * Thank you again for letting us push our boundaries on this. We learned a lot
 * in the process of writing our server in a language neither of us knew.
 * Hopefully we can keep it up for the rest of the semester.
 */

import java.io._
import java.net._
import java.nio._
import java.nio.file._
import java.io.File
import java.text._
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object MyWebServer {

	val version = "0.1"
	val server_name = "idk-not-java-tho"

	var root: String = null
	var port: Int = 0
	var socket: ServerSocket = null

	def main(args: Array[String]) = {
		try {
			root = args(1)
			port = args(0).toInt
			if (port < 0) throw new NumberFormatException
			socket = new ServerSocket(port)
			println(s"listening on port ${port}")
			Iterator continually None foreach ( _ => got_connection(socket.accept) ) //debug
		} catch {
			case e: ArrayIndexOutOfBoundsException => println("Not enough arguments!")
			case e: NumberFormatException => println(s"'${args(0)}' is not a port number!")
			case e: Exception => println("Server error: " + e.toString)
		}
	}

	/* ported from "Getting Date in HTTP format in Java" at
	 * http://stackoverflow.com/a/8642463 */
	def http_date = {
		var fmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
		fmt.setTimeZone(TimeZone.getTimeZone("GMT"))
		fmt
	}

	/* gets file mtime and normalizes it to the nearest second */
	def http_modified(file: File): Date = http_date.parse(http_date.format(new Date(file.lastModified)))

	/* turn an HTTP path request into an actual filesystem path */
	def resolve_path(r: Request) = {
			var path = Paths.get(root + r.path)
			var file = path.toFile
			while (file.isDirectory) {
				path = path.resolve("index.html")
				file = path.toFile
			}
			path
	}

	/* given a path and a request, get the appropriate error code */
	def status_code(r: Request, path: Path) = {
		val f = path.toFile
		val mtime = http_modified(f)

		if (r.verb != "GET" && r.verb != "HEAD")
			501
		else if (!f.exists)
			404
		else if (!f.canRead)
			403
		else if (r.if_modified_since.map(x => !mtime.after(x)).getOrElse(false)) {
			println(s"if_modified_since: ${http_date.format(r.if_modified_since.get)}")
			println(s"mtime: ${http_date.format(mtime)}")
		    304
		} else
			200
	}

	def got_connection(conn: Socket): Unit = {
		println("\nconnection received")
		val output = new DataOutputStream(conn.getOutputStream)

		parse_headers(new BufferedReader(new InputStreamReader(conn.getInputStream))) match {
			case Some(r) => {
				println(s"${r.verb} ${r.path}")
				val path = resolve_path(r)
				status_code(r, path) match {
					case 200 => ok(output, path, r.verb != "HEAD")
					case 304 => okNotMod(output, path, r.verb != "HEAD")
					case status: Int => err(output, status)
				}
				if (r.close_requested)
					conn.close
			}

			case None => {
				println("malformed request")
				err(output, 400)
				conn.close
			}
		}
	}

	def parse_headers(input: BufferedReader): Option[Request] = {
		var r = new Request

		// Iterator continually None foreach: lol
		while (true) {
			val line = input.readLine
			if (line.isEmpty) return Some(r)
			try {
				line match {

					case HTTPVerb(verb, path) => {
						r.verb = verb
						r.path = path
						if (r.path.startsWith("http://"))
							r.path = r.path.substring(7)
					}

					case HTTPHeader(key, value) => {
						key match {
							case "Connection" => r.close_requested = (value == "close")
							case "If-Modified-Since" => {

								/* explicitly passing ParsePosition means that if the date
								 * string fails to parse, header remains None */
								r.if_modified_since = Option(http_date.parse(value, new ParsePosition(0)))

							}
							case _ => ;
						}
					}

					case _ => return None
				}

			} catch {
				case _: Throwable => return None
			}
		}

		None
	}

	def ok(output: DataOutputStream, path: Path, include_body: Boolean) = {
		respond(output, 200, http_modified(path.toFile), Files.probeContentType(path), if (include_body) new String(Files.readAllBytes(path)) else "")
	}

	def okNotMod(output: DataOutputStream, path: Path, include_body: Boolean) = {
		respond(output, 304, http_modified(path.toFile), Files.probeContentType(path), "")
	}

	def err(output: DataOutputStream, status: Int) = {
		val err_name = Map(
			400 -> "Bad request",
			403 -> "Access is forbidden",
			404 -> "Page not found",
			500 -> "Internal server error",
			501 -> "Not implemented"
		)

		respond(output, status, new Date, "text/html", f"""<!doctype html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>${status}</title>
</head>
<body>
	<h1>${status}: ${err_name(status)}</h1>
</body>
</html>""")
	}

	def respond(output: DataOutputStream, status: Int, mtime: Date, content_type: String, body: String) = {
		val status_desc = if (status == 200) "OK" else if (status == 304) "Not Modified" else ""
		output.writeBytes(s"""HTTP/1.1 ${status} ${status_desc}
Date: ${http_date.format(new Date)}
Server: ${server_name}/${version} (GNU/Linux)
Last-Modified: ${http_date.format(mtime)}
Content-Length: ${body.length + 1}
Content-Type: ${content_type}

${body}

""")
	}
}

class Request {
	var verb: String = ""
	var path: String = ""
	var if_modified_since: Option[Date] = None
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
