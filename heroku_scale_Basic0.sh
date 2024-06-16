#!/bin/bash

# O Dyno-Container vom Typ "Basic": App ist nicht verfuegbar, 
# erzeugt aber -- bis auf evtl. AddOns keine Kosten.

heroku ps:scale web=0:basic
