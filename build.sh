#!/bin/sh

cd $(dirname $0)

NAME=$(basename $PWD)-$(./get-version.sh)

B_DIR=build-$RANDOM

mkdir $B_DIR
cp -r src $B_DIR/

cd $B_DIR/src
mkdir ../bin

if [ "$1" != '--debug' ]; then
	sed -si -e 's+boolean debug+final boolean debug+g' $(< ../../build-list)
fi

javac -cp $(ls ../../lib/*) -d ../bin/ $(< ../../build-list)

cd ../bin && jar -cf $NAME.jar $(ls)
cp $NAME.jar ../../
cd ../../

rm -rf $(basename $B_DIR)
