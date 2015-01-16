package org.silverpeas.setup.configuration

import org.gradle.api.DefaultTask

/**
 *
 * @author mmoquillon
 */
abstract class AbstractConfigurationTask extends DefaultTask {

  def settings

  void init() {
    def availableDrivers = listAvailableDrivers()
    settings.DB_DRIVER = availableDrivers[settings.DB_SERVERTYPE].driverClass()
    settings.DB_DRIVER_NAME = availableDrivers[settings.DB_SERVERTYPE].driver()
    settings.DB_SCHEMA = availableDrivers[settings.DB_SERVERTYPE].name().toLowerCase()
    settings.JACKRABBIT_PERSISTENCE_MANAGER = availableDrivers[settings.DB_SERVERTYPE].persistenceManager()
  }

  private def listAvailableDrivers() {
    def drivers = [:]
    new File(project.silverconf.driversDir).listFiles().each { driver ->
      // we don't take into account H2 as it is already provided by JBoss >= 8
      if (driver.name.startsWith('postgresql')) {
        drivers.POSTGRES = DatabaseType.POSTGRESQL.withJDBCDriver(driver.name)
      } else if (driver.name.startsWith('jtds')) {
        drivers.MSSQL = DatabaseType.MSSQL.withJDBCDriver(driver.name)
      } else if (driver.name.startsWith('ojdbc')) {
        drivers.ORACLE = DatabaseType.ORACLE.withJDBCDriver(driver.name)
      } else if (driver.name.startsWith('h2')) {
        drivers.H2 = DatabaseType.H2.withJDBCDriver(driver.name)
      }
    }
    return drivers
  }

  enum DatabaseType {
    H2('org.h2.Driver',
        'org.apache.jackrabbit.core.persistence.pool.H2PersistenceManager'),
    POSTGRESQL('org.postgresql.Driver',
        'org.apache.jackrabbit.core.persistence.pool.PostgreSQLPersistenceManager'),
    MSSQL('net.sourceforge.jtds.jdbc.Driver',
        'org.apache.jackrabbit.core.persistence.pool.MSSqlPersistenceManager'),
    ORACLE('oracle.jdbc.driver.OracleDriver',
        'org.apache.jackrabbit.core.persistence.pool.OraclePersistenceManager');

    private DatabaseType(String driver, String jackrabbitPersistenceManager) {
      this.driverClass = driver
      this.persistenceManager = jackrabbitPersistenceManager;
    }

    private String driver
    private String driverClass
    private String persistenceManager;

    DatabaseType withJDBCDriver(String driver) {
      this.driver = driver
      return this
    }

    String driverClass() {
      return this.driverClass
    }

    String driver() {
      return this.driver
    }

    String persistenceManager() {
      return this.persistenceManager
    }
  }
}
