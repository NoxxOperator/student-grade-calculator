
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY StudentGradeServer.java .
COPY index.html .

RUN javac StudentGradeServer.java

ENV PORT=10000

EXPOSE 10000

CMD ["java", "StudentGradeServer"]
