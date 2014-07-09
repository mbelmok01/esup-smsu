sh /usr/local/apache-tomcat-8.0.9/bin/shutdown.sh

mvn clean install package

mv ./target/esup-smsu-2.0.0-RC1.war ./target/esup-smsu.war

rm -rf /usr/local/apache-tomcat-8.0.9/webapps/esup-smsu

rm -rf /usr/local/apache-tomcat-8.0.9/webapps/esup-smsu.war

cp ./target/esup-smsu.war /usr/local/apache-tomcat-8.0.9/webapps

sh /usr/local/apache-tomcat-8.0.9/bin/startup.sh