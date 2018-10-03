import java.nio.file.Files
import java.nio.file.Path

/**
 * This script scans all the existing WYSIWYGs to replace the links targeting FileServer and written
 * in the old and deprecated format by a link respecting the new format.
 * @author mmoquillon
 */

log.info 'Replace old FileServer links in existing WYSIWYG contents'

Path workspacePath = "${settings.SILVERPEAS_DATA_HOME}/workspaces".asPath()
if (Files.exists(workspacePath) && Files.isDirectory(workspacePath)) {
  workspacePath.toFile().eachDirRecurse { dir ->
    dir.eachFileMatch(~/.*wysiwyg.*.txt/) { wysiwyg ->
      String content = wysiwyg.text
      String replaced = content.replaceAll(/FileServer\/([a-zA-Z0-9.-_]+)\?ComponentId=([a-zA-Z0-9]+)&(amp;)?attachmentId=(\d+)/,
          'attached_file/componentId/$2/attachmentId/$4/lang/fr/name/$1')
      if (replaced != content) {
        log.info " -> replace old link(s) in ${wysiwyg.path}"
        wysiwyg.write(replaced)
      }
    }
  }
}
