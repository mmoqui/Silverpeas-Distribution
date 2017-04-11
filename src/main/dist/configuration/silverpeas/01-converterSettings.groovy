/**
 * This script configures the host and the port at which is listening a LibreOffice program running
 * as a daemon.
 * @author mmoquillon
 */

log.info 'Configure the access to an external Document Conversion Service'

def properties = [
  'openoffice.port': settings.CONVERTER_PORT != null ? settings.CONVERTER_PORT : '8100', 
  'openoffice.host': settings.CONVERTER_HOST != null ? settings.CONVERTER_HOST : '']

service.updateProperties(
  "${settings.SILVERPEAS_HOME}/properties/org/silverpeas/converter/openoffice.properties",
  properties)

