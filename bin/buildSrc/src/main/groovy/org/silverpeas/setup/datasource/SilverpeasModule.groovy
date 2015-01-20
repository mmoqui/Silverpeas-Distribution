package org.silverpeas.setup.datasource

import groovy.util.slurpersupport.GPathResult

/**
 * A module in Silverpeas. A module defines a set of functionalities that can be technical and
 * business-oriented. A module can be a Silverpeas application (previously named component or peas)
 * or a part of Silverpeas Core.
 * <p/>
 * Each module is made up of both codes, configuration files, and a set of setting-up rules. The
 * setting-up rules are on how to build the different data sources for the module to work properly.
 * This class that represents a module does take into account only of the setting-up rules.
 * @author mmoquillon
 */
class SilverpeasModule {

  DatasourceMigration migration
  class Status {

    private long currentVersion
    protected long latestVersion

    /**
     * Constructs a new module state.
     * @param currentVersion the current version of the module in Silverpeas.
     * @param latestVersion the up-to-date version of the module that is available with the actual
     * installation of Silverpeas.
     */
    Status(long currentVersion, long latestVersion) {
      this.currentVersion = currentVersion
      this.latestVersion = latestVersion
    }

    boolean shouldBeInstalled() {
      this.currentVersion == 0
    }

    boolean shouldBeUpgraded() {
      this.currentVersion != 0 && this.currentVersion < this.latestVersion
    }
  }

  String name
  Status status

  /**
   * Constructs a new <code>SilverpeasModule</actually code> instance representing a new module
   * with the specified name to install into the actual Silverpeas installation.
   * @param name the name of the module
   */
  SilverpeasModule(String name) {
    this(name, 0l)
  }

  /**
   * Constructs a new <code>SilverpeasModule</actually code> instance representing a module
   * with the specified name and being currently at the specified version in the actual Silverpeas
   * installation. * @param name the name of the module
   * @param currentVersion the current version of the module
   */
  SilverpeasModule(String name, long currentVersion) {
    this.status = new Status(currentVersion, currentVersion)
    this.name = name
  }

  void loadDatasourceMigration(GPathResult migrationDescriptor) {
    migration = new DatasourceMigration(status)
    status.latestVersion = migrationDescriptor.current.@version
    if (status.shouldBeInstalled()) {
      status.latestVersion = status.currentVersion
      migration.createTable =
          MigrationScriptBuilder.fromScriptAt(migrationDescriptor.current.create_table.file.@name.text().replaceAll('\\', '/'))
              .ofType(migrationDescriptor.current.create_table.file.@type).build()
      migration.createConstraint =
          MigrationScriptBuilder.fromScriptAt(migrationDescriptor.current.create_constraint.@name.text().replaceAll('\\', '/'))
              .ofType(migrationDescriptor.current.create_constraint.file.@type).build()
      migration.dropTable =
          MigrationScriptBuilder.fromScriptAt(migrationDescriptor.current.drop_table.@name.text().replaceAll('\\', '/'))
              .ofType(migrationDescriptor.current.drop_table.file.@type).build()
      migration.dropConstraint =
          MigrationScriptBuilder.fromScriptAt(migrationDescriptor.current.drop_constraint.@name.text().replaceAll('\\', '/'))
              .ofType(migrationDescriptor.current.drop_constraint.file.@type).build()
    } else {
      status.latestVersion = migrationDescriptor.upgrade.list().max({ a, b ->
        (a.@version.text() as long) < (b.@version.text() as long) ? -1 : 1
      })
      migration.createTable =
          MigrationScriptBuilder.fromScriptAt(migrationDescriptor.upgrade.create_table.file.@name.text().replaceAll('\\', '/'))
              .ofType(migrationDescriptor.current.create_table.file.@type).build()
      migration.createConstraint =
          MigrationScriptBuilder.fromScriptAt(migrationDescriptor.upgrade.create_constraint.@name.text().replaceAll('\\', '/'))
              .ofType(migrationDescriptor.current.create_constraint.file.@type).build()
      migration.dropTable =
          MigrationScriptBuilder.fromScriptAt(migrationDescriptor.upgrade.drop_table.@name.text().replaceAll('\\', '/'))
              .ofType(migrationDescriptor.current.drop_table.file.@type).build()
      migration.dropConstraint =
          MigrationScriptBuilder.fromScriptAt(migrationDescriptor.upgrade.drop_constraint.@name.text().replaceAll('\\', '/'))
              .ofType(migrationDescriptor.current.drop_constraint.file.@type).build()
    }
  }

  /**
   * Migrates this module. If it is an installation, then it sets up the datasource structure that
   * is required by this module. In the case of an upgrade, the datasource structure is then
   * updated.
   */
  void migrate() {
    migration.create()
  }

}
