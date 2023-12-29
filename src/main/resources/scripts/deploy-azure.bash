#!/bin/bash

# This file is runned before any bash script, because it declares necssary variables
# and create the resource group required

clear

RESOURCEGROUPNAME=brainy
LOCATION=northeurope

echo "Signing you in"
az login

if [ $(az group exists --name $RESOURCEGROUPNAME) = false ]; then
    echo "Creating resource group"
    az group create --name $RESOURCEGROUPNAME --location $LOCATION
else
    echo "Resource group allready created"
fi

source ./storage-account.bash $RESOURCEGROUPNAME $LOCATION
