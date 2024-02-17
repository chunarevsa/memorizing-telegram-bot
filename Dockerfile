# Используем образ Maven для сборки проекта
FROM maven:3.8.4-openjdk-11 AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы с зависимостями и сборки
COPY pom.xml .
COPY src src

# Собираем проект
RUN mvn -B clean package -DskipTests

# Используем минимальный образ с JRE
FROM openjdk:11-jre-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR файл из предыдущего этапа сборки
COPY --from=builder /app/target/memorizing-telegram-bot-1.0-SNAPSHOT.jar .

# Задаем команду для запуска приложения
CMD ["java", "-jar", "memorizing-telegram-bot-1.0-SNAPSHOT.jar"]
