package nl.dekkr.pagefetcher.persistence

import com.typesafe.config.ConfigFactory
import net.fwbrasil.activate.ActivateContext
import net.fwbrasil.activate.storage.memory.TransientMemoryStorage
import net.fwbrasil.activate.storage.mongo.MongoStorage
import net.fwbrasil.activate.storage.prevayler.PrevaylerStorage
import net.fwbrasil.activate.storage.relational.idiom.postgresqlDialect

object persistenceContext extends ActivateContext {

  val dbFlavor = ConfigFactory.load().getString("nl.dekkr.pagefetcher.persistence.flavor").toLowerCase

  val storage = dbFlavor match {
    case "postgres" => new ModifiedPooledJdbcRelationalStorage {
      val user = Some(ConfigFactory.load().getString("nl.dekkr.pagefetcher.persistence.postgres.user"))
      val password = Some(ConfigFactory.load().getString("nl.dekkr.pagefetcher.persistence.postgres.password"))
      val databaseName = Some(ConfigFactory.load().getString("nl.dekkr.pagefetcher.persistence.postgres.databaseName"))
      val serverName = Some(ConfigFactory.load().getString("nl.dekkr.pagefetcher.persistence.postgres.serverName"))
      val portNumber = Some(ConfigFactory.load().getInt("nl.dekkr.pagefetcher.persistence.postgres.portNumber"))
      val dialect = postgresqlDialect
      val jdbcDriver = "org.postgresql.Driver"
    }
    case "mongo" => new MongoStorage {
      val host = ConfigFactory.load().getString("nl.dekkr.pagefetcher.persistence.mongo.host")
      override val port = ConfigFactory.load().getInt("nl.dekkr.pagefetcher.persistence.mongo.port")
      val db = ConfigFactory.load().getString("nl.dekkr.pagefetcher.persistence.mongo.db")
      override val authentication = Option((
        ConfigFactory.load().getString("nl.dekkr.pagefetcher.persistence.mongo.user"),
        ConfigFactory.load().getString("nl.dekkr.pagefetcher.persistence.mongo.password")
        ))
    }
    case "prevayler" => new PrevaylerStorage
    case "memory" => new TransientMemoryStorage
    case other: String =>
      throw new UnsupportedOperationException(s"Perstistence flavor not found or incorrect. " +
        s"Allowed types are: postgres, mongo, prevayler and memory. " +
        s"Found: $other")
  }

}

