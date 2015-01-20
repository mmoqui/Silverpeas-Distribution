package org.silverpeas.setup

/**
 * Extension of the plugin in order to provide to the usual Silverpeas setting up properties.
 * @author mmoquillon
 */
class SilverpeasSetupExtension {

  /**
   * The path of the Silverpeas home directory.
   */
  String silverpeasHome = System.getenv('SILVERPEAS_HOME')
  /**
   * The path of the JBoss home directory.
   */
  String jbossHome = System.getenv('JBOSS_HOME')
  /**
   * The path of the Silverpeas and JBoss configuration home directory. It is expected to contain
   * two subdirectories:
   * <ul>
   *   <li><code>jboss</code> with the configuration files for JBoss/Wildfly;</li>
   *   <li><code>silverpeas</code> with the configuration files for Silverpeas itself</li>
   * </ul>
   * By default, the configuration home directory is set to be placed in
   * <code>SILVERPEAS_HOME</code> under the name <code>configuration</code>
   */
  String configurationHome = "${silverpeasHome}/configuration"
  /**
   * The path of the home directory of the datasource structure building scripts. It is expected to
   * contain two kinds of subdirectories:
   * <ul>
   *   <li><code>data</code> containing a folder for each supported datasource system and in which an
   *   XML setting file provide information on the SQL scripts to run for building the datasource;</li>
   *   <li>a directory per supported datasource system into which a folder per Silverpeas components
   *   gathers the SQL scripts tp build the datasource; these scripts are located in subdirectories
   *   representing a given version of the datasource structure for the belonged component.</li>
   * </ul>
   */
  String databaseHome = "${silverpeasHome}/dbRepository"
  /**
   * The path of the directory containing the JDBC drivers.
   */
  String driversDir
  /**
   * The path of the directory containing the application servers-dedicated modules required by
   * Silverpeas. The modules are gathered into a folder per supported application server.
   */
  String modulesDir
  /**
   * The path of the directory into which the log should be generated.
   */
  String logDir = "${silverpeasHome}/log"

  /**
   * Constructs a new silverpeas configuration extension. It checks the environment variables
   * SILVERPEAS_HOME and JBOSS_HOME are correctly set.
   */
  SilverpeasSetupExtension() {
    if (!silverpeasHome || !jbossHome) {
      println 'The environment variables SILVERPEAS_HOME or JBOSS_HOME aren\'t set!'
      throw new IllegalStateException()
    }
  }
}
