![Continous Integration](https://github.com/MetalDetectorRocks/metal-detector-main/workflows/Continous%20Integration/badge.svg)
[![codecov](https://codecov.io/gh/MetalDetectorRocks/metal-detector-main/branch/master/graph/badge.svg)](https://codecov.io/gh/MetalDetectorRocks/metal-detector-main)
![Build and deploy docker container image](https://github.com/MetalDetectorRocks/metal-detector-main/workflows/Build%20and%20deploy%20docker%20container%20image/badge.svg)

![Alt](https://repobeats.axiom.co/api/embed/cb9842a5ae951f4f965972409be5ff2b64ff02b8.svg "Repobeats analytics image")

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

The following software is required:

- JDK 11 (<https://jdk.java.net/11/>)
- Docker (<https://docs.docker.com/get-docker/>)
- Node.js LTS or Latest (<https://nodejs.org/en/>)
- TypeScript (run `npm install -g typescript` in your terminal after you have installed Node.js)

To start the application locally in default profile, the following preparatory actions are necessary:

1. Run `docker-compose.yml` via command `docker-compose up -d --no-recreate`. This starts all peripheral docker containers that are needed locally to run the Metal Detector Application:
    - `detector-db`: The database for Metal Detector application (currently PostgreSQL)
    - `butler-db`: The database for Metal Release Butler application
    - `butler-app`: Metal Release Butler Spring Boot application

2. Define the data source connection details in file `application.yml`:
    - `spring.datasource.username` (you have to use user `postgres`)
    - `spring.datasource.password` (password must match `POSTGRES_PASSWORD` of service `detector-db` from `docker-compose.yml` file)
    - `spring.datasource.url` (`jdbc:postgresql://localhost:5432/metal-detector`, database name must match `POSTGRES_DB` of service `detector-db` from `docker-compose.yml` file)

3. Deposit your Discogs Access Token for the property `discogs.access-token` in file `application.yml` (see [Discogs API Documentation](https://www.discogs.com/developers/) for further information).

4. Deposit your Spotify Client ID and Client Secret in file `application.yml`:
    - `spotify.client-id`
    - `spotify.client-secret`

5. Deposit your Google Client ID and Client Secret in file `application.yml`:
   - `spring.security.oauth2.client.registration.google.client-id`
   - `spring.security.oauth2.client.registration.google.client-secret`

6. Define JWT Issuer and secrets (you can choose any value you want) in file `application.yml`:
    - `security.token-issuer` for JWT
    - `security.token-secret` for JWT
    - `security.remember-me-secret` for remember me functionality

7. Compile the frontend initially. To do this you have to execute the following commands from the root directory of the project:
    - `npm --prefix webapp/src/main/resources/static/ts/ install`
    - `npm --prefix webapp/src/main/resources/static/ts/ run prod-build`

It is also possible to define all mentioned connection details and secrets as environment variables. In this case no variables in
`application.yml` need to be changed. The names of the environment variables are already in the `application.yml` file. You can
define the environment variables for example within a Run Configuration in IntelliJ.

If you start the application with the default Spring profile, all emails sent by the application will be displayed on the console.
No emails are sent via an SMTP server. If you want the application to send emails via an SMTP server, you must start the application
with the Spring profile 'preview'.

<a name="run-application-locally-preview"></a>
## 4 Run application locally (Preview profile)

To start the application locally in PROD profile, the following preparatory actions are necessary:

1. Complete all steps from [Run application locally (Default profile)](#run-application-locally-default).

2. Define the email server connection details in file `application-preview.yml`. Define at least the following properties for the connection:
    - `spring.mail.host`
    - `spring.mail.username`
    - `spring.mail.password`
    - `spring.mail.properties.from`
    
3. Configure the profile `preview` for example via your IntelliJ Run Configuration or via `spring.profiles.active=preview` in the file `application.yml`

It is also possible to define all mentioned connection details and secrets as environment variables. In this case no variables in
`application-preview.yml` file need to be changed. The names of the environment variables are already in the `application-preview.yml`.
You can define the environment variables for example within a Run Configuration in IntelliJ.

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

<a name="postman-collection"></a>
## 6 Import Postman collection

Click the button below to import a Postman collection holding some of the most important requests. Please follow this tutorial to import a [Postman collection](https://www.getpostman.com/docs/collections).

[![Run in Postman](https://run.pstmn.io/button.svg)](https://www.getpostman.com/run-collection/237a57215d5d6f0f9fb6)
