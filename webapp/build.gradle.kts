import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
  id("org.springframework.boot")
  id("org.siouan.frontend-jdk17")
  id("de.europace.docker-publish")
}

dockerPublish {
  organisation.set("metaldetectorrocks")
  imageName.set(rootProject.name)
}

springBoot {
  mainClass.set("rocks.metaldetector.MetalDetectorApplication")
  buildInfo().apply {
    version = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
  }
}

tasks {
  bootJar {
    dependsOn(assembleFrontend)
    archiveClassifier.set("boot")
    enabled = true
  }

  jar {
    enabled = false
  }

  register<Delete>("cleanFrontend") {
    delete("src/main/resources/static/ts/node_modules/", "src/main/resources/static/ts/dist/")
  }

  named("clean") {
    dependsOn("cleanFrontend")
  }

  frontend {
    nodeDistributionProvided.set(false)
    nodeVersion.set("20.18.1")
    nodeDistributionUrlRoot.set("https://nodejs.org/dist/")
    nodeDistributionUrlPathPattern.set("vVERSION/node-vVERSION-ARCH.TYPE")
    nodeInstallDirectory.set(file("${projectDir}/node"))

    installScript.set("install")
    assembleScript.set("run assemble")
    checkScript.set("run test")
    corepackVersion.set("0.31.0")

    packageJsonDirectory.set(file("${projectDir}/src/main/resources/static/ts"))
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

  implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6:${libs.versions.thymeleafExtras.get()}")
  implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:${libs.versions.thymeleafDialect.get()}")
  implementation("org.apache.commons:commons-lang3:${libs.versions.commonsLang3.get()}")
  implementation("commons-codec:commons-codec:${libs.versions.commonsCodec.get()}")
  implementation("org.ehcache:ehcache:${libs.versions.ehcache.get()}") {
    capabilities {
      requireCapability("org.ehcache:ehcache-jakarta")
    }
  }
  implementation("org.jsoup:jsoup:${libs.versions.jsoup.get()}")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")
  implementation("org.owasp.esapi:esapi:${libs.versions.esapi.get()}") {
    exclude(group = "org.slf4j", module = "slf4j-simple")
  }

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  developmentOnly("org.springframework.boot:spring-boot-devtools")

  runtimeOnly("com.github.loki4j:loki-logback-appender:${libs.versions.lokiLogbackAppender.get()}")
  runtimeOnly("org.flywaydb:flyway-core")
  runtimeOnly("org.flywaydb:flyway-database-postgresql")
  runtimeOnly("io.micrometer:micrometer-registry-prometheus")
//  runtimeOnly("javax.cache:cache-api:${libs.versions.cacheApi.get()}") // TODO NilsD remove if not necessary

  implementation(rootProject.projects.support)
  implementation(rootProject.projects.spotify)
  implementation(rootProject.projects.discogs)
  implementation(rootProject.projects.butler)
  implementation(rootProject.projects.persistence)
  implementation(rootProject.projects.telegram)

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("com.h2database:h2")
  testImplementation("io.rest-assured:rest-assured:${libs.versions.restAssured.get()}")
  testImplementation("io.rest-assured:json-path:${libs.versions.restAssured.get()}")
  testImplementation("io.rest-assured:xml-path:${libs.versions.restAssured.get()}")
  testImplementation("io.rest-assured:spring-mock-mvc:${libs.versions.restAssured.get()}")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

description = "webapp"
