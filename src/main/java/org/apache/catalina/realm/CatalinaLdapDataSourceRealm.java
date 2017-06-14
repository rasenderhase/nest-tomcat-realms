package org.apache.catalina.realm;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.naming.directory.DirContext;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;


/**
 * CatalinaLdapDataSourceRealm is a minimal implementation of a <b>Realm</b> to connect to LDAP
 * for authentication and a database for authorization.<br>
 * <br>
 * Example server.xml configuration fragment:<br>
 * <pre>
        &lt;Realm 
            className="de.nikem.nest.tomcatrealms.ldapjdbc.LdapDataSourceRealm"
            
            connectionName="ad001\z00084or"
            connectionPassword="xxx"

            authentication="simple"
            connectionURL="ldaps://ad001.siemens.net:636/dc=ad001,dc=siemens,dc=net"
            referrals="follow"
            userSearch="(sAMAccountName={0})"
            userBase="OU=Users,OU=_Central,OU=40DE000,OU=40DE,OU=RA006" 
            userSubtree="true"
            
            dataSourceName="jdbc/shopfloor_local"
            
            roleNameCol="role_name" 
            userCredCol="password" 
            userNameCol="user_name" 
            userRoleTable="v_user_role" 
            userTable="&quot;USER&quot;"
            
            /&gt;
 * </pre>
 *
 * @author Greg Chabala, Andreas Knees
 * @see <a href="https://stackoverflow.com/questions/1138450/implement-a-tomcat-realm-with-ldap-authentication-and-jdbc-authorization#2319514">Stack Overflow</a>
 * @see <a href="https://github.com/rasenderhase/nest-tomcat-realms">GitHub</a>
 *
 */
public class CatalinaLdapDataSourceRealm extends JNDIRealm implements Realm
{
	private static final Log log = LogFactory.getLog(CatalinaLdapDataSourceRealm.class);
	/**
	 * Encapsulated <b>JDBCRealm</b> to do role lookups
	 */
	private final DataSourceRealm dataSourceRealm = new DataSourceRealm();

	protected static final String info = CatalinaLdapDataSourceRealm.class.getName() + "/" + CatalinaLdapDataSourceRealm.class.getPackage().getImplementationVersion();
	protected static final String name = CatalinaLdapDataSourceRealm.class.getSimpleName();

	@Override
	protected void initInternal() throws LifecycleException {
		super.initInternal();
		try {
			dataSourceRealm.init();
		} catch (LifecycleException e) {
			log.error("cannot initialize DataSourceRealm", e);
		}
		log.info("initialized DataSourceRealm");
	}
	
	@Override
	protected void startInternal() throws LifecycleException {
		super.startInternal();
		dataSourceRealm.start();
	}
	
	@Override
	public void setContainer(Container container) {
		super.setContainer(container);
		dataSourceRealm.setContainer(getContainer());
	}
	
	@Override
	public Principal authenticate(String username, String credentials) {
		Principal p = super.authenticate(username, credentials);
		log.info("Principal retrieved from JNDI: " + p);
		if (p == null) {
			log.info("Fallback to DataSource authentication");
			p = dataSourceRealm.authenticate(username, credentials);
			log.info("Principal retrieved from DataSource: " + p);
		}
		return p;
	}
	
	/**
	 * Set the all roles mode.
	 *
	 * @param allRolesMode authentication mode
	 */
	public void setAllRolesMode(String allRolesMode) {
		super.setAllRolesMode(allRolesMode);
		getDataSourceRealm().setAllRolesMode(allRolesMode);
	}

	/**
	 * Return the table that holds user data..
	 *
	 * @return table name
	 * @see JDBCRealm#getUserTable()
	 */
	public String getUserTable() {
		return getDataSourceRealm().getUserTable();
	}

	/**
	 * Set the table that holds user data.
	 *
	 * @param userTable The table name
	 * @see JDBCRealm#setUserTable(String)
	 */
	public void setUserTable(String userTable) {
		getDataSourceRealm().setUserTable(userTable);
	}

	/**
	 * Return the column in the user table that holds the user's name.
	 *
	 * @return username database column name
	 * @see JDBCRealm#getUserNameCol()
	 */
	public String getUserNameCol() {
		return getDataSourceRealm().getUserNameCol();
	}

	/**
	 * Set the column in the user table that holds the user's name.
	 *
	 * @param userNameCol The column name
	 * @see JDBCRealm#setUserNameCol(String)
	 */
	public void setUserNameCol(String userNameCol) {
		getDataSourceRealm().setUserNameCol(userNameCol);
	}

	/**
	 * Return the table that holds the relation between user's and roles.
	 *
	 * @return user role database table name
	 * @see JDBCRealm#getUserRoleTable()
	 */
	public String getUserRoleTable() {
		return getDataSourceRealm().getUserRoleTable();
	}

	/**
	 * Set the table that holds the relation between user's and roles.
	 *
	 * @param userRoleTable The table name
	 * @see JDBCRealm#setUserRoleTable(String)
	 */
	public void setUserRoleTable(String userRoleTable) {
		getDataSourceRealm().setUserRoleTable(userRoleTable);
	}

	/**
	 * Return the column in the user role table that names a role.
	 *
	 * @return role column name
	 * @see JDBCRealm#getRoleNameCol()
	 */
	public String getRoleNameCol() {
		return getDataSourceRealm().getRoleNameCol();
	}

	/**
	 * Set the column in the user role table that names a role.
	 *
	 * @param roleNameCol The column name
	 * @see JDBCRealm#setRoleNameCol(String)
	 */
	public void setRoleNameCol(String roleNameCol) {
		getDataSourceRealm().setRoleNameCol(roleNameCol);
	}
	
	/**
	 * @return the column in the user table that holds the user's credentials.
	 * @see org.apache.catalina.realm.DataSourceRealm#getUserCredCol()
	 */
	public String getUserCredCol() {
		return dataSourceRealm.getUserCredCol();
	}

	/**
	 * @param userCredCol the column in the user table that holds the user's credentials.
	 * @see org.apache.catalina.realm.DataSourceRealm#setUserCredCol(java.lang.String)
	 */
	public void setUserCredCol(String userCredCol) {
		dataSourceRealm.setUserCredCol(userCredCol);
	}

	@Override
	public SecurityConstraint[] findSecurityConstraints(Request request, Context context)
	{
		return getDataSourceRealm().findSecurityConstraints(request, context);
	}

	@Override
	public boolean hasUserDataPermission(Request request, Response response,
			SecurityConstraint []constraints) throws IOException
	{
		return getDataSourceRealm().hasUserDataPermission(request, response, constraints);
	}

	@Override
	public boolean hasResourcePermission(Request request, Response response,
			SecurityConstraint[]constraints,
			Context context) throws IOException
	{
		return getDataSourceRealm().hasResourcePermission(request, response, constraints, context);
	}

	@Override
	public boolean hasRole(Wrapper wrapper, Principal principal, String role) {
		return getDataSourceRealm().hasRole(wrapper, principal, role);
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
		return getDataSourceRealm().getRoles(user.getUserName());
	}

	/**
     * @return the name of the JNDI JDBC DataSource.
     */
	public String getDataSourceName() {
		return getDataSourceRealm().getDataSourceName();
	}

	/**
     * Set the name of the JNDI JDBC DataSource.
     *
     * @param dataSourceName the name of the JNDI JDBC DataSource
     */
	public void setDataSourceName(String dataSourceName) {
		getDataSourceRealm().setDataSourceName(dataSourceName);
	}

	public DataSourceRealm getDataSourceRealm() {
		return dataSourceRealm;
	}
}