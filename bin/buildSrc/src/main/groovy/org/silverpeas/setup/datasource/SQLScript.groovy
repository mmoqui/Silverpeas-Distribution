package org.silverpeas.setup.datasource

import com.ninja_squad.dbsetup.DbSetup
import com.ninja_squad.dbsetup.Operations
import com.ninja_squad.dbsetup.destination.DataSourceDestination
import com.ninja_squad.dbsetup.operation.Operation

/**
 * An SQL script
 * @author mmoquillon
 */
class SQLScript implements MigrationScript {

  private Operation operation

  SQLScript(String ... scriptPath) {
    def sqlStatements = []
    for (String sqlScript: scriptPath) {
      sqlStatements << new File(sqlScript).getText('UTF-8')
    }
    operation = Operations.sequenceOf(sqlStatements)
  }

  @Override
  void run() throws RuntimeException {
    DbSetup dbSetup = new DbSetup(new DataSourceDestination(null), operation);
    dbSetup.launch()
  }
}
