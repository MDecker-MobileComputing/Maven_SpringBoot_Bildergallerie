#!/bin/bash

# Fügt der App eine Postgresql-Datenbank-Instanz hinzu
# https://elements.heroku.com/addons/heroku-postgresql
#
# Kleinste Instanz "essential-0":
# * Kosten: 0,007$/Stunde, 5$/Monat
# * Kapazität: 1 GB

# Standard-Instanz: essential-0
#heroku addons:create heroku-postgresql
heroku addons:create heroku-postgresql:essential-0

# Folgende Umgebungsvariable werden gesetzt:
# * SPRING_DATASOURCE_URL
# * SPRING_DATASOURCE_USERNAME
# * SPRING_DATASOURCE_PASSWORD

# Nach Erzeugung kann man sich mit den folgenden beiden
# Befehlen die URL bzw. weitere Infos anzeigen lassen:
# * heroku config
# * heroku pg:info
