# Brainy

This the backend repository for the Brainy project.

## Requirements

- JDK 20
- Maven installed and added to the path
- PostgreSQL

## Development tools

- [Postman](https://www.postman.com/)
- [Visual studio code](https://code.visualstudio.com/download)
- [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
- [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=vmware.vscode-boot-dev-pack)
- [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode)
- [XML](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-xml)
- [Bash Beautify](https://marketplace.visualstudio.com/items?itemName=shakram02.bash-beautify)
- [Coverage Gutters](https://marketplace.visualstudio.com/items?itemName=ryanluker.vscode-coverage-gutters)

## Setting up Database

First create a new database with the name **Brainy**, then run the following
sql files that you can find in `src/main/resources/sql` in the following order:

1. `user_database.sql`
1. `shared_files.sql`
1. `development_test_data.sql` **(only development, it contains test data)**

## Setting up secrets

Secrets are values (passwords, jwt-key, etc...) that are not shared with the
git repo. Those are necessary to make the application work.

### Setting up secrets for development and production

1. `cd src/main/resources/scripts`
1. `./create-secrets.bash -h` this will give you the instruction about what to do.

### Setting up secrets for integration testing

1. `src/test/resources/scripts`
1. `./create-secrets.bash -h` this will give you the instruction about what to do.

### Setting up secrets on Github Actions

On the github repo, click on **settings**, then on **Secrets and variables**, then from the
dropdown select **Actions**.

### Getting storage account keys

To get the storage account keys, so that you have access to the storage account, got to
[Azure Portal](https://portal.azure.com), find the storage account. On the left side click
on **Access Keys**, then copy _key1_ key.

## See code coverage in vscode

1. Press `Ctrl + Shift + P` so that the command palette is visible, then run `Tasks: Run task` and
   choose the task with the name `Update code coverage`.
1. Press `Ctrl + Shift + P` so that the command palette is visible again, run the command
   `Coverage Gutters: Display Coverage`.

## Setting up a new environment on Azure

To setup the Azure resources run `src/main/resources/scripts/deploy-azure.bash`.
This must be run only when creating new Azure resources, if there is already
existing resources, nothing has to be done.
