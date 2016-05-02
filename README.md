# Silverpeas-Distribution

A project to create a distribution of Silverpeas. A distribution of Silvepreas is made up of an installer (a Gradle script) and some configuration files for Silverpeas and the JEE application server.

Currently, only Wildfly >= 8 is supported.

To build and push the distribution onto our server, please use the `build.sh` script as this script will sign also the archive before pushing the GPG signature onto the server.
