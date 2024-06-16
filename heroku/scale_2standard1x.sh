#!/bin/bash

# 2 Dyno-Container vom Typ "standard-1x".
# Kosten ca.: 0,069$/Stunde, 50$/Monat

heroku ps:scale web=2:standard-1x
