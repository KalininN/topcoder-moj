VERSION := $(shell egrep -o 'Powered by moj [0-9][0-9a-zA-Z.]+' src/moj/moj.java | egrep -o '[0-9][0-9a-zA-Z.]+')

.PHONY: build jar instructions package test clean

build:
	mkdir -p bin && \
	javac -cp "deps/*" -d bin src/moj/*.java

jar:
	jar cf deploy/moj.jar -C bin .

instructions:
	echo "moj ${VERSION}" > deploy/moj_instructions.txt && \
	(echo "moj ${VERSION}" | sed -e 's/./=/g' >> deploy/moj_instructions.txt) && \
	echo >> deploy/moj_instructions.txt && \
	cat README >> deploy/moj_instructions.txt

package: build jar instructions
	@echo "Version ${VERSION}" && \
	zip -j deploy/moj_${VERSION}.zip \
	  deploy/moj.jar \
	  deploy/moj_instructions.txt \
	  deploy/template.cpp deploy/template.java \
	  deps/CodeProcessor.jar \
	  deps/FileEdit.jar

test:
	javac -cp "deps/*:deps/test/*:bin" -d bin test/moj/*.java test/moj/mocks/*.java && \
	java -cp "deps/*:deps/test/*:bin" org.junit.runner.JUnitCore moj.CPPParameterTest moj.JavaParameterTest

clean:
	rm -rf bin deploy/moj.jar deploy/moj*.zip
