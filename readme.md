# Mini-app Nace

Spring Boot web application to provide REST API in JSON to
 - upload and persist csv file
 - get list of persisted data
 - get nace by its order

## 1. Getting started

### 1.1. Clone Project

```
$ git clone https://github.com/lobnaKh/nace-miniapp.git
```

### 1.2. Launch the application

```
$ mvn spring-boot:run
```

### 1.3. Technological stack
```
Java 17 - Spring boot 3.2.4
OpenCsv 5.7.1
```

### 1.4. API

Method | Path                 | Description                    |
-------|----------------------|--------------------------------|
POST   | /api/csv/upload      | upload a csv file and persist data           |
GET    | /api/naces           | retrieve all the naces         |
GET    | /api/nace/{order}   | retrieve one nace by its ORDER |

