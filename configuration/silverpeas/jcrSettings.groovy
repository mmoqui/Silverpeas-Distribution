import groovy.xml.XmlUtil

import javax.xml.parsers.SAXParserFactory
import java.nio.file.Files
import java.nio.file.Path

/**
 * This script setups the JCR repository configuration file according to the customer configuration
 * properties.
 * @author mmoquillon
 */

println 'Configure the JCR repository'

Path jcrHomePath = Service.getPath(settings.JCR_HOME)
Path jcrConfigurationPath = Service.getPath("${settings.JCR_HOME}/repository.xml")
Path jcrConfigurationTemplatePath = Service.getPath("${settings.CONFIGURATION_HOME}/silverpeas/repository.jcr")

/* creates the JCR home directory if it doesn't already exist and copies the JCR configuration file
* into this directory */
Service.createDirectory(jcrHomePath, [readable: true, writable: true, executable: true])
if (!Files.exists(jcrConfigurationPath)) {
  Files.copy(jcrConfigurationTemplatePath, jcrConfigurationPath)
}

/* case of an old workspace named jackrabbit */
Path oldWorkspace = Service.getPath("${settings.JCR_HOME}/workspaces/jackrabbit")
if (Files.exists(oldWorkspace)) {
  Files.move(oldWorkspace, Service.getPath("${settings.JCR_HOME}/workspaces/silverpeas"))
}

/* Update the JCR configuration file from the Silverpeas configuration properties */
SAXParserFactory factory = javax.xml.parsers.SAXParserFactory.newInstance()
factory.validating = false
factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
    false)
def jcrRepositoryConf = new XmlSlurper(factory.newSAXParser()).parse(jcrConfigurationPath.toFile())
jcrRepositoryConf.Workspace.PersistenceManager.@class = settings.JACKRABBIT_PERSISTENCE_MANAGER
jcrRepositoryConf.Workspace.PersistenceManager.param.find { it.@name == 'schema'}.@value = settings.DB_SCHEMA
jcrRepositoryConf.Versioning.PersistenceManager.@class = settings.JACKRABBIT_PERSISTENCE_MANAGER
XmlUtil.serialize(jcrRepositoryConf, new FileWriter(jcrConfigurationPath.toFile()))

