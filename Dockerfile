FROM maven:3.8.5-openjdk-8 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM jboss/wildfly:18.0.1.Final
COPY --from=builder /app/target/ultima-aps.war /opt/jboss/wildfly/standalone/deployments/ultima-aps.war

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]