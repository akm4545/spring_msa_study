#stage 1
#이미지 빌드
FROM openjdk:11-slim as build
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

#stage2
#스테이지 1에서 데이터 추출후 빌드 레이어 추출
FROM openjdk:11-slim
WORKDIR application

#jarmode 결과로 출력된 각 레이어 복사
COPY --from=build application/dependencies/ ./
COPY --from=build application/spring-boot-loader/ ./
COPY --from=build application/snapshot-dependencies/ ./
COPY --from=build application/application/ ./

#JarLauncher 로 실행
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]


