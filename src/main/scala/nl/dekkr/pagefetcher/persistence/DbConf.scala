package nl.dekkr.pagefetcher.persistence

import nl.dekkr.pagefetcher.model.Constants
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

trait DbConf extends Constants {

  val dc = DatabaseConfig.forConfig[JdbcProfile](s"$CONFIG_BASE.persistence.slick")

}
