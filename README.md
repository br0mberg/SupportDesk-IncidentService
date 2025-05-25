# 🛠 Incident Service

📘 [Habr статья](https://habr.com/ru/articles/870640/)  
✉️ Telegram: [@brombinandrey](https://t.me/brombinandrey)

---

## 📖 Содержание

- [Обзор](#-обзор)
- [Функции](#-функции)
- [Технологии](#-технологии)
- [Требования](#-требования)
- [Запуск](#-запуск)
  - [1. Клонирование](#1-клонирование)
  - [2. Настройка конфигураций](#2-настройка-конфигураций)
  - [3. Запуск через Docker Compose](#3-запуск-через-docker-compose)
- [Ссылки на сервисы](#-ссылки-на-сервисы)
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
- Интеграция с [User Service](#) и [Image Service](#)

---

## 🚀 Функции

✅ CRUD API для инцидентов  
✅ gRPC-взаимодействие с другими сервисами  
✅ Kafka-подписки и публикации  
✅ Keycloak для авторизации/аутентификации  
✅ Метрики для Prometheus  
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
| Мониторинг        | Prometheus, Grafana                |
| Документация API  | Swagger/OpenAPI                    |

---

## 📦 Требования

- [Docker](https://www.docker.com/)
- JDK 21+
- PostgreSQL (если не через Docker)
- Kafka (если не через Docker)
- [Keycloak](https://www.keycloak.org/)

---

## 🚀 Запуск

### 1. Клонирование

```bash
git clone https://github.com/br0mberg/SupportDesk-IncidentService
cd SupportDesk-IncidentService

#### 2. Configure your application.properties
Make sure to set up the following properties in your application.properties 
file located in src/main/resources/.

### 3. Set up Docker Compose
Use the following docker-compose.yml to configure and start your containers.
```bash
docker-compose up --build
```
This will start the Incident Service, Kafka, Postgres, and all other required services containers.

### 4. Access the services
- The Incident Service API will be accessible at http://localhost:8081.
- Kafka UI will be available at http://localhost:8383 for managing Kafka messages.
- Keycloak will be accessible at http://localhost:8080 for user authentication.
- Swagger UI will be accessible at http://localhost:8081/swagger-ui.html for api doc.

TODO: UserService integration
For more details on available endpoints, please refer to the Swagger documentation at http://localhost:8081/swagger-ui.html.

Happy using! 🎉

Let me know if you need any more adjustments!
