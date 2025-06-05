FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./

RUN mvn wrapper:wrapper -Dmaven=3.9.6

RUN if [ ! -f .mvn/wrapper/maven-wrapper.properties ]; then \
    echo "Creating maven-wrapper.properties" && \
    mkdir -p .mvn/wrapper && \
    echo "distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip" > .mvn/wrapper/maven-wrapper.properties && \
    echo "wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" >> .mvn/wrapper/maven-wrapper.properties; \
  elif ! grep -q "distributionUrl" .mvn/wrapper/maven-wrapper.properties; then \
    echo "Adding distributionUrl to maven-wrapper.properties" && \
    echo "distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip" >> .mvn/wrapper/maven-wrapper.properties && \
    echo "wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" >> .mvn/wrapper/maven-wrapper.properties; \
  fi && \
  cat .mvn/wrapper/maven-wrapper.properties && \
  chmod +x ./mvnw && \
  ./mvnw --version

RUN ./mvnw dependency:go-offline -B

COPY src/ src/

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]