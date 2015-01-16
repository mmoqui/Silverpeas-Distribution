import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Create the tickers from sample files.
 * @neysseric
 */

println 'Generate the tickers from samples'
Path tickerHome = API.get("${settings.SILVERPEAS_DATA_HOME}/web/weblib.war/ticker")
if (Files.exists(tickerHome) && Files.isDirectory(tickerHome)) {
  tickerHome.toFile().eachFileMatch(~/sample_.*/) {
    File aTicker = new File(it.name.replaceAll('sample_', ''), tickerHome.toFile())
    if (!aTicker.exists()) {
      println "  ->  ${aTicker.name}"
      Files.move(Paths.get(it.path), Paths.get(aTicker.path))
    }
  }
}
