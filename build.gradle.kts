buildscript {
  extra.apply {
    set("apacheCommonsLang3Version", "3.12.0")
    set("apacheCommonsTextVersion", "1.9")
    set("cacheApiVersion", "1.1.1")
    set("commonsCodecVersion", "1.15")
    set("datatablesVersion", "1.10.25")
    set("ehcacheVersion", "3.9.6")
    set("esapiVersion", "2.2.3.1")
    set("flywayVersion", "7.14.1")
    set("h2Version", "1.4.200")
    set("httpClientVersion", "4.5.13")
    set("jacksonVersion", "2.12.4")
    set("jacocoVersion", "0.8.7")
    set("jaxbApiVersion", "2.3.1")
    set("jsonwebtokenVersion", "0.9.1")
    set("jsoupVersion", "1.14.2")
    set("lombokVersion", "1.18.20")
    set("micrometerVersion", "1.7.3")
    set("mockitoVersion", "3.12.4")
    set("modelmapperVersion", "2.4.4")
    set("postgresqlVersion", "42.2.23")
    set("restAssuredVersion", "4.4.0")
    set("servletApiVersion", "4.0.1")
    set("slf4jMockVersion", "2.1.1")
    set("springBootVersion", "2.5.4")
    set("springVersion", "5.3.9")
    set("springSecurityVersion", "5.5.2")
    set("thymeleafDialectVersion", "3.0.0")
    set("thymeleafExtrasVersion", "3.0.4.RELEASE")
  }
}

val javaVersion: JavaVersion = JavaVersion.VERSION_11
val dependencyVersions = listOf(
  "org.slf4j:slf4j-api:1.7.32",
  "org.jboss.logging:jboss-logging:3.4.2.Final",
  "net.bytebuddy:byte-buddy:1.11.16",
  "org.javassist:javassist:3.28.0-GA"
)
val dependencyGroupVersions = mapOf(
  "org.springframework" to extra["springVersion"] as String,
  "org.springframework.security" to extra["springSecurityVersion"] as String,
  "org.springframework.boot" to extra["springBootVersion"] as String,
  "com.fasterxml.jackson.core" to extra["jacksonVersion"] as String,
  "com.fasterxml.jackson.datatype" to extra["jacksonVersion"] as String,
  "com.fasterxml.jackson.module" to extra["jacksonVersion"] as String,
  "org.mockito" to extra["mockitoVersion"] as String
)

plugins {
  id("java")
  id("org.siouan.frontend-jdk11") version "5.3.0" apply false
  id("org.springframework.boot") version "2.5.4" apply false
  id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
}

allprojects {
  group = "rocks.metaldetector"

  repositories {
    mavenCentral()
  }
}

subprojects {
  project.apply(plugin = "java")
  project.apply(plugin = "io.spring.dependency-management")
  project.apply(plugin = "jacoco")

  configurations {
    all {
      resolutionStrategy {
        failOnVersionConflict()
        force(dependencyVersions)
            eachDependency {
              val forcedVersion = dependencyGroupVersions[requested.group]
              if (forcedVersion != null) {
                useVersion(forcedVersion)
              }
            }
        cacheDynamicVersionsFor(0, "seconds")
      }
    }
  }

  configure<JavaPluginExtension> {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
  }

  tasks {
    withType<Test> {
      useJUnitPlatform()
      testLogging.showStandardStreams = true
    }
    withType<JacocoReport> {
      reports {
        xml.required.set(true)
        html.required.set(false)
      }
    }
    withType<JavaCompile> {
      options.encoding = "UTF-8"
    }
  }
}

tasks {
  wrapper {
    gradleVersion = "7.2"
    distributionType = Wrapper.DistributionType.ALL
  }
}
