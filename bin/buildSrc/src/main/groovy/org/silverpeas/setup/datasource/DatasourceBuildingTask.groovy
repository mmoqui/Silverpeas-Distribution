package org.silverpeas.setup.datasource

import groovy.sql.Sql
import groovy.util.slurpersupport.GPathResult
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.sql.DataSource
import java.sql.Connection

/**
 * This task is for migrating the datasources used as the backend by Silverpeas. A migration is
 * either a fresh setting-up of the datasource structure or an upgrade of an existing datasource
 * schema. For doing it loads and runs a set of migration rules for each module that made
 * Silverpeas. The migrations rules are SQL or Groovy scripts.
 * <p/>
 * Currently, the data sources supported by Silverpeas are the databases (H2, PostgreSQL, MS-SQL,
 * and Oracle), the filesystem and the JCR Jackrabbit.
 * @author mmoquillon
 */
class DatasourceBuildingTask extends DefaultTask {

  private String databaseType = project.silverconf.DB_SERVERTYPE.toLowerCase()

  @TaskAction
  def buildDatasources() {
    DatasourceMigration.databaseHome = project.silverconf.databaseHome
    loadSilverpeasModules().each { module ->
      module.migrate()
    }
  }

  def loadSilverpeasModules() {
    def modules = [:]
    Sql sql
    try {
      sql = new Sql(DataSourceProvider.dataSource)
      sql.eachRow('select sr_package, sr_version from sr_packages') { row ->
        modules[row.sr_package] = new SilverpeasModule(row.sr_package, row.sr_version)
      }
      new File("${project.silverconf.databaseHome}/data/${databaseType}").listFiles() {
        GPathResult descriptor = new XmlSlurper().parse(it)
        String moduleName = descriptor.@product
        if (!modules[moduleName]) {
          modules[moduleName] = new SilverpeasModule(moduleName)
        }
        modules[moduleName].loadDatasourceMigration(descriptor)
      }
    } finally {
      sql.close()
    }
    return modules
  }

}
