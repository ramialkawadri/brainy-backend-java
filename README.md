# Brainy

This the backend repository for the Brainy project.

## See code coverage in vscode

1. Press `Ctrl + Shift + P` so that the command palette is visible, then run `Tasks: Run task` and
   choose the task with the name `Update test coverage`.
1. Press `Ctrl + Shift + P` so that the command palette is visible again, run the command
   `Coverage Gutters: Display Coverage`.

## How to setup the development/deployment environment

This a guide about how to setup the development/deployment environment for the
backend. This include both steps for the development and the deployment
environments.

### Requirements

- JDK 20
- Maven installed and added to the path
- PostgreSQL

### Development tools

Those are the primary development tools used in developing the backend:

- Postman
- Visual studio code
- [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
- [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=vmware.vscode-boot-dev-pack)
- [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode)
- [XML](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-xml)
- [Bash Beautify](https://marketplace.visualstudio.com/items?itemName=shakram02.bash-beautify)
- [Coverage Gutters](https://marketplace.visualstudio.com/items?itemName=ryanluker.vscode-coverage-gutters)

### Setting up Database

First create a new database with the name **Brainy**, then run the following
sql files that you can find in `src/main/resources/sql` in the following order:

1. user_database.sql
1. shared_files.sql
1. **(only development)** development_test_data.sql

### Setting up Azure resources

To setup the Azure resources run `src/main/resources/scripts/deploy-azure.bash`.
This must be run only when creating new Azure resources, if there is already
existing resources, nothing has to be done.

### Setting up secrets

Secrets are values (passwords, jwt-key, etc...) that are not shared with the
git repo. Those are necessary to make the application work. How to setup those
secrets can be found by running `create-secrets.bash -h`, the file can
found in `src/main/resources/scripts`.
