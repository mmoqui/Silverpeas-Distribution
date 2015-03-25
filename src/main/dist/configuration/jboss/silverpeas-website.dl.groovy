package jboss

import java.nio.file.Files
import java.nio.file.Path

Path doDeployTagPath = service.getPath("${settings.SILVERPEAS_DATA_WEB}/website.war.dodeploy")
if (!Files.exists(doDeployTagPath)) {
  Files.createFile(doDeployTagPath)
}