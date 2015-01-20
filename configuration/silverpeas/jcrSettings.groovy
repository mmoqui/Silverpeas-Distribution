import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

import javax.xml.parsers.SAXParserFactory

/**
 * This script setups the JCR repository configuration file according to the customer configuration
 * properties.
 * @author mmoquillon
 */

println 'Configure the JCR repository'

def jcrRepositoryDir = "${settings.SILVERPEAS_HOME}/jcr"
def xmlSettingFile = 'repository.xml'

SAXParserFactory factory = javax.xml.parsers.SAXParserFactory.newInstance()
factory.validating = false
factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
    false)
def jcrRepositoryConf = new XmlSlurper(factory.newSAXParser()).parse(new File("${jcrRepositoryDir}/${xmlSettingFile}"))
jcrRepositoryConf.Workspace.PersistenceManager.@class = settings.JACKRABBIT_PERSISTENCE_MANAGER
jcrRepositoryConf.Workspace.PersistenceManager.param.find { it.@name == 'schema'}.@value = settings.DB_SCHEMA
jcrRepositoryConf.Versioning.PersistenceManager.@class = settings.JACKRABBIT_PERSISTENCE_MANAGER
XmlUtil.serialize(jcrRepositoryConf, new FileWriter("${jcrRepositoryDir}/${xmlSettingFile}"))

