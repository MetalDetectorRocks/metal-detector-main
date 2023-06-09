import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

val javaVersion: JavaVersion = JavaVersion.VERSION_17

val dependencyVersions = listOf(
    "com.google.guava:guava:32.0.1-jre"
)

val dependencyGroupVersions = mapOf(
    "io.rest-assured" to libs.versions.restAssured.get(),
    "org.apache.groovy" to libs.groovy.get().version
)

plugins {
  alias(libs.plugins.springBoot) apply false
  alias(libs.plugins.springDependencyManagement) apply false
  alias(libs.plugins.dockerPublish) apply false
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
      options.compilerArgs.add("-parameters")
    }
  }
}
