.PHONY = all clean

all:
	sbt assembly
	cp target/scala*/MyWebServer-assembly-*.jar .

clean:
	rm -rf *.class target
	rm -rf project/target project/project
	rm -rf MyWebServer-assembly-*.jar
