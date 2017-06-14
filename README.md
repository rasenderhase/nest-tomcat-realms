# nest-tomcat-realms
Realms for usage in Apache Tomcat

taken from [StackOverflow - Implement a Tomcat Realm with LDAP authentication and JDBC authorization](https://stackoverflow.com/questions/1138450/implement-a-tomcat-realm-with-ldap-authentication-and-jdbc-authorization#2319514)

Combination of two realms. JNDIRealm and DataSourceRealm. JNDIRealm is used to authenticate user. 
DataSourceRealm is used to retrieve user roles (authorization). If JNDIRealm authentication is not possible
LdapDataSourceRealm tries to authenticate using DataSourceRealm.

        <Realm 
            className="de.nikem.nest.tomcatrealms.ldapjdbc.LdapDataSourceRealm"
            
            connectionName="domain\technicalLdapBrowserUser"
            connectionPassword="xxxx"

            authentication="simple"
            connectionURL="ldap://domain/dc=domain,dc=company,dc=net"
            referrals="follow"
            userSearch="(sAMAccountName={0})"
            userBase="OU=Users" 
            userSubtree="true"
            
            dataSourceName="jdbc/database"
            
            roleNameCol="role_name" 
            userCredCol="password" 
            userNameCol="user_name" 
            userRoleTable="v_user_role" 
            userTable="&quot;USER&quot;"
            
            />