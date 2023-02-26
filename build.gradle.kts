import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

val javaVersion: JavaVersion = JavaVersion.VERSION_17

val dependencyVersions = listOf(
    "com.google.guava:guava:31.1-jre"
)

val dependencyGroupVersions = mapOf(
    "io.rest-assured" to libs.versions.restAssured.get(),
    "org.apache.groovy" to "4.0.9"
)

plugins {
  id("org.springframework.boot") version "3.0.2" apply false
  id("io.spring.dependency-management") version "1.1.0" apply false
  id("de.europace.docker-publish") version "1.4.2" apply false
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
      options.compilerArgs.add("-parameters")
    }
  }
}
