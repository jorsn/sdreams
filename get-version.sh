#!/bin/sh

COMMIT=$(git log | head -n 1)
COMMIT=${COMMIT#commit }

git tag --contains $COMMIT | tail -n 1 | grep \$ || echo ${COMMIT}git
