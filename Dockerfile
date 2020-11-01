# builder
FROM gradle:jdk11 as build

WORKDIR /workspace/app

COPY src src
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN gradle assemble --no-daemon

# jar extractor
FROM openjdk:11-jdk as extractor

WORKDIR /workspace/app
COPY --from=build workspace/app/build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# runner
FROM openjdk:11-jdk

WORKDIR application
COPY --from=extractor /workspace/app/dependencies/ ./
COPY --from=extractor /workspace/app/snapshot-dependencies/ ./
COPY --from=extractor /workspace/app/spring-boot-loader/ ./
COPY --from=extractor /workspace/app/application/ ./

ARG profile
ENV SPRING_PROFILES_ACTIVE=$profile

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]