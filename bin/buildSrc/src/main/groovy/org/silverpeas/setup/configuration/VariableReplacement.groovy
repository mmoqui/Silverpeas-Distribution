package org.silverpeas.setup.configuration

import org.gradle.api.tasks.StopExecutionException

/**
 * It parses the specified parameters to replace each variables found in their value by the
 * variable value from the specified properties.
 * @author mmoquillon
 */
class VariableReplacement {

  private static final def VARIABLE_PATTERN = /\$\{(\w+)\}/

  static final def parseParameters(def parameters, def variables) {
    parameters.each { key, value ->
      parameters[key] = parseValue(value, variables)
    }
    return parameters
  }

  static final String parseValue(String value, def variables) {
    def matching = value =~ VARIABLE_PATTERN
    matching.each { token ->
      if (!token[1].startsWith('env') && !token[1].startsWith('sys')) {
        if (variables.containsKey(token[1])) {
          value = value.replace(token[0], variables[token[1]])
        } else {
          println "Error: no such variable ${token[1]}"
          throw new StopExecutionException()
        }
      }
    }
    return value
  }

}
