import java.nio.file.Files
import java.nio.file.Path

log.info 'Inform Wildfly to deploy explicitly the website war'

Path doDeployTagPath = "${settings.SILVERPEAS_DATA_WEB}/website.war.dodeploy".asPath()
if (!Files.exists(doDeployTagPath)) {
  Files.createFile(doDeployTagPath)
}