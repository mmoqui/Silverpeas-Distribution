#!/usr/bin/env bash

die() {
  echo "Missing user credentials to push files into the silverpeas web site"
  exit 1
}

test $# -eq 1 || die

user="$1"

mvn clean install
gpg -ab target/silverpeas-*.zip
scp target/silverpeas-*.zip.asc $user@www.silverpeas.org:/var/www/files/
