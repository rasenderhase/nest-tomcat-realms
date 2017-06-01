package org.apache.catalina.realm;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.naming.directory.DirContext;

import org.apache.catalina.Context;
import org.apache.catalina.Realm;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;


/**
 * LdapJdbcRealm is a minimal implementation of a <b>Realm</b> to connect to LDAP
 * for authentication and a database for authorization.<br>
 * <br>
 * Example server.xml configuration fragment:<br>
 * <pre>
   &lt;Realm className="org.apache.catalina.realm.LdapJdbcRealm"
      connectionURL="ldap://ldaphost:389"
      resourceName="LDAP Auth" driverName="oracle.jdbc.driver.OracleDriver"
      userPattern="uid={0}, ou=Portal, dc=example, dc=com"
      dbConnectionName="dbuser" dbConnectionPassword="dbpassword"
      dbConnectionURL="jdbc:oracle:thin:@oracledb:1521:dbname"
      userTable="users" userNameCol="user_id"
      userRoleTable="user_role_xref" roleNameCol="role_id" /&gt;
 * </pre>
 *
 * @author Greg Chabala, Andreas Knees
 * @see <a href="https://stackoverflow.com/questions/1138450/implement-a-tomcat-realm-with-ldap-authentication-and-jdbc-authorization#2319514">Stack Overflow</a>
 * @see <a href="https://github.com/rasenderhase/nest-tomcat-realms">GitHub</a>
 *
 */
public class CatalinaLdapJdbcRealm extends JNDIRealm implements Realm
{
	/**
	 * Encapsulated <b>JDBCRealm</b> to do role lookups
	 */
	private JDBCRealm jdbcRealm = new JDBCRealm();

	protected static final String info = CatalinaLdapJdbcRealm.class.getName() + "/" + CatalinaLdapJdbcRealm.class.getPackage().getImplementationVersion();
	protected static final String name = CatalinaLdapJdbcRealm.class.getSimpleName();

	/**
	 * Set the all roles mode.
	 *
	 * @param allRolesMode authentication mode
	 */
	public void setAllRolesMode(String allRolesMode) {
		super.setAllRolesMode(allRolesMode);
		jdbcRealm.setAllRolesMode(allRolesMode);
	}

	/**
	 * Return the username to use to connect to the database.
	 *
	 * @return username
	 * @see JDBCRealm#getConnectionName()
	 */
	public String getDbConnectionName() {
		return jdbcRealm.getConnectionName();
	}

	/**
	 * Set the username to use to connect to the database.
	 *
	 * @param dbConnectionName username
	 * @see JDBCRealm#setConnectionName(String)
	 */
	public void setDbConnectionName(String dbConnectionName) {
		jdbcRealm.setConnectionName(dbConnectionName);
	}

	/**
	 * Return the password to use to connect to the database.
	 *
	 * @return password
	 * @see JDBCRealm#getConnectionPassword()
	 */
	public String getDbConnectionPassword() {
		return jdbcRealm.getConnectionPassword();
	}

	/**
	 * Set the password to use to connect to the database.
	 *
	 * @param dbConnectionPassword password
	 * @see JDBCRealm#setConnectionPassword(String)
	 */
	public void setDbConnectionPassword(String dbConnectionPassword) {
		jdbcRealm.setConnectionPassword(dbConnectionPassword);
	}

	/**
	 * Return the URL to use to connect to the database.
	 *
	 * @return database connection URL
	 * @see JDBCRealm#getConnectionURL()
	 */
	public String getDbConnectionURL() {
		return jdbcRealm.getConnectionURL();
	}

	/**
	 * Set the URL to use to connect to the database.
	 *
	 * @param dbConnectionURL The new connection URL
	 * @see JDBCRealm#setConnectionURL(String)
	 */
	public void setDbConnectionURL(String dbConnectionURL) {
		jdbcRealm.setConnectionURL(dbConnectionURL);
	}

	/**
	 * Return the JDBC driver that will be used.
	 *
	 * @return driver classname
	 * @see JDBCRealm#getDriverName()
	 */
	public String getDriverName() {
		return jdbcRealm.getDriverName();
	}

	/**
	 * Set the JDBC driver that will be used.
	 *
	 * @param driverName The driver name
	 * @see JDBCRealm#setDriverName(String)
	 */
	public void setDriverName(String driverName) {
		jdbcRealm.setDriverName(driverName);
	}

	/**
	 * Return the table that holds user data..
	 *
	 * @return table name
	 * @see JDBCRealm#getUserTable()
	 */
	public String getUserTable() {
		return jdbcRealm.getUserTable();
	}

	/**
	 * Set the table that holds user data.
	 *
	 * @param userTable The table name
	 * @see JDBCRealm#setUserTable(String)
	 */
	public void setUserTable(String userTable) {
		jdbcRealm.setUserTable(userTable);
	}

	/**
	 * Return the column in the user table that holds the user's name.
	 *
	 * @return username database column name
	 * @see JDBCRealm#getUserNameCol()
	 */
	public String getUserNameCol() {
		return jdbcRealm.getUserNameCol();
	}

	/**
	 * Set the column in the user table that holds the user's name.
	 *
	 * @param userNameCol The column name
	 * @see JDBCRealm#setUserNameCol(String)
	 */
	public void setUserNameCol(String userNameCol) {
		jdbcRealm.setUserNameCol(userNameCol);
	}

	/**
	 * Return the table that holds the relation between user's and roles.
	 *
	 * @return user role database table name
	 * @see JDBCRealm#getUserRoleTable()
	 */
	public String getUserRoleTable() {
		return jdbcRealm.getUserRoleTable();
	}

	/**
	 * Set the table that holds the relation between user's and roles.
	 *
	 * @param userRoleTable The table name
	 * @see JDBCRealm#setUserRoleTable(String)
	 */
	public void setUserRoleTable(String userRoleTable) {
		jdbcRealm.setUserRoleTable(userRoleTable);
	}

	/**
	 * Return the column in the user role table that names a role.
	 *
	 * @return role column name
	 * @see JDBCRealm#getRoleNameCol()
	 */
	public String getRoleNameCol() {
		return jdbcRealm.getRoleNameCol();
	}

	/**
	 * Set the column in the user role table that names a role.
	 *
	 * @param roleNameCol The column name
	 * @see JDBCRealm#setRoleNameCol(String)
	 */
	public void setRoleNameCol(String roleNameCol) {
		jdbcRealm.setRoleNameCol(roleNameCol);
	}

	@Override
	public SecurityConstraint[] findSecurityConstraints(Request request, Context context)
	{
		return jdbcRealm.findSecurityConstraints(request, context);
	}

	@Override
	public boolean hasUserDataPermission(Request request, Response response,
			SecurityConstraint []constraints) throws IOException
	{
		return jdbcRealm.hasUserDataPermission(request, response, constraints);
	}

	@Override
	public boolean hasResourcePermission(Request request, Response response,
			SecurityConstraint[]constraints,
			Context context) throws IOException
	{
		return jdbcRealm.hasResourcePermission(request, response, constraints, context);
	}

	@Override
	public boolean hasRole(Wrapper wrapper, Principal principal, String role) {
		return jdbcRealm.hasRole(wrapper, principal, role);
	}

	/**
	 * Return a List of roles associated with the given User. If no roles
	 * are associated with this user, a zero-length List is returned.
	 *
	 * @param context unused. JDBC does not need this field.
	 * @param user The User to be checked
	 * @return list of role names
	 *
	 * @see JNDIRealm#getRoles(DirContext, User)
	 * @see JDBCRealm#getRoles(String) 
	 */
	@Override
	protected List<String> getRoles(DirContext context, User user)
	{
		return jdbcRealm.getRoles(user.getUserName());
	}
}