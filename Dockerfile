FROM fabric8/java-jboss-openjdk8-jdk:1.5.2
ENV JAVA_APP_DIR=/jdbcapp
EXPOSE 8080 8778
COPY target/test-jdbc-1.0-SNAPSHOT.jar /jdbcapp/
