package org.silverpeas.setup.datasource

import groovy.sql.Sql
import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource

import javax.sql.DataSource

/**
 *
 * @author mmoquillon
 */
class DataSourceProvider {

  private static DataSource dataSource

  static void init(settings) {
    DriverAdapterCPDS cpds = new DriverAdapterCPDS();
    cpds.setDriver(settings.DB_DRIVER);
    cpds.setUrl(settings.DB_URL);
    cpds.setUser(settings.DB_USER);
    cpds.setPassword(settings.DB_PASSWORD);

    SharedPoolDataSource tds = new SharedPoolDataSource();
    tds.setConnectionPoolDataSource(cpds);
    tds.setMaxTotal(10);
    tds.setDefaultMaxWaitMillis(50);
    dataSource = tds
  }

  static DataSource getDataSource() {
    return dataSource
  }
}
