import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
  alias(libs.plugins.springBoot)
  alias(libs.plugins.dockerPublish)
}

dockerPublish {
  organisation.set("metaldetectorrocks")
  imageName.set(rootProject.name)
}

springBoot {
  mainClass.set("rocks.metaldetector.MetalDetectorApplication")
  buildInfo().apply {
    // TODO: remove as soon as the preview branch is removed
    version = "preview" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
  }
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
  implementation(libs.bundles.springBootStarterApp)

  implementation(libs.commonsLang3)
  implementation(libs.commonsCodec)
  implementation(libs.ehcache) {
    capabilities {
      requireCapability("org.ehcache:ehcache-jakarta")
    }
  }
  implementation(libs.jsoup)
  implementation(libs.lombok)
  implementation(libs.esapi) {
    exclude(group = "org.slf4j", module = "slf4j-simple")
    exclude(group = "commons-logging", module = "commons-logging")
  }

  annotationProcessor(libs.lombok)

  developmentOnly(libs.springBootDevTools)

  runtimeOnly(libs.lokiLogbackAppender)
  runtimeOnly(libs.bundles.flyway)
  runtimeOnly(libs.micrometerRegistryPrometheus)
  runtimeOnly(libs.cacheApi)

  implementation(rootProject.projects.support)
  implementation(rootProject.projects.spotify)
  implementation(rootProject.projects.discogs)
  implementation(rootProject.projects.butler)
  implementation(rootProject.projects.persistence)
  implementation(rootProject.projects.telegram)

  testImplementation(libs.springBootStarterTest)
  testImplementation(libs.springSecurityTest)
  testImplementation(libs.h2)
  testImplementation(libs.bundles.restAssured)

  testImplementation(libs.mockito)
  mockitoAgent(libs.mockito) { isTransitive = false }
}

tasks {

  bootJar {
    archiveClassifier.set("boot")
    enabled = true
  }

  jar {
    enabled = false
  }

  test {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
  }
}

description = "webapp"
