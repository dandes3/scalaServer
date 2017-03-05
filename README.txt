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
       purposes) and the executable packaged MyWebServer jar (which is large due to 
       packaging).


Usage--

      The packaged jar is invoked with "$ java -jar MyWebServer.jar 8817 nameOfRootPath" 
       We have implemented very basic, minimal argument checking.

      When adding a date for any test, it should be in the form "EEE MMM d hh:mm:ss zzz yyyy"
      i.e. "$ curl -I --header 'If-Modified-Since: Fri Mar 4 03:22:49 EDT 2017' 
      http://localhost:8817"

