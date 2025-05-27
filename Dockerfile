FROM openjdk:21
COPY target/chessclub-1.0.jar chessclub.jar
ENTRYPOINT ["java","-jar","/chessclub.jar" ]