![Logo](https://github.com/Waginator/metal-release-radar/blob/master/misc/logo.png) 

[![CircleCI](https://circleci.com/gh/Waginator/metal-release-radar/tree/master.svg?style=svg)](https://circleci.com/gh/Waginator/metal-release-radar/tree/master)
[![Coverage Status](https://coveralls.io/repos/github/Waginator/metal-release-radar/badge.svg?branch=master)](https://coveralls.io/github/Waginator/metal-release-radar?branch=master)

## Table of contents
1. [ Introduction ](#introduction)

2. [ Download source code ](#download-source-code)

3. [ Project Setup ](#project-setup)

4. [ Run the application ](#run-application)

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
git clone https://github.com/Waginator/metal-release-radar.git
```

<a name="project-setup"></a>
## 3 Project Setup
To setup the project please apply the following steps:
- Define the data source connection details in file `application.properties`. Define at least the following properties for mysql connection:
    - `spring.datasource.username`
    - `spring.datasource.password`
    - `spring.datasource.url`
- Deposit your Discogs Access Token for the property `discogs.access-token` in file `discogs.properties` (see [Discogs API Documentation](https://www.discogs.com/developers/) for further information).
- Define the following secrets in file `security.properties`:
    - `security.token-issuer` for JWT
    - `security.token-secret` for JWT
    - `security.remember-me-secret` for remember me functionality

If you start the application with the default Spring profile or with the profile 'dev', all emails sent by the application will be displayed on the console. 
No emails are sent via an SMTP server. If you want the application to send emails via an SMTP server, you must start the application with the Spring profile 
'prod'. Before this you have to define the email server connection details in file `application.properties`. Define at least the following properties for the connection:
    - `spring.mail.host`
    - `spring.mail.username`
    - `spring.mail.password`
    - `mail.from.email`

<a name="run-application"></a>
## 4 Run the application
You need Java 11 to run the application.

via Maven
- Clone the repository
- Execute command `mvn install` in root directory
- Execute command `mvn springboot:run` in root directory

via your IDE
- Clone the repository
- Execute main class `com.metalr2.MetalReleaseRadarApplication`

Go to your web browser and visit `http://localhost:8090`.
You can log in via the URL `http://localhost:8090/login`. 

There are three example users with the following credentials:

| Username       | Password       | Role           |
| -------------- | -------------- | -------------- |
| JohnD          | john.doe       | USER           |
| MariaT         | maria.thompson | USER           |
| Administrator  | simsalabim     | ADMINISTRATOR  |

