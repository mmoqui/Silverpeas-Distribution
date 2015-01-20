package org.silverpeas.setup.configuration

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


/**
 * This task aims to configure Silverpeas from the Silverpeas configuration file, from some XML
 * configuration rules and from Groovy scripts.
 * @author mmoquillon
 */
class SilverpeasConfigurationTask extends DefaultTask {

  def settings
  private def scriptEngine

  SilverpeasConfigurationTask() {
    description = 'Configure Silverpeas'
    group = 'Build'
    dependsOn = ['assemble']
  }

  @TaskAction
  def configureSilverpeas() {
    scriptEngine = new GroovyScriptEngine(["${project.silverconf.configurationHome}/silverpeas"]
        as String[])

    new File("${project.silverconf.configurationHome}/silverpeas").listFiles().each {
      try {
        if (it.name.endsWith('.xml')) {
          processXmlSettingsFile(it)
        } else if (it.name.endsWith('.groovy')) {
          processScriptFile(it)
        } else {
          throw new UnsupportedOperationException('Configuration file not supported')
        }
      } catch (Exception ex) {
        println "An error occured while processing the configuration file ${it.path}: ${ex.message}"
      }
    }
  }

  def processXmlSettingsFile(settingsFile) {
    def settingsStatements = new XmlSlurper().parse(settingsFile)
    settingsStatements.fileset.each { fileset ->
      String dir = VariableReplacement.parseValue(fileset.@root.text(), settings)
      fileset.configfile.each { configfile ->
        String properties = configfile.@name
        def parameters = [:]
        configfile.parameter.each {
          parameters[it.@key.text()] = it.text()
        }
        parameters = VariableReplacement.parseParameters(parameters, settings)
        processPropertiesFile("${dir}/${properties}", parameters)
      }
    }
  }

  def processPropertiesFile(propertiesFilePath, parameters) {
    API.updateProperties(propertiesFilePath, parameters)
  }

  def processScriptFile(scriptFile) {
    def scriptEnv = new Binding()
    scriptEnv.setVariable('settings', settings)
    scriptEnv.setVariable('API', API)
    scriptEngine.run(scriptFile.path, scriptEnv)
  }
}
