/**
 * This script is to setup and configure various things that can be necessary for Silverpeas.
 * @author mmoquillon
 */

/* Creates the hidden Silverpeas directory for important stuffs if it doesn't already exist */
service.createDirectory(settings.HIDDEN_SILVERPEAS_DIR.asPath(),
    [readable: true, writable: true, executable: true, hidden: true])