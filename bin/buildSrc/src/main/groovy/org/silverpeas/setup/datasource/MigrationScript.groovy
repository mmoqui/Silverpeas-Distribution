package org.silverpeas.setup.datasource

/**
 * A script to migrate the structure (aka schema) of one or several datasources used by a given
 * Silverpeas module.
 * @author mmoquillon
 */
interface MigrationScript {

  /**
   * Runs this script.
   * @throws RuntimeException if an error occurs during the execution of this script.
   */
  void run() throws RuntimeException
}
