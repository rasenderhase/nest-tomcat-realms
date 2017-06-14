# nest-tomcat-realms

[![Maven Central](https://img.shields.io/maven-central/v/de.nikem.nest/nest-tomcat-realms.svg)](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22nest-tomcat-realms%22)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Realms for usage in Apache Tomcat

taken from [StackOverflow - Implement a Tomcat Realm with LDAP authentication and JDBC authorization](https://stackoverflow.com/questions/1138450/implement-a-tomcat-realm-with-ldap-authentication-and-jdbc-authorization#2319514)

Put the jar into your `tomcat/lib` folder.

Combination of two realms. `JNDIRealm` and `DataSourceRealm`. `JNDIRealm` is used to authenticate user. 
`DataSourceRealm` is used to retrieve user roles (authorization). If `JNDIRealm` authentication is not possible
`LdapDataSourceRealm` tries to authenticate using `DataSourceRealm`.

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
