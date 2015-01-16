package org.silverpeas.setup.configuration

import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Matcher

/**
 * API of functions for the writing of Groovy scripts that will run in the installation of
 * Silverpeas.
 * @author mmoquillon
 */
class API {

  private static final def ENV_VAR_PATTERN = /\$\{(env|sys)\.(\w+)\}/

  /**
   * Gets a Path object from the specified file or directory path. The difference with the
   * {@code Paths#get(String)} method is that it supports the system properties or environment
   * variables in the specified path. If the given path contains system or environment variables,
   * then they are replaced by their value.
   * @param path the path of a file or a directory.
   * @return the Path instance representing the specified file/directory path.
   */
  static final Path get(String path) {
    def matching = path =~ ENV_VAR_PATTERN
    matching.each { token ->
      if (token[1] == 'sys') {
        path = path.replace(token[0], System.getProperty(token[2]))
      } else if (token[1] == 'env') {
        path = path.replace(token[0], System.getenv(token[2]))
      }
    }
    return Paths.get(path)
  }

  /**
   * Updates the specified properties file by replacing each property value by those specified in
   * the given properties and by adding those not defined in the properties file.
   * @param propertiesFilePath the path of the properties file.
   * @param properties the properties to put into the file.
   */
  static final void updateProperties(propertiesFilePath, properties) {
    def existingProperties = []
    FileWriter updatedPropertiesFile = new FileWriter(propertiesFilePath + '.tmp')
    new FileReader(propertiesFilePath).transformLine(updatedPropertiesFile) { line ->
      properties.each() { key, value ->
        if (line.contains(key)) {
          existingProperties << key
          line = line.replaceFirst('=.*',"=  ${Matcher.quoteReplacement(value)}")
        }
      }
      line
    }
    new FileWriter(propertiesFilePath + '.tmp', true).withWriter { writer ->
      writer.println()
      properties.findAll({ key, value -> !existingProperties.contains(key) }).each { key, value ->
        writer.println("${key} = ${value}")
      }
    }
    def template = new File(propertiesFilePath)
    def propertiesFile = new File(propertiesFilePath + '.tmp')
    propertiesFile.setReadable(template.canRead())
    propertiesFile.setWritable(template.canWrite())
    propertiesFile.setExecutable(template.canExecute())
    template.delete()
    propertiesFile.renameTo(template)
  }

}
