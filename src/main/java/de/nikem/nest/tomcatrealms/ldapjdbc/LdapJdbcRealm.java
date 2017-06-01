package de.nikem.nest.tomcatrealms.ldapjdbc;

import org.apache.catalina.realm.CatalinaLdapJdbcRealm;

/**
 * LdapJdbcRealm is a minimal implementation of a <b>Realm</b> to connect to LDAP
 * for authentication and a database for authorization.<br>
 * <br>
 * Example server.xml configuration fragment:<br>
 * <pre>
   &lt;Realm className="de.nikem.nest.tomcatrealms.ldapjdbc.LdapJdbcRealm"
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
public class LdapJdbcRealm extends CatalinaLdapJdbcRealm {

}
