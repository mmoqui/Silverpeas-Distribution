package silverpeas

import groovy.xml.XmlUtil

/**
 * This script configures the different database XML configuration.
 * @author mmoquillon
 */

println 'Configure the workflow persistence engine'

def workflowSettingsDir = "${settings.SILVERPEAS_HOME}/resources/instanceManager"
def xmlSettingFiles = ['database.xml', 'fast_database.xml']

def engine
switch (settings.DB_SERVERTYPE) {
  case 'MSSQL':
    engine = 'sql-server'
    break
  case 'ORACLE':
    engine = 'oracle'
    break
  default:
    engine = 'postgresql'
    break
}

xmlSettingFiles.each { aXmlSettingFile ->
  def xmlMapping = (aXmlSettingFile.contains('fast')) ? 'fast_mapping.xml':'mapping.xml'
  def jdoConf = new XmlSlurper(false, false).parse(new File("${workflowSettingsDir}/${aXmlSettingFile}"))
  jdoConf.@engine = engine
  jdoConf.database.@engine = engine
  jdoConf.database.mapping.@href = "file:///${settings.SILVERPEAS_HOME}/resources/instanceManager/${xmlMapping}" as String

  XmlUtil.serialize(jdoConf, new FileWriter("${workflowSettingsDir}/${aXmlSettingFile}"))
}
