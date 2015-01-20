package org.silverpeas.setup.datasource

/**
 *
 * @author mmoquillon
 */
class MigrationScriptBuilder {

  private scriptPath
  private scriptType

  static MigrationScriptBuilder fromScriptAt(String scriptPath) {
    MigrationScriptBuilder builder = new MigrationScriptBuilder()
    builder.scriptPath = scriptPath
    return builder
  }

  MigrationScriptBuilder ofType(String type) {
    this.scriptType = type
  }

  MigrationScript build() {
    MigrationScript script
    switch (type) {
      case 'sqlstatementlist':
        script = new SQLScript("${DatasourceMigration.databaseHome}/${scriptPath}")
        break
      case 'javalib':
        script = new GroovyScript("${DatasourceMigration.databaseHome}/${scriptPath}")
        break
      default:
        throw new IllegalArgumentException("Unknow script type: ${scriptType}")
    }
    return script
  }
}
