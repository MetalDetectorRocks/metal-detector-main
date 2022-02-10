plugins {
  id("org.springframework.boot")
  id("org.siouan.frontend-jdk11")
}

springBoot {
  mainClass.set("rocks.metaldetector.MetalDetectorApplication")
}

tasks {
  bootJar {
    dependsOn("assembleFrontend")
    archiveClassifier.set("boot")
    enabled = true
  }

  jar {
    enabled = false
  }

  frontend {
    nodeDistributionProvided.set(false)
    nodeVersion.set("16.14.0")
    nodeDistributionUrlRoot.set("https://nodejs.org/dist/")
    nodeDistributionUrlPathPattern.set("vVERSION/node-vVERSION-ARCH.TYPE")
    nodeInstallDirectory.set(file("${projectDir}/node"))

    installScript.set("install")
    cleanScript.set("run clean")
    assembleScript.set("run assemble")
    checkScript.set("run test")

    packageJsonDirectory.set(file("${projectDir}/src/main/resources/static/ts"))
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web:${libs.versions.springBoot.get()}")
  implementation("org.springframework.boot:spring-boot-starter-mail:${libs.versions.springBoot.get()}")
  implementation("org.springframework.boot:spring-boot-starter-actuator:${libs.versions.springBoot.get()}")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf:${libs.versions.springBoot.get()}")
  implementation("org.springframework.boot:spring-boot-starter-validation:${libs.versions.springBoot.get()}")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client:${libs.versions.springBoot.get()}")

  implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5:${libs.versions.thymeleafExtras.get()}")
  implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:${libs.versions.thymeleafDialect.get()}")
  implementation("org.apache.commons:commons-lang3:${libs.versions.commonsLang3.get()}")
  implementation("commons-codec:commons-codec:${libs.versions.commonsCodec.get()}")
  implementation("org.ehcache:ehcache:${libs.versions.ehcache.get()}")
  implementation("org.jsoup:jsoup:${libs.versions.jsoup.get()}")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")
  implementation("org.owasp.esapi:esapi:${libs.versions.esapi.get()}") {
    exclude(group = "org.slf4j", module = "slf4j-simple")
  }

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  developmentOnly("org.springframework.boot:spring-boot-devtools:${libs.versions.springBoot.get()}")

  runtimeOnly("org.webjars:datatables:${libs.versions.datatables.get()}")
  runtimeOnly("org.flywaydb:flyway-core:${libs.versions.flyway.get()}")
  runtimeOnly("io.micrometer:micrometer-registry-prometheus:${libs.versions.micrometer.get()}")
  runtimeOnly("javax.cache:cache-api:${libs.versions.cacheApi.get()}")

  implementation(rootProject.projects.support)
  implementation(rootProject.projects.spotify)
  implementation(rootProject.projects.discogs)
  implementation(rootProject.projects.butler)
  implementation(rootProject.projects.persistence)
  implementation(rootProject.projects.telegram)

  testImplementation("org.springframework.boot:spring-boot-starter-test:${libs.versions.springBoot.get()}") {
    exclude(group = "junit", module = "junit")
  }
  testImplementation("org.springframework.security:spring-security-test:${libs.versions.springSecurity.get()}")
  testImplementation("com.h2database:h2:${libs.versions.h2.get()}")
  testImplementation("io.rest-assured:rest-assured:${libs.versions.restAssured.get()}")
  testImplementation("io.rest-assured:json-path:${libs.versions.restAssured.get()}")
  testImplementation("io.rest-assured:xml-path:${libs.versions.restAssured.get()}")
  testImplementation("io.rest-assured:spring-mock-mvc:${libs.versions.restAssured.get()}")
}

description = "webapp"
