FROM openjdk:18

RUN mkdir -p usr/src/app
WORKDIR /usr/src/app
ADD . /usr/src/app

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY target/ms-auth-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]