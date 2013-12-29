package kz.bee.hibernate.connection;

import java.sql.Connection;
import java.sql.SQLException;

import kz.bee.util.QLog;

import org.jivesoftware.database.DbConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ConnectionProvider implements org.hibernate.service.jdbc.connections.spi.ConnectionProvider {
	
	private static final Logger log = LoggerFactory.getLogger(ConnectionProvider.class);
	
	public ConnectionProvider() {
		QLog.info("Loading hibernate connection provider");
	}
	
	public void closeConnection(Connection conn) throws SQLException {
		DbConnectionManager.closeConnection(conn);
		QLog.info("Closed connection by Hibernate");
	}

	public Connection getConnection() throws SQLException {
		QLog.info("Getting connection by Hibernate");
		return DbConnectionManager.getConnection();
	}

	public boolean supportsAggressiveRelease() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isUnwrappableAs(Class arg0) {
		return false;
	}

	public <T> T unwrap(Class<T> arg0) {
		return null;
	}

}
