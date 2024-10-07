FXPATH=${HOME}/javafx/lib
SOURCEPATH=src/main
CLASSPATH=lib/json.jar:lib/jgrapht.jar
OUTPUT=bin

build:
	javac \
		-d ${OUTPUT} \
		--module-path ${FXPATH} \
		--add-modules javafx.controls \
		--add-modules javafx.fxml \
		-sourcepath ${SOURCEPATH} \
		-classpath ${CLASSPATH} \
		src/main/dungeon/Main.java
	cp -r ${SOURCEPATH}/dungeon/View/fxml ${OUTPUT}/dungeon/View

run:
	java \
		--module-path ${FXPATH} \
		--add-modules javafx.controls \
		--add-modules javafx.fxml \
		-classpath bin:${CLASSPATH} \
		dungeon.Main
