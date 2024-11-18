#!/bin/bash

# Script that does enough so that IntelliJ is bothered with giving tailwind autocomplete
# Probably requires:
# - npm
# - node (and configured in IntelliJ)
# - tailwindcss plugin for IntelliJ

printf "\033[33msetting up tailwind hack, if nothing happens npm has borked, ctrl+c and try again...\033[0m\n"

cat <<EOL > package.json
{
  "devDependencies": {
    "tailwindcss": "latest"
  }
}
EOL

cat <<EOL > tailwind.config.js
module.exports = {
  content: ['src/main/resources/templates/**']
}
EOL

npm i &> /dev/null

printf "\033[32mtailwind IntelliJ hack hack'd in place, enjoy your auto suggest\033[0m\n"
