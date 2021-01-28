all:
	./gradlew generateGrammarSource && ./gradlew shadowJar

clean:
	./gradlew clean

test:
	./gradlew test

.PHONY: all clean