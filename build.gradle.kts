import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

val javaVersion: JavaVersion = JavaVersion.VERSION_17

val dependencyVersions = listOf(
  "commons-beanutils:commons-beanutils:1.11.0",
  "commons-logging:commons-logging:1.3.5"
)

val dependencyGroupVersions = mapOf<String, String>()

plugins {
  id("org.siouan.frontend-jdk17") version "10.0.0" apply false
  id("org.springframework.boot") version "3.5.3" apply false
  id("io.spring.dependency-management") version "1.1.7" apply false
  id("de.europace.docker-publish") version "2.0.15" apply false
}

subprojects {
  project.apply(plugin = "java")
  project.apply(plugin = "io.spring.dependency-management")

  the<DependencyManagementExtension>().apply {
    imports {
      mavenBom(BOM_COORDINATES)
    }
    dependencies {
      dependency("net.minidev:json-smart:2.5.2")
    }
  }

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
    withType<JavaCompile> {
      options.encoding = "UTF-8"
    }
  }
}
