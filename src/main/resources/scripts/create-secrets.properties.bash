#!/bin/bash

TEMPLATEPATH=secrets-template.properties
OUTPUT=$(cat $TEMPLATEPATH)

# Development values
DEVELOPMENT_DATABASE_URL=jdbc:postgresql://localhost:5432/brainy 
DEVELOPMENT_STORAGE_ACCOUNT_NAME=brainy7198
DEVELOPMENT_JWT_KEY=xn+Qgj44apCFF9Kr+8gvpa+Hau+W0D+BOPv4SD0caULHBxausydBqyZO0HVMYRx7OUUbK4jgSE0LQdWymhp1gyK6u6Ye2W7fCHaKnco5jSWoA18cJ4+fFhjkqIKEV29Xk7BFad0dykNCg07CJ/mCim9Zn+okR/qCxoYBUZmIrUW4sWxlS4FztVYi7XKQ1zxLZugeP1HRZ5JIRnjWgmBTCJbP8K3+vMBFCtV+H1NebW9Hm59wpOgjdDlfhgxxWYEVcZ1RKCty22WrHharzIFhe9xaIPkHMfDx8sGJizilVyouIFOLYUjV0FMbifeVuxiYgKtIiQ31nOzWkxPiubhhmm1Z0trsED/cUYFWS8nYn78=

# Passed valeus
DATABASE_URL=##
DATABASE_USERNAME=##
DATABASE_PASSWORD=##
STORAGE_ACCOUNT_NAME=##
JWT_KEY=##

if [[ $* == *--help* ]] || [[ $* == *-h* ]]; then
    echo -e "Creates secret.properties \n"

    echo "The following arguments must be passed: ${REQUIRED_ARGUMENTS}"
    echo -e "\t--database-url\t\t\t{value}"
    echo -e "\t--database-username\t\t{value}"
    echo -e "\t-database-password\t\t{value}"
    echo -e "\t--storage-account-name\t\t{value}"
    echo -e "\t--jwt-key\t\t\t{value}\n"

    echo "Passing --development will automatically fill the following properties so that you don't need to pass them:"
    echo -e "\t--database-url, --storage-account-name and --jwt-key"
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
        *)
            shift
            ;;
    esac
done

OUTPUT="${OUTPUT/DATABASEURL/"$DATABASE_URL"}"
OUTPUT="${OUTPUT/DATABASEUSERNAME/"$DATABASE_USERNAME"}"
OUTPUT="${OUTPUT/DATABASEPASSWORD/"$DATABASE_PASSWORD"}"
OUTPUT="${OUTPUT/JWTKEY/"$JWT_KEY"}"
OUTPUT="${OUTPUT/STORAGEACCOUNTNAME/"$STORAGE_ACCOUNT_NAME"}"
printf "$OUTPUT" > ../secrets.properties
echo "Created"
