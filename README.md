![Continous Integration](https://github.com/MetalDetectorRocks/metal-detector-main/workflows/Continous%20Integration/badge.svg)
[![codecov](https://codecov.io/gh/MetalDetectorRocks/metal-detector-main/branch/master/graph/badge.svg)](https://codecov.io/gh/MetalDetectorRocks/metal-detector-main)

## Table of contents
1. [ Introduction ](#introduction)

2. [ Download source code ](#download-source-code)

3. [ Run application locally (Default profile) ](#run-application-locally-default)

4. [ Run application locally (Preview profile) ](#run-application-locally-preview)

5. [ Start the application ](#start-application)

<a name="introduction"></a>
## 1 Introduction
This repository contains the source code for the Metal Detector application. The application is a Spring Boot application and currently under development. 

The following features are planned:
- Mark your favorite metal bands
- Receive regular email alerts about upcoming and recently released albums from your favorite bands

The application uses the REST API from Discogs.

<a name="download-source-code"></a>
## 2 Download source code

Clone the source code via:

```
git clone https://github.com/MetalDetectorRocks/metal-detector-main.git
```

<a name="run-application-locally-default"></a>
## 3 Run application locally (Default profile)

To start the application locally in default profile, the following preparatory actions are necessary:

1. Install Java 11

2. Install Docker CE

3. Create folder `.secrets` below the project root directory and create the following files within this folder
    - `butler_db_root_password.txt`
    - `detector_db_root_password.txt`

4. Enter a password of your choice in each file created. The files are used for the `docker-compose.yml` file, which becomes relevant in a moment.

5. Expose the environment variable `BUTLER_DB_ROOT_PASSWORD` with value from file `butler_db_root_password.txt` to inject the database password into the Metal Release Butler docker container.

6. Run `docker-compose.yml` via command `docker-compose up -d --no-recreate`. This starts all peripheral docker containers that are needed locally to run the Metal Detector Application:
    - `detector-db`: The database for Metal Detector application (currently MySQL)
    - `detector-phpmyadmin`: phpmyadmin for Metal Detectors database
    - `butler-db`: The database for Metal Release Butler application 
    - `butler-phpmyadmin`: phpmyadmin for Metal Release Butlers database
    - `butler-app`: Metal Release Butler Spring Boot application

4. Define the data source connection details in file `application.yml`:
    - `spring.datasource.username` (you have to use user `postgres`)
    - `spring.datasource.password` (password must match `POSTGRES_PASSWORD` of service `detector-db` from `docker-compose.yml` file)
    - `spring.datasource.url` (`jdbc:postgresql://localhost:5432/metal-detector`, database name must match `POSTGRES_DB` of service `detector-db` from `docker-compose.yml` file)

8. Deposit your Discogs Access Token for the property `discogs.access-token` in file `application.properties` (see [Discogs API Documentation](https://www.discogs.com/developers/) for further information).

9. Define the following secrets (you can choose any value you want) in file `application.properties`:
    - `security.token-issuer` for JWT
    - `security.token-secret` for JWT
    - `security.remember-me-secret` for remember me functionality
    
10. Configure the profile `dev` for example via your IntelliJ Run Configuration or via `spring.profiles.active=dev` in the file `application.properties`     

It is also possible to define all mentioned connection details and secrets as environment variables. In this case no variables in `application.yml` need to be changed. The names of the environment variables are already in the `application.yml` file. You can define the environment variables for example within a Run Configuration in IntelliJ.

If you start the application with the default Spring profile, all emails sent by the application will be displayed on the console. No emails are sent via an SMTP server. If you want the application to send emails via an SMTP server, you must start the application with the Spring profile 'preview'.

<a name="run-application-locally-preview"></a>
## 4 Run application locally (Preview profile)

To start the application locally in PROD profile, the following preparatory actions are necessary:

1. Complete steps 1 to 6 from [Run application locally (Default profile)](#run-application-locally-default).

2. Define the email server connection details in file `application-preview.yml`. Define at least the following properties for the connection:
    - `spring.mail.host`
    - `spring.mail.username`
    - `spring.mail.password`
    - `spring.mail.properties.from`
    
3. Configure the profile `preview` for example via your IntelliJ Run Configuration or via `spring.profiles.active=preview` in the file `application.yml`

It is also possible to define all mentioned connection details and secrets as environment variables. In this case no variables in `application-preview.yml` file need to be changed. The names of the environment variables are already in the `application-preview.yml`. You can define the environment variables for example within a Run Configuration in IntelliJ.

<a name="start-application"></a>
## 5 Start the application

via Maven
- Execute command `mvn clean package spring-boot:run` in root directory

via your IDE
- Execute main class `rocks.metaldetector.MetalDetectorApplication`

Go to your web browser and visit `http://localhost:8080`.
You can log in via the URL `http://localhost:8080/login`.

There are three example users with the following credentials:

| Username       | Password       | Role           |
| -------------- | -------------- | -------------- |
| JohnD          | john.doe       | USER           |
| MariaT         | maria.thompson | USER           |
| Administrator  | simsalabim     | ADMINISTRATOR  |
