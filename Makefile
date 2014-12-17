JC		?= javac
SRC_DIR		:= src
CLASS_DIR	:= class
CLASSPATH	?= -cp ":$(SRC_DIR):./lib/sdljava.jar"
TARGET		:= frog$(shell date +-%Y.%m.%d)
SRC		:= $(SRC_DIR)/Program.java

$(TARGET): $(SRC)
	$(JC) $(CLASSPATH) $^ -d $(CLASS_DIR)

release: $(TARGET)
	jar cfm $(TARGET).jar manifest -C $(CLASS_DIR) .

clean:
	rm -f $(CLASS_DIR)/*.class ./$(TARGET).jar
