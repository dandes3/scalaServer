-------------------------------------------
| Quint Guvernator, Don Andes
| Project 1: Web Server
-------------------------------------------

Created and submitted as coursework for CSCI 434, The College of William & Mary, Spring 2017.

---------------------------------------------------------------------------------------------
----- This code is provided as is and has been created strictly for educational purposes ----
---------------------------------------------------------------------------------------------

---------------------------------------------------------------------------------------------
|                   _______            __                           __                      |
|                   \      \    ____ _/  |_ __  _  __ ____ _______ |  | __                  |
|                   /   |   \ _/ __ \\   __\\ \/ \/ //  _ \\_  __ \|  |/ /                  |
|                  /    |    \\  ___/ |  |   \     /(  <_> )|  | \/|    <                   |
|                  \____|__  / \___  >|__|    \/\_/  \____/ |__|   |__|_ \                  |
|                          \/      \/                                   \/                  |
|                                                                                           |
---------------------------------------------------------------------------------------------

Notes--

      Under professor Gang Zhou's permission, this project has been written in Scala, and
       packaged into a .jar file, along with all of Scala's dependencies. Since Scala is a
       JVM based language, the program is fully executable with the normal java run command.
       We have supplied both the pre-packaged MyWebServer.scala file (for code review
       purposes) and the executable packaged MyWebServer .jar (which is large due to the 
       inclusion of all of the Scala language).

      We want to again express our gratitude for allowing us to complete this assignment in 
       an alternate JVM language, and by doing so, push the boundaries of our knowledge. 
       While we understand any partial credit will be more difficult to assign due to the 
       language differences, we are more than happy to answer any questions that may arise. 


Usage--

      The packaged jar is invoked with "$ java -jar MyWebServer.jar 8817 nameOfRootPath"
       We have implemented very basic, minimal argument checking.

      When adding a date for any test, it should be in the form "EEE, dd MMM yyyy HH:mm:ss z"
       i.e. "$ curl -I --header 'If-Modified-Since: Fri, 4 Mar 2017 13:22:49 GMT'
       http://localhost:8817" 
       We convert, deliver, and refer to all times both externally and internally in GMT. 


