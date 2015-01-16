package org.silverpeas.setup
/**
 * A Gradle plugin to configure both JBoss and Silverpeas.
 * @author mmoquillon
 */

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.silverpeas.setup.configuration.JBossConfigurationTask
import org.silverpeas.setup.configuration.SilverpeasConfigurationTask
import org.silverpeas.setup.configuration.VariableReplacement

/**
 * This plugin aims to prepare the configuration and to setup Silverpeas.
 * For doing, it loads both the default and the customer configuration file of Silverpeas and it
 * registers two tasks, one dedicated to configure JBoss/Wildfly for Silverpeas and another to
 * configure Silverpeas..
 */
class SilverpeasSetupPlugin implements Plugin<Project> {

  private def settings

  @Override
  void apply(Project project) {
    project.extensions.create('silverconf', SilverpeasSetupExtension)

    this.settings = loadConfiguration(project.silverconf.configurationHome)
    completeSettingsForProject(project)

    project.task('configureJBoss', type: JBossConfigurationTask) {
      settings = this.settings
    }

    project.task('configureSilverpeas', type: SilverpeasConfigurationTask) {
      settings = this.settings
    }
  }

  private def loadConfiguration(String configurationHome) {
    Properties properties = new Properties()
    properties.load(getClass().getResourceAsStream('/default_config.properties'))
    def customConfiguration = new File("${configurationHome}/config.properties")
    // the custom configuration overrides the default configuration
    if (customConfiguration.exists()) {
      Properties customProperties = new Properties()
      customProperties.load(customConfiguration.newReader())
      customProperties.propertyNames().each {
        properties[it] = customProperties[it]
      }
    }
    // replace the variables by their values in the properties and return the properties
    return VariableReplacement.parseParameters(properties, properties)
  }

  private def completeSettingsForProject(Project project) {
    settings.SILVERPEAS_HOME = project.silverconf.silverpeasHome
    if (settings.SILVERPEAS_LANGUAGES) {
      settings.SILVERPEAS_DEFAULT_LANGUAGE = settings.SILVERPEAS_LANGUAGES.split(',')[0].trim()
    } else {
      settings.SILVERPEAS_LANGUAGES = 'fr'
      settings.SILVERPEAS_DEFAULT_LANGUAGE = 'fr'
    }
    switch (settings.DB_SERVERTYPE) {
      case 'MSSQL':
        settings.DB_URL = "jdbc:jtds:sqlserver://${settings.DB_SERVER}:${settings.DB_PORT_MSSQL}/${settings.DB_NAME}"
        break
      case 'ORACLE':
        settings.DB_URL = "jdbc:oracle:thin:@${settings.DB_SERVER}:${settings.DB_PORT_ORACLE}:${settings.DB_NAME}"
        break
      case 'POSTGRES':
        settings.DB_URL = "jdbc:postgresql://${settings.DB_SERVER}:${settings.DB_PORT_POSTGRES}/${settings.DB_NAME}"
        break
      case 'H2':
        settings.DB_URL = "jdbc:h2:tcp://${settings.DB_SERVER}:${settings.DB_PORT_H2}/${settings.DB_NAME}"
        break
      default:
        throw new IllegalArgumentException("Unsupported database system: ${settings.DB_SERVERTYPE}")
    }
  }

}
