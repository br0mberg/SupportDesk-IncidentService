# 🛠 Incident Service

📘 [Habr статья](https://habr.com/ru/articles/870640/)  
✉️ Email: andrey@brombin.ru

---

## 📖 Содержание

- [Обзор](#-обзор)
- [Функции](#-функции)
- [Технологии](#-технологии)
- [Требования](#-требования)
- [Запуск](#-запуск)
  - [1. Клонирование](#1-клонирование)
  - [2. Настройка конфигурации](#2-настройка-конфигурации)
  - [3. Создание Docker Network](#3-создание-docker-network)
  - [4. Запуск через Docker Compose](#4-запуск-через-docker-compose)
  - [5. Доступ к сервисам и мониторинг](#5-доступ-к-сервисам-и-мониторинг)
- [Файл Docker Compose](#-файл-docker-compose)
- [Обратная связь](#-обратная-связь)

---

## 🧭 Обзор

**Incident Service** — микросервис для регистрации и обработки пользовательских инцидентов, часть системы автоматизации поддержки.

### 💼 Возможности:

- Создание, редактирование, удаление и просмотр инцидентов
- Назначение приоритета, категории, статуса и ответственного
- Уведомление администраторов о новых инцидентах
- Асинхронная обработка изображений через Kafka
- Аутентификация по JWT Bearer через Keycloak
- Интеграция с [Image Service](https://github.com/br0mberg/SupportDesk-ImageService) и UserService (на доработке)

---

## 🚀 Функции

✅ CRUD API для инцидентов  
✅ gRPC-взаимодействие с другими сервисами  
✅ Kafka-подписки и публикации  
✅ Keycloak для авторизации/аутентификации  
✅ Документация через Swagger/OpenAPI

---

## 🔧 Технологии

| Категория         | Технологии                         |
|------------------|------------------------------------|
| ЯП                | Java 21                            |
| Backend           | Spring Boot, Spring Security, gRPC |
| БД                | PostgreSQL + Liquibase             |
| Брокер сообщений  | Apache Kafka + Kafka UI            |
| DevOps            | Docker, Docker Compose             |
| Маппинг           | MapStruct, Lombok                  |
| Документация API  | Swagger/OpenAPI                    |

---

## 📦 Требования

- [Docker](https://www.docker.com/)
- [JDK 21+](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [PostgreSQL](https://www.postgresql.org/) (если не через Docker)
- [Kafka](https://kafka.apache.org/downloads) (если не через Docker)
- [Keycloak](https://www.keycloak.org/) (если не через Docker)

---

## 🚀 Запуск

### 1. Клонирование

```bash
git clone https://github.com/br0mberg/SupportDesk-IncidentService
cd SupportDesk-IncidentService
```

### 2. Настройка конфигураций
Make sure to set up the following properties in your application.properties 
file located in src/main/resources/.

### 3. Создание docker-network
```bash
docker network create support-network
```
### 4. Запуск через docker-compose
Use the following docker-compose.yml to configure and start your containers.
```bash
docker-compose up --build
```
This will start the Incident Service, Kafka, Postgres, and all other required services containers.

### 5. Доступ к сервисам и мониторинг
- The Incident Service API will be accessible at http://localhost:8081.
- Kafka UI will be available at http://localhost:8383 for managing Kafka messages.
- Keycloak will be accessible at http://localhost:8080 for user authentication.
- Swagger UI will be accessible at http://localhost:8081/swagger-ui.html for api doc.

##🤝 Обратная связь

Если у вас возникли вопросы или предложения, свяжитесь со мной:
✉️ Email: andrey@brombin.ru

Приятного использования! 🎉



