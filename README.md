![Logo](https://github.com/Waginator/metal-release-radar/blob/master/misc/logo.png) 

[![CircleCI](https://circleci.com/gh/DanielW1987/metal-release-radar/tree/master.svg?style=svg)](https://circleci.com/gh/DanielW1987/metal-release-radar/tree/master)
[![Coverage Status](https://coveralls.io/repos/github/DanielW1987/metal-release-radar/badge.svg?branch=master)](https://coveralls.io/github/DanielW1987/metal-release-radar?branch=master)

## Table of contents
1. [ Introduction ](#introduction)

2. [ Download source code ](#download-source-code)

3. [ Run application locally (DEV profile) ](#run-application-locally-dev)

4. [ Run application locally (PROD profile) ](#run-application-locally-prod)

5. [ Start the application ](#start-application)

<a name="introduction"></a>
## 1 Introduction
This repository contains the source code for the Metal Release Radar application. The application is currently under development. 

The following features are planned:
- Mark your favorite metal bands
- Receive regular email alerts about upcoming and recently released albums from your favorite bands

The application uses the REST API from Discogs.

<a name="download-source-code"></a>
## 2 Download source code

Clone the source code via:

```
git clone https://github.com/DanielW1987/metal-release-radar.git
```

<a name="run-application-locally-dev"></a>
## 3 Run application locally (DEV profile)

To start the application locally in DEV profile, the following preparatory actions are necessary:

1. Install Java 11

2. Install Docker CE

3. Create folder `.secrets` below the project root directory and create the following files within this folder
  - `mrr_butler_mysql_password.txt`
  - `mrr_butler_mysql_root_password.txt`
  - `mrr_mysql_password.txt`
  - `mrr_mysql_root_password.txt`

4. A password of your choice can be entered in each file created. The files are used for the `docker-compose.yml` file, which becomes relevant in a moment.

5. Run `docker-compose.yml` via command `docker-compose up -d --no-recreate`. This starts all peripheral docker containers that are needed locally to run the Metal Release Radar Application:
  - `mrr-mysql`: MySQL database for Metal Release Radar application 
  - `mrr-phpmyadmin`: phpmyadmin for Metal Release Radars MySQL database
  - `mrr-butler-mysql`: MySQL database for Metal Release Butler application 
  - `mrr-butler-phpmyadmin`: phpmyadmin for Metal Release Butlers MySQL database
  - `mrr-butler`: Metal Release Butler Spring Boot application

6. Define the data source connection details in file `application.properties` for mysql connection:
    - `spring.datasource.username` (`root` or must match with `MYSQL_USER` of serive `mrr-mysql` from `docker-compose.yml` file)
    - `spring.datasource.password` (password from `mrr_mysql_password.txt` or `mrr_mysql_root_password.txt`)
    - `spring.datasource.url` (`jdbc:mysql://localhost:3306/metal-release-radar?useUnicode\=true&characterEncoding\=utf-8&serverTimezone\=UTC`, database name must match `MYSQL_DATABASE` of serive `mrr-mysql` from `docker-compose.yml` file)

7. Deposit your Discogs Access Token for the property `discogs.access-token` in file `application.properties` (see [Discogs API Documentation](https://www.discogs.com/developers/) for further information).

8. Define the following secrets (you can choose any value you want) in file `application.properties`:
    - `security.token-issuer` for JWT
    - `security.token-secret` for JWT
    - `security.remember-me-secret` for remember me functionality

It is also possible to define all mentioned connection details and secrets as environment variables. In this case no .properties variables need to be changed. The names of the environment variables are already in the .properties files. You can define the environment variables, for example, within a Run Configuration in IntelliJ.

If you start the application with the default Spring profile or with the profile 'dev', all emails sent by the application will be displayed on the console. No emails are sent via an SMTP server. If you want the application to send emails via an SMTP server, you must start the application with the Spring profile 'prod'. 

<a name="run-application-locally-prod"></a>
## 4 Run application locally (PROD profile)

To start the application locally in PROD profile, the following preparatory actions are necessary:

1. Complete all steps from [ Run application locally (DEV profile) ](#run-application-locally-dev).

2. Define the email server connection details in file `application-prod.properties`. Define at least the following properties for the connection:
    - `spring.mail.host`
    - `spring.mail.username`
    - `spring.mail.password`
    - `spring.mail.properties.from`

It is also possible to define all mentioned connection details and secrets as environment variables. In this case no .properties variables need to be changed. The names of the environment variables are already in the .properties files. You can define the environment variables, for example, within a Run Configuration in IntelliJ.

<a name="start-application"></a>
## 4 Start the application

via Maven
- Execute command `mvn install` and after that `mvn springboot:run` in root directory

via your IDE
- Execute main class `com.metalr2.MetalReleaseRadarApplication`

Go to your web browser and visit `http://localhost:8090`.
You can log in via the URL `http://localhost:8090/login`. 

There are three example users with the following credentials:

| Username       | Password       | Role           |
| -------------- | -------------- | -------------- |
| JohnD          | john.doe       | USER           |
| MariaT         | maria.thompson | USER           |
| Administrator  | simsalabim     | ADMINISTRATOR  |
