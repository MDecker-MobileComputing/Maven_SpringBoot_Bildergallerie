#!/bin/bash

# App anlegen (aber noch nicht deployen).
# Es wird auch ein (noch) leeres Repo auf git.heroku.com angelegt.

heroku create bildergallerie --region eu

# --region: eu|us (default: us)
# Name muss Heroku-weit eindeutig sein
