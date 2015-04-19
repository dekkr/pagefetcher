package nl.dekkr.pagefetcher.persistence

import javax.sql.DataSource

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import net.fwbrasil.activate.storage.relational.PooledJdbcRelationalStorage

/**
 * The PGSimpleDataSource does not support the url parameter, but requires a databaseName, serverName and portNumber.
 * This trait is a slightly modified version of the net.fwbrasil.activate.storage.relational.PooledJdbcRelationalStorage
 */
trait ModifiedPooledJdbcRelationalStorage extends PooledJdbcRelationalStorage {

  override val url: String = "DO NOT USE: Url has been replace by the databaseName, serverName & portNumber properties"
  // Added 3 new properties,
  val databaseName: Option[String]
  val serverName: Option[String]
  val portNumber: Option[Int]

  private var _connectionPool: DataSource = _

  override def getConnection =
    _connectionPool.getConnection

  override def delayedInit(body: => Unit) = {
    body
    val jdbcDataSourceName: String = jdbcDataSource match {
      case Some(name) => name
      case None => jdbcDriverToDataSource.get(jdbcDriver) match {
        case Some(name) => name
        case None => throw new Exception("Could not find provided jdbcDriver, please provide a jdbcDataSource instead.")
      }
    }
    initConnectionPool(jdbcDataSourceName)
  }

  protected def initConnectionPool(jdbcDataSourceName: String) = {
    //ActivateContext.loadClass(jdbcDataSourceName)
    //_connectionPool = new HikariDataSource(dialect.hikariConfigFor(this, jdbcDataSourceName))
    this.getClass.getClassLoader.loadClass(jdbcDataSourceName)
    _connectionPool = new HikariDataSource(changeHikariConfig(dialect.hikariConfigFor(this, jdbcDataSourceName)))
  }

  protected def changeHikariConfig(config: HikariConfig): HikariConfig = {
    config.getDataSourceProperties.remove("url")
    databaseName map { d => config.addDataSourceProperty("databaseName", d) }
    serverName map { s => config.addDataSourceProperty("serverName", s) }
    portNumber map { p => config.addDataSourceProperty("portNumber", p) }
    config
  }

  private val jdbcDriverToDataSource = Map(
    "org.postgresql.Driver" -> "org.postgresql.ds.PGSimpleDataSource",
    "com.mysql.jdbc.Driver" -> "com.mysql.jdbc.jdbc2.optional.MysqlDataSource",
    "oracle.jdbc.driver.OracleDriver" -> "oracle.jdbc.pool.OracleDataSource",
    "org.h2.Driver" -> "org.h2.jdbcx.JdbcDataSource",
    "org.apache.derby.jdbc.EmbeddedDriver" -> "org.apache.derby.jdbc.EmbeddedDataSource",
    "org.hsqldb.jdbcDriver" -> "org.hsqldb.jdbc.JDBCDataSource",
    "com.ibm.db2.jcc.DB2Driver" -> "com.ibm.db2.jcc.DB2SimpleDataSource",
    "net.sourceforge.jtds.jdbc.Driver" -> "net.sourceforge.jtds.jdbcx.JtdsDataSource"
  )

}

