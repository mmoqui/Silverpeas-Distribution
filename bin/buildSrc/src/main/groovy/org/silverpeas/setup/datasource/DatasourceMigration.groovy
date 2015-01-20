package org.silverpeas.setup.datasource

import groovy.util.slurpersupport.GPathResult
import org.silverpeas.setup.datasource.SilverpeasModule.Status

/**
 * The migration of the structure (aka schema) of a given datasource. For a first installation,
 * the migration consists of setting-up the datasource structure, whereas for each further
 * Silverpeas upgrade it is on the update of the datasource schema. Currently, only SQL and
 * Groovy scripts are supported.
 * @author mmoquillon
 */
class DatasourceMigration {

  static databaseHome

  def status
  MigrationScript createTable
  MigrationScript createConstraint
  MigrationScript dropTable
  MigrationScript dropConstraint

  DatasourceMigration(Status status) {
    this.status = status
  }

  void create() {
    createTable.run()
    createConstraint.run()
  }

  void drop() {
    dropConstraint.run()
    dropTable.run()
  }
}
