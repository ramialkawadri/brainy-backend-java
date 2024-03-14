#!/bin/bash

TEMPLATE_PATH=secrets-template.properties
OUTPUT=$(cat $TEMPLATE_PATH)

# Font styles
BOLD=$(tput bold)
NORMAL=$(tput sgr0)

if [[ $* == *--help* ]] || [[ $* == *-h* ]]; then
    echo -e "This script creates ${BOLD}secrets.properties${NORMAL} that are required to make the tests work.\n"
    echo "The following arguments must be passed"
    echo -e "\t--storage-account-key\t\t{value}"
    exit
fi

STORAGE_ACCOUNT_KEY="$2"
OUTPUT="${OUTPUT/STORAGE_ACCOUNT_KEY/"$STORAGE_ACCOUNT_KEY"}"
printf "$OUTPUT" > ../secrets.properties
echo "Created"
