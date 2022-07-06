val javaVersion: JavaVersion = JavaVersion.VERSION_17

val dependencyVersions = listOf(
    "org.slf4j:slf4j-api:1.7.36",
    "org.jboss.logging:jboss-logging:3.5.0.Final",
    "net.bytebuddy:byte-buddy:1.12.12"
)

val dependencyGroupVersions = mapOf(
    "org.springframework" to libs.versions.spring.get(),
    "org.springframework.security" to libs.versions.springSecurity.get(),
    "org.springframework.boot" to libs.versions.springBoot.get(),
    "com.fasterxml.jackson.core" to libs.versions.jackson.get(),
    "com.fasterxml.jackson.datatype" to libs.versions.jackson.get(),
    "com.fasterxml.jackson.module" to libs.versions.jackson.get(),
    "org.mockito" to libs.versions.mockito.get(),
    "org.junit.jupiter" to libs.versions.junit.get(),
    "org.apache.groovy" to "4.0.2"
)

plugins {
  id("java")
  id("org.siouan.frontend-jdk11") version "6.0.0" apply false
  id("org.springframework.boot") version "2.7.1" apply false
  id("io.spring.dependency-management") version "1.0.12.RELEASE" apply false
  id("de.europace.docker-publish") version "1.4.0" apply false
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

  repositories {
    mavenCentral()
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
