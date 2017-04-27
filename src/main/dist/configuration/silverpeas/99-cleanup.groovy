import java.sql.SQLException
import groovy.sql.Sql

/**
 * This script if to clean up all the tables used by the Silverpeas Calendar Engine in the database
 * in order to recreate them.
 *
 * This script is executed only if the peculiar property DEV_CLEANUP_CALENDAR is set in the
 * config.properties configuration file. This is dedicated to the development version of
 * Silverpeas 6.0.0 until the backend of the Calendar Engine v1 is stable enough.
 */

if (settings.DEV_CLEANUP_CALENDAR) {
  log.info 'Clean up all the tables relative to the Silverpeas Calendar Engine'

  Sql sql = service.sql

  ['SB_Cal_Attendees',
   'SB_Cal_Categories',
   'SB_Cal_Attributes',
   'SB_Cal_Occurrences',
   'SB_Cal_Recurrence_Exception',
   'SB_Cal_Recurrence_DayOfWeek',
   'SB_Cal_Event',
   'SB_Cal_Recurrence',
   'SB_Cal_Components',
   'SB_Cal_Calendar'].each { table ->
    try{
      sql.execute('DROP TABLE ' + table)
    } catch (SQLException e) {
      // nothing to do, we pass to the next table
    }
  }

  sql.execute("delete from sr_packages where sr_package = 'calendar' and sr_version='001'")
}