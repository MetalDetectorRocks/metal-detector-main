import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

val javaVersion: JavaVersion = JavaVersion.VERSION_21

val dependencyVersions = listOf(
  "commons-beanutils:commons-beanutils:1.11.0"
  )

val dependencyGroupVersions = mapOf<String, String>()

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
      options.compilerArgs.add("-parameters")
    }
  }
}
