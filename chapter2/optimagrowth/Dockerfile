#stage 1
#이미지 빌드
FROM openjdk:11-slim as build
LABEL maintainer="Illary Huaylupo <illaryhs@gamil.com>"
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)

#stage2
#스테이지 1에서 데이터 추출후 빌드 레이어 추출
FROM openjdk:11-slim
VOLUME /tmp
ARG DEPENDENCY=/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.optimagrowth.license.LicenseServiceApplication"]


