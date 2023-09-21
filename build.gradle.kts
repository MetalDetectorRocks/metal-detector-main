import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

val javaVersion: JavaVersion = JavaVersion.VERSION_17

val dependencyVersions = listOf(
    "com.google.guava:guava:32.1.2-jre"
)

val dependencyGroupVersions = mapOf(
    "io.rest-assured" to libs.versions.restAssured.get(),
    "org.apache.groovy" to "4.0.9"
)

plugins {
  id("org.siouan.frontend-jdk11") version "8.0.0" apply false
  id("org.springframework.boot") version "2.7.16" apply false
  id("io.spring.dependency-management") version "1.1.3" apply false
  id("de.europace.docker-publish") version "2.0.0" apply false
}

subprojects {
  project.apply(plugin = "java")
  project.apply(plugin = "io.spring.dependency-management")

  the<DependencyManagementExtension>().apply {
    imports {
      mavenBom(BOM_COORDINATES)
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
