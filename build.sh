#!/bin/sh

cd $(dirname $0)/src

NAME=$(basename $PWD)
B_DIR=../build-$RANDOM

mkdir $B_DIR

javac -cp $(ls lib/*) -d $B_DIR $(< build-list)

cd $B_DIR && jar -cf $NAME.jar *
cp $NAME.jar ../
cd ../

rm -rf $B_DIR
