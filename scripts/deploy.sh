#!/bin/bash

set -e

cd ..
./gradlew distZip
scp ./build/distributions/raspberry-client-*.zip raspi:~/client
ssh raspi 'cd client && unzip raspberry-client-*.zip && ./raspberry-client-*/bin/raspberry-client'