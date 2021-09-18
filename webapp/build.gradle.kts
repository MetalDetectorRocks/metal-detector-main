plugins {
  id("org.springframework.boot")
  id("org.siouan.frontend-jdk11")
}

springBoot {
  mainClass.set("rocks.metaldetector.MetalDetectorApplication")
}

tasks {
  bootJar {
    archiveClassifier.set("boot")
    enabled = true
  }

  jar {
    enabled = false
  }

  frontend {
    nodeDistributionProvided.set(false)
    nodeVersion.set("14.15.3")
    nodeDistributionUrlRoot.set("https://nodejs.org/dist/")
    nodeDistributionUrlPathPattern.set("vVERSION/node-vVERSION-ARCH.TYPE")
    nodeInstallDirectory.set(file("${projectDir}/node"))

    installScript.set("install")
    cleanScript.set("run clean")
    assembleScript.set("run assemble")
    checkScript.set("run test")

    packageJsonDirectory.set(file("${projectDir}/src/main/resources/static/ts"))
  }

  build {
    dependsOn("assembleFrontend")
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

  implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5:${rootProject.extra["thymeleafExtrasVersion"]}")
  implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:${rootProject.extra["thymeleafDialectVersion"]}")
  implementation("org.apache.commons:commons-lang3:${rootProject.extra["apacheCommonsLang3Version"]}")
  implementation("commons-codec:commons-codec:${rootProject.extra["commonsCodecVersion"]}")
  implementation("org.modelmapper:modelmapper:${rootProject.extra["modelmapperVersion"]}")
  implementation("org.ehcache:ehcache:${rootProject.extra["ehcacheVersion"]}")
  implementation("org.jsoup:jsoup:${rootProject.extra["jsoupVersion"]}")
  implementation("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")
  implementation("org.owasp.esapi:esapi:${rootProject.extra["esapiVersion"]}") {
    exclude(group = "org.slf4j", module = "slf4j-simple")
  }

  annotationProcessor("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")

  developmentOnly("org.springframework.boot:spring-boot-devtools")

  runtimeOnly("org.webjars:datatables:${rootProject.extra["datatablesVersion"]}")
  runtimeOnly("org.flywaydb:flyway-core:${rootProject.extra["flywayVersion"]}")
  runtimeOnly("io.micrometer:micrometer-registry-prometheus:${rootProject.extra["micrometerVersion"]}")
  runtimeOnly("javax.cache:cache-api:${rootProject.extra["cacheApiVersion"]}")

  implementation(project(":support"))
  implementation(project(":spotify"))
  implementation(project(":discogs"))
  implementation(project(":butler"))
  implementation(project(":persistence"))
  implementation(project(":telegram"))

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "junit", module = "junit")
  }
  testImplementation("org.springframework.security:spring-security-test:${rootProject.extra["springSecurityVersion"]}")
  testImplementation("com.h2database:h2:${rootProject.extra["h2Version"]}")
  testImplementation("io.rest-assured:rest-assured:${rootProject.extra["restAssuredVersion"]}")
  testImplementation("io.rest-assured:json-path:${rootProject.extra["restAssuredVersion"]}")
  testImplementation("io.rest-assured:xml-path:${rootProject.extra["restAssuredVersion"]}")
  testImplementation("io.rest-assured:spring-mock-mvc:${rootProject.extra["restAssuredVersion"]}")
}

description = "webapp"
