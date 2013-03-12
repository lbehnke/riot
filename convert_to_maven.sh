#!/bin/sh

cd $1
mkdir -p src/main
mkdir -p src/main/java
mkdir -p src/main/resources
mkdir -p src/test/java
mkdir -p src/test/resources

if [ -d "src/org" ]
then
	if [ -d "META-INF" ]
	then
		mv META-INF src/main/resources/ 
	fi
	mv src/org src/main/resources/

	cd src/main/resources
	find org -type f -name "*.java" | while read a
	do  
		 mkdir -p ../java/${a%/*}
		 mv "$a" ../java/"$a" 
	done
	find . -empty -type d -delete
	cd ../../../
fi

if [ -d "test" ]
then
	mv test/* src/test/resources
	cd src/test/resources
	find . -type f -name "*.java" | while read a
	do  
		 mkdir -p ../java/${a%/*}
		 mv "$a" ../java/"$a" 
	done
	find . -empty -type d -delete
	cd ../../../
    rm -rf test
fi

rm -rf build

cd ../


