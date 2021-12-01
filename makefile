target:test

test: *.java
	javadoc *.java -d javadoc && clear && echo "---Javadoc Files Are Created---"  && javac *.java && java main