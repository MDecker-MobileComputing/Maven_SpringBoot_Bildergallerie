#!/bin/bash

# 1 Dyno-Container vom Typ "standard-1x".
# Kosten ca.: 0,035$/Stunde, 25$/Monat

heroku ps:scale web=1:standard-1x
