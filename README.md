spring-saml-adfs
================

SSO example for Spring-based application for the integration with Windows domain accounts via ADFS 2.0. This is based on the Spring Security SAML project [on GitHub](https://github.com/SpringSource/spring-security-saml). 

Prerequisites
-------------
* ADFS 2.0 properly installed on a Windows Server box. This acts as an IdP.
* Clone the project to your sandbox, this acts as an SP. Make sure it is compilable using Maven.
* The IdP machine (ADFS) and the SP machine (your sandbox) must see each other via DNS reference. To do this within your LAN/VLAN, simply modify the HOSTS file on both machines pointing to the IP addresses of both.
* You will need administrator access on the AD FS to import the SAML metadata from the SP (see reference 4)

For my testing, a VM with Windows Server 2008 R2 + AD DS + AD FS 2.0 was configured and named `dc01.hrboss.local`, managing user accounts in the domain `hrboss.local`. My sandbox is named as `sp.hrboss.local`. In the HOSTS files on both machines, two records are added:

> 192.168.60.192	dc01.hrboss.local

> 192.168.60.180	sp.hrboss.local

To verify the setting, create an account in the AD and use this link to log in:

> https://dc01.hrboss.local/adfs/ls/IdpInitiatedSignon.aspx

Of course, make sure you can log in successfully using the testing account :)

Usage
-----

Start up the application by triggering the Maven task from the project root:

> mvn tomcat7:run

ADFS requires secure connection for the communication to the SP application, thus the embedded Tomcat server is configured to be SSL-enabled and trust the certificate of the AD server (see reference 1, 2 and 3). Once started up, access the application via the link:

> https://sp.hrboss.local:8443/spring-security-saml2-sample/

Enter the domain user account in the pop-up box, and there you go!


Reference
---------

1. To generate the keystore containing the private key of your Tomcat server:
  `keytool -genkey -alias tomcat7 -keyalg RSA -keystore src\main\resources\tomcat\tomcat-ssl.keystore`
2. To import the AD server's certificate to your trust store:
  `keytool -import -trustcacerts -alias hrboss.local -file D:/Share/licenses-certificates/hrboss.local.crt -keystore src\main\resources\tomcat\tomcat-ssl.keystore`
3. To check all the certs that were imported:
  `keytool -v -list -keystore src\main\resources\tomcat\tomcat-ssl.keystore`
4. To import the SAML metadata from SP to the ADFS, follow [the instruction](http://docs.spring.io/spring-security-saml/docs/current/reference/html/chapter-idp-guide.html) on the referenced project:

* In AD FS 2.0 Management Console select "Add Relying Party Trust"
* Select "Import data about the relying party from a file" and select the metadata.xml file created earlier. Select Next
* The wizard may complain that some content of metadata is not supported. You can safely ignore this warning
* Continue with the wizard. On the "Ready to Add Trust" make sure that tab endpoints contains multiple endpoint values. If not, verify that your metadata was generated with HTTPS protocol URLs
* Leave "Open the Edit Claim Rules dialog" checkbox checked and finish the wizard
* Select "Add Rule", choose "Send LDAP Attributes as Claims" and press Next
* Add NameID as "Claim rule name", choose "Active Directory" as Attribute store, choose "SAM-Account-Name" as LDAP Attribute and "Name ID" as "Outgoing claim type", finish the wizard and confirm the claim rules window
* Open the provider by double-clicking it, select tab Advanced and change "Secure hash algorithm" to SHA-1

Troubleshooting
---------------

1. While running the SP application, if you face this issue "org.apache.xml.security.encryption.XMLEncryptionException: Illegal key size", this is due to you JRE policy doesn't support unlimited strength jurisdiction policy file. To fix this, refer to [this link](http://suhothayan.blogspot.com/2012/05/how-to-install-java-cryptography.html), or follow these steps:
  i. Go to the Oracle Java SE download page http://www.oracle.com/technetwork/java/javase/downloads/index.html
  ii. Scroll down ... Under "Additional Resources" section you will find "Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy File"
  iii. Download the version that matches your installed JVM E.g. UnlimitedJCEPolicyJDK7.zip
  iv. Unzip the downloaded zip
  v. Copy local_policy.jar and US_export_policy.jar to the $JAVA_HOME/jre/lib/security (Note: these jars will be already there so you have to overwrite them)
  vi. Then restart your application to get rid of this exception.

