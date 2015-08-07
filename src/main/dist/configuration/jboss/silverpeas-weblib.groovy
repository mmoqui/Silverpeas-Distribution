import java.nio.file.Files
import java.nio.file.Path

Path doDeployTagPath = "${settings.SILVERPEAS_DATA_WEB}/weblib.war.dodeploy".asPath()
if (!Files.exists(doDeployTagPath)) {
  Files.createFile(doDeployTagPath)
}