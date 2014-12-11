JC		?= javac
CLASSPATH	?= -cp ":./:./lib/sdljava.jar"
TARGET		:= frog$(shell date +-%Y.%m.%d)
SRC		:= Program.java

$(TARGET): $(SRC)
	$(JC) $(CLASSPATH) $^

release: $(TARGET)
	jar cfm $(TARGET).jar manifest *.class

clean:
	rm -f ./*.class ./$(TARGET).jar
