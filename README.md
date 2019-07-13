# Metal Release Radar [![CircleCI](https://circleci.com/gh/Waginator/metal-release-radar/tree/master.svg?style=svg)](https://circleci.com/gh/Waginator/metal-release-radar/tree/master)

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
- Define the email server connection details in file `application.properties`. Define at least the following properties for the connection:
    - `spring.mail.host`
    - `spring.mail.username`
    - `spring.mail.password`
    - `mail.from.email`
- Define the token secret property `token-secret` also in file `application.properties`.
- In the resources folder, create the file `discogs.properties` with values for the following properties:
    - `discogs.user-agent`
    - `discogs.access-token`
    - `discogs.rest-base-url`
- Take into account the information available at [Discogs API Documentation](https://www.discogs.com/developers/).

<a name="run-application"></a>
## 4 Run the application
You need Java 11 to run the application.

via Maven
- Clone the repository
- Execute command `mvn install` in root directory
- Execute .jar file from directory `target` via `java -jar metal-release-radar-0.0.1.jar`

via your IDE
- Clone the repository
- Execute main class `com.metalr2.MetalReleaseRadarApplication`

Go to your web browser and visit `http://localhost:8090`.
You can log in via the URL `http://localhost:8090/login`. 

There are two example users with the following credentials:

| Email                        | Password       |
| ---------------------------- | -------------- |
| john.doe@example.com         | john.doe       |
| maria.thompson@example.com   | maria.thompson |
