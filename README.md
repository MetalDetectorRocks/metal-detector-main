![Continous Integration](https://github.com/MetalDetectorRocks/metal-detector-main/workflows/Continous%20Integration/badge.svg)
![Build and deploy docker container image](https://github.com/MetalDetectorRocks/metal-detector-main/workflows/Build%20and%20deploy%20docker%20container%20image/badge.svg)

![Alt](https://repobeats.axiom.co/api/embed/cb9842a5ae951f4f965972409be5ff2b64ff02b8.svg "Repobeats analytics image")

## 1 Introduction

This repository contains the source code for the Metal Detector application. The application is a Java based Spring Boot application.

Metal Detector is a metal release calendar that is 100% tailored to you and your listening habits. The application has the following features:

- Search, follow and unfollow metal bands
- Synchronization of your followed bands from Spotify
- An overview of bands you are currently following
- A nicely designed release page that gives you a comprehensive overview of upcoming releases
- You also have the possibility to filter this release page to those bands you are currently following
- Release and announcement notifications as well as regular notifications via email or telegram

## 2 Download source code

Clone the source code via:

```
git clone https://github.com/MetalDetectorRocks/metal-detector-main.git
```

## 3 Run application locally (Default profile)

The following software is required:

- JDK 17
- Docker CE
- Node.js LTS or Latest
- TypeScript (run `npm install -g typescript` in your terminal after you have installed Node.js)

To start the application locally in default profile, the following preparatory actions are necessary:

1. Run `docker-compose.yml` via command `docker-compose up -d --no-recreate`. This starts all peripheral docker containers that are needed locally to run the Metal Detector Application:
    - `detector-db`: The database for Metal Detector application (currently PostgreSQL)
    - `butler-app`: Metal Release Butler Spring Boot application
    - `butler-db`: The database for Metal Release Butler application
    - `auth-app`: Metal Detector Auth Spring Boot application
    - `auth-db`: The database for Metal Detector auth application

2. Define the data source connection details in file `application.yml`:
    - `spring.datasource.username` (you have to use user `postgres`)
    - `spring.datasource.password` (password must match `POSTGRES_PASSWORD` of service `detector-db` from `docker-compose.yml` file)
    - `spring.datasource.url` (`jdbc:postgresql://localhost:5432/metal-detector`, database name must match `POSTGRES_DB` of service `detector-db` from `docker-compose.yml` file)

3. Deposit your personal Discogs Access Token for the property `discogs.access-token` in file `application.yml` (see [Discogs API Documentation](https://www.discogs.com/developers/) for further information).

4. Deposit your personal Spotify Client ID and Client Secret (or use preview credentials from keepass) in file `application.yml`:
    - `spotify.client-id`
    - `spotify.client-secret`

5. Deposit your personal Google Client ID and Client Secret (or use preview credentials from keepass) in file `application.yml`:
   - `spring.security.oauth2.client.registration.google.client-id`
   - `spring.security.oauth2.client.registration.google.client-secret`

6. Deposit the Metal Detector client id and client secret (see environment variables of auth-app service in `docker-compose.yml`) in file `application.yml`:
   - `spring.security.oauth2.client.registration.metal-release-butler-user.client-id`
   - `spring.security.oauth2.client.registration.metal-release-butler-user.client-secret`
   - `spring.security.oauth2.client.registration.metal-release-butler-admin.client-id`
   - `spring.security.oauth2.client.registration.metal-release-butler-admin.client-secret`

7. Define remember-me secret (you can choose any value you want) in file `application.yml`:
   - `security.remember-me-secret` for remember me functionality

8. Define a dummy value for `telegram.bot-id` in file `application.yml`.

9. Compile the frontend initially. To do this you have to execute the following commands from the root directory of the project:
   - `npm --prefix webapp/src/main/resources/static/ts/ install`
   - `npm --prefix webapp/src/main/resources/static/ts/ run dev-build`

10. Add the following snippet to `/etc/hosts`:

```
# Metal Release Butler
127.0.0.1 auth-app
```

It is also possible to define all mentioned connection details and secrets as environment variables. In this case no variables in `application.yml` need to be changed. The names of the environment variables are already in the `application.yml` file. You can  define the environment variables for example within a Run Configuration in IntelliJ.

If you start the application with the default Spring profile, all emails sent by the application will be displayed on the console.  No emails are sent via an SMTP server. If you want the application to send emails via an SMTP server, you must start the application with the Spring profile 'preview'.

## 4 Run application locally (Preview profile)

To start the application locally in PROD profile, the following preparatory actions are necessary:

1. Complete all steps from [Run application locally (Default profile)](#run-application-locally-default).

2. Define the email server connection details in file `application-preview.yml`. Define at least the following properties for the connection:
    - `spring.mail.host`
    - `spring.mail.username`
    - `spring.mail.password`
    - `spring.mail.properties.from`
    
3. Configure the profile `preview` for example via your IntelliJ Run Configuration or via `spring.profiles.active=preview` in the file `application.yml`

It is also possible to define all mentioned connection details and secrets as environment variables. In this case no variables in `application-preview.yml` file need to be changed. The names of the environment variables are already in the `application-preview.yml`. You can define the environment variables for example within a Run Configuration in IntelliJ.

## 5 Start the application

via gradle
- Execute command `./gradlew bootRun` in root directory

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

## 6 Execute tests locally

via gradle
- Execute command `./gradlew clean check` in root directory

via your IDE
- Execute the task `test` from folder `verification`
- Please note: You might get the message "Test events were not received" if you do this via IntelliJ. This is intentional behaviour of gradle. If nothing changes in the tests themselves, they will not be executed repeatedly. If you still want to run the tests, you have to execute `clean` before.

## 7 Import Postman collection

Click the button below to import a Postman collection holding some of the most important requests. Please follow this tutorial to import a [Postman collection](https://www.getpostman.com/docs/collections).

[![Run in Postman](https://run.pstmn.io/button.svg)](https://www.getpostman.com/run-collection/237a57215d5d6f0f9fb6)
