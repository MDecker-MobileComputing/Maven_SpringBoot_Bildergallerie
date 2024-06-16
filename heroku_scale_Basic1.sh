#!/bin/bash

# 1 Dyno-Container vom Typ "Basic": billigster Container
# ohne Abo, mehr als ein Container geht nicht
# Kosten ca.: 0,01$/Stunde, 7$/Monat

heroku ps:scale web=1:basic
