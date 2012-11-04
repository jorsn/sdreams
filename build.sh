#!/bin/sh

cd $(dirname $0)

NAME=$(basename $PWD)-$(./get-version.sh)

B_DIR=../build-$RANDOM

cd src
mkdir $B_DIR

javac -cp $(ls ../lib/*) -d $B_DIR $(< ../build-list)

cd $B_DIR && jar -cf $NAME.jar $(ls)
cp $NAME.jar ../
cd ../

rm -rf $(basename $B_DIR)
