#!/bin/bash

TEMPLATE_PATH=secrets-template.properties
OUTPUT=$(cat $TEMPLATE_PATH)

# Development values
DEVELOPMENT_DATABASE_URL=jdbc:postgresql://localhost:5432/brainy

DEVELOPMENT_STORAGE_ACCOUNT_NAME=brainydevelopment

DEVELOPMENT_JWT_KEY=xn+Qgj44apCFF9Kr+8gvpa+Hau+W0D+BOPv4SD0caULHBxausydBqyZO0HVMYRx7OUUbK4jgSE0LQdWymhp1gyK6u6Ye2W7fCHaKnco5jSWoA18cJ4+fFhjkqIKEV29Xk7BFad0dykNCg07CJ/mCim9Zn+okR/qCxoYBUZmIrUW4sWxlS4FztVYi7XKQ1zxLZugeP1HRZ5JIRnjWgmBTCJbP8K3+vMBFCtV+H1NebW9Hm59wpOgjdDlfhgxxWYEVcZ1RKCty22WrHharzIFhe9xaIPkHMfDx8sGJizilVyouIFOLYUjV0FMbifeVuxiYgKtIiQ31nOzWkxPiubhhmm1Z0trsED/cUYFWS8nYn78=

# Passed values
DATABASE_URL=##
DATABASE_USERNAME=##
DATABASE_PASSWORD=##
STORAGE_ACCOUNT_NAME=##
STORAGE_ACCOUNT_KEY=##
JWT_KEY=##

# Font styles
BOLD=$(tput bold)
NORMAL=$(tput sgr0)

if [[ $* == *--help* ]] || [[ $* == *-h* ]]; then
    echo -e "This script creates ${BOLD}secrets.properties${NORMAL} that are required to make the app work.\n"
    
    echo "The following arguments must be passed on a ${BOLD}production${NORMAL} environment:"
    echo -e "\t--database-url\t\t\t{value}"
    echo -e "\t--database-username\t\t{value}"
    echo -e "\t--database-password\t\t{value}"
    echo -e "\t--storage-account-name\t\t{value}"
    echo -e "\t--storage-account-key\t\t{value}"
    echo -e "\t--jwt-key\t\t\t{value}\n"
    
    echo "The following arguments must be passed on a ${BOLD}development${NORMAL} environment:"
    echo -e "\t--development"
    echo -e "\t--database-username\t\t{value}"
    echo -e "\t--database-password\t\t{value}"
    echo -e "\t--storage-account-key\t\t{value}"
    exit
fi

if [[ $* == *--development* ]]; then
    DATABASE_URL=$DEVELOPMENT_DATABASE_URL
    STORAGE_ACCOUNT_NAME=$DEVELOPMENT_STORAGE_ACCOUNT_NAME
    JWT_KEY=$DEVELOPMENT_JWT_KEY
fi

while true; do
    if [[ $# == 0 ]]; then
        break
    fi
    
    case "$1" in
        --database-url)
            DATABASE_URL="$2"
            shift 2
        ;;
        --database-username)
            DATABASE_USERNAME="$2"
            shift 2
        ;;
        --database-password)
            DATABASE_PASSWORD="$2"
            shift 2
        ;;
        --jwt-key)
            JWT_KEY="$2"
            shift 2
        ;;
        --storage-account-name)
            STORAGE_ACCOUNT_NAME="$2"
            shift 2
        ;;
        --storage-account-key)
            STORAGE_ACCOUNT_KEY="$2"
            shift 2
        ;;
        *)
            shift
        ;;
    esac
done

OUTPUT="${OUTPUT/DATABASE_URL/"$DATABASE_URL"}"
OUTPUT="${OUTPUT/DATABASE_USERNAME/"$DATABASE_USERNAME"}"
OUTPUT="${OUTPUT/DATABASE_PASSWORD/"$DATABASE_PASSWORD"}"
OUTPUT="${OUTPUT/JWT_KEY/"$JWT_KEY"}"
OUTPUT="${OUTPUT/STORAGE_ACCOUNT_NAME/"$STORAGE_ACCOUNT_NAME"}"
OUTPUT="${OUTPUT/STORAGE_ACCOUNT_KEY/"$STORAGE_ACCOUNT_KEY"}"
printf "$OUTPUT" > ../secrets.properties
echo "Created"
