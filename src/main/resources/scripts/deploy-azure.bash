#!/bin/bash

# This file is runned before any bash script, because it declares necssary variables
# and create the resource group required

clear

RESOURCE_GROUP_NAME=brainy
LOCATION=northeurope

echo "Signing you in"
az login

if [ $(az group exists --name $RESOURCE_GROUP_NAME) = false ]; then
    echo "Creating resource group"
    az group create --name $RESOURCE_GROUP_NAME --location $LOCATION
else
    echo "Resource group allready created"
fi

source ./storage-account.bash $RESOURCE_GROUP_NAME $LOCATION
