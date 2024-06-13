#!/bin/bash

rm logdatei.log 2> /dev/null

./mvnw clean spring-boot:run -Dspring-boot.run.profiles=postgres

