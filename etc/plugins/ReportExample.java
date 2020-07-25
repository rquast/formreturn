package com.ebstrada.formreturn.api.export.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import com.ebstrada.formreturn.api.messaging.ProcessingStatusDialog;
import com.ebstrada.formreturn.api.database.JPAManager;
import com.ebstrada.formreturn.api.export.Report;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;

@PluginImplementation
public class ReportExample implements Report {

	private ProcessingStatusDialog processingStatusDialog;

	/*
	 * Private inner class required for loading JDBC driver
	 * at runtime and registering with the DriverManager.
	 * Thanks to: http://www.kfu.com/~nsayer/Java/dyn-jdbc.html
	 */
	private class DriverShim implements Driver {
		private Driver driver;

		DriverShim(Driver d) {
			this.driver = d;
		}

		public boolean acceptsURL(String u) throws SQLException {
			return this.driver.acceptsURL(u);
		}

		public Connection connect(String u, Properties p) throws SQLException {
			return this.driver.connect(u, p);
		}

		public int getMajorVersion() {
			return this.driver.getMajorVersion();
		}

		public int getMinorVersion() {
			return this.driver.getMinorVersion();
		}

		public DriverPropertyInfo[] getPropertyInfo(String u, Properties p)
				throws SQLException {
			return this.driver.getPropertyInfo(u, p);
		}

		public boolean jdbcCompliant() {
			return this.driver.jdbcCompliant();
		}
	}

	@Override
	public void reportOnPublications(ArrayList<Long> publicationIds) throws Exception {

		loadMySQLDriver();

		EntityManager em = JPAManager.getEntityManager();

		if (em == null) {
			throw new Exception("JPA EntityManager Not Found.");
		}

		try {

			if (this.processingStatusDialog != null) {
				this.processingStatusDialog.setVisible(true);
				this.processingStatusDialog.setMessage("Exporting Publication(s) to MySQL");
			}

			for (Long publicationId : publicationIds) {
				Publication pub = em.find(Publication.class, publicationId);
				copyFormDataToMySQL(pub);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
			if (this.processingStatusDialog != null) {
				this.processingStatusDialog.dispose();
			}
		}

	}

	@Override
	public void reportOnForms(ArrayList<Long> formIds) throws Exception {

		loadMySQLDriver();

		EntityManager em = JPAManager.getEntityManager();

		if (em == null) {
			throw new Exception("JPA EntityManager Not Found.");
		}

		try {
			if (this.processingStatusDialog != null) {
				this.processingStatusDialog.setVisible(true);
				this.processingStatusDialog.setMessage("Exporting Form(s) to MySQL");
			}
			copyFormDataToMySQL(em, formIds);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
			if (this.processingStatusDialog != null) {
				this.processingStatusDialog.dispose();
			}
		}

	}

	private Connection getConnection() throws SQLException {

		// Replace the following information with your MySQL server, database, username and password details.
		// You must have read/write/create permissions set for the user of this database.
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost/test", "", "");

		return con;

	}

	private void loadMySQLDriver() throws InstantiationException, IllegalAccessException, ClassNotFoundException, MalformedURLException, SQLException {
		// Replace the file location and driver name with the one you download from the MySQL Connector/J website.
		// See http://dev.mysql.com/downloads/connector/j/5.0.html
		try {
			Driver drv = DriverManager.getDriver("jdbc:mysql://");
			if (drv != null) {
				return;
			}
		} catch (Exception ex) {
		}

		URL[] urls = new URL[]{new File("C:\\mysql-connector-java-5.0.8-bin.jar").toURI().toURL()};
		URLClassLoader ucl = new URLClassLoader(urls, DriverManager.class.getClassLoader());

		Driver obj = (Driver) Class.forName("com.mysql.jdbc.Driver", true, ucl).newInstance();
		DriverManager.registerDriver(new DriverShim(obj));
	}

	private void copyFormDataToMySQL(EntityManager em, List<Long> formIds) throws Exception {

		Connection con = null;

		try {
			con = getConnection();
			if (con.isClosed()) {
				throw new SQLException("Connection is closed.");
			}
			for (Long formId : formIds) {
				if (this.processingStatusDialog.isInterrupted()) {
					throw new Exception("User Aborted");
				}
				Form form = em.find(Form.class, formId);
				copyFormRecord(con, form);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
			}
		}

	}

	private void copyFormRecord(Connection con, Form form) throws SQLException {
		// TODO: This code needs to be completed.
		Statement s = con.createStatement();
		int count;
		s.executeUpdate("DROP TABLE IF EXISTS animal");
		s.executeUpdate(
				"CREATE TABLE animal ("
						+ "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
						+ "PRIMARY KEY (id),"
						+ "name CHAR(40), category CHAR(40))");
		count = s.executeUpdate(
				"INSERT INTO animal (name, category)"
						+ " VALUES"
						+ "('snake', 'reptile'),"
						+ "('frog', 'amphibian'),"
						+ "('tuna', 'fish'),"
						+ "('racoon', 'mammal')");
		s.close();
		System.out.println(count + " rows were inserted");
	}

	private void copyFormDataToMySQL(Publication publication) throws Exception {

		List<Form> forms = publication.getFormCollection();

		Connection con = null;

		try {
			con = getConnection();
			if (con.isClosed()) {
				throw new SQLException("Connection is closed.");
			}
			for (Form form : forms) {
				if (this.processingStatusDialog.isInterrupted()) {
					throw new Exception("User Aborted");
				}
				copyFormRecord(con, form);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
			}
		}

	}

	@Override
	public void setProcessingStatusDialog(
			ProcessingStatusDialog processingStatusDialog) {
		this.processingStatusDialog = processingStatusDialog;
	}

}
