import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * This script generates the tickers from sample files.
 * @neysseric
 */

log.info 'Generate the tickers from samples'
Path tickerHome = "${settings.SILVERPEAS_DATA_HOME}/web/weblib.war/ticker".asPath()
if (Files.exists(tickerHome) && Files.isDirectory(tickerHome)) {
  tickerHome.toFile().eachFileMatch(~/sample_.*/) {
    File aTicker = new File(it.name.replaceAll('sample_', ''), tickerHome.toFile())
    if (!aTicker.exists()) {
      log.info " -> ${aTicker.name}"
      Files.move(Paths.get(it.path), Paths.get(aTicker.path))
    }
  }
}
