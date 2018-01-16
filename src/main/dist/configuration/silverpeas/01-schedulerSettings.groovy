/**
 * This script configures the JDBC dialect to use by the Quartz scheduler when persisting the
 * jobs and the triggers into a JDBC store.
 * @author mmoquillon
 */

log.info 'Configure the JDBC driver delegate for the Persistent Quartz Scheduler'

String driverDelegateClass
switch (settings.DB_SERVERTYPE) {
  case 'POSTGRESQL':
    driverDelegateClass = 'org.quartz.impl.jdbcjobstore.PostgreSQLDelegate'
    break
  case 'MSSQL':
    driverDelegateClass = 'org.quartz.impl.jdbcjobstore.MSSQLDelegate'
    break
  case 'ORACLE':
    driverDelegateClass = 'org.quartz.impl.jdbcjobstore.oracle.OracleDelegate'
    break
  default:
    driverDelegateClass = 'org.quartz.impl.jdbcjobstore.StdJDBCDelegate'
    break
}

def properties = ['org.quartz.jobStore.driverDelegateClass': driverDelegateClass]

service.updateProperties(
    "${settings.SILVERPEAS_HOME}/properties/org/silverpeas/scheduler/settings/persistent-scheduler.properties",
    properties)

