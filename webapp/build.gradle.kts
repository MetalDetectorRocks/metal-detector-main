plugins {
  id("org.springframework.boot")
}

tasks {
  bootJar {
    archiveClassifier.set("boot")
    enabled = true
  }

  jar {
    enabled = false
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
  implementation("org.owasp.esapi:esapi:${rootProject.extra["esapiVersion"]}") {
    exclude(group = "org.slf4j", module = "slf4j-simple")
  }

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

  testImplementation("io.rest-assured:rest-assured:${rootProject.extra["restAssuredVersion"]}")
  testImplementation("io.rest-assured:json-path:${rootProject.extra["restAssuredVersion"]}")
  testImplementation("io.rest-assured:xml-path:${rootProject.extra["restAssuredVersion"]}")
  testImplementation("io.rest-assured:spring-mock-mvc:${rootProject.extra["restAssuredVersion"]}")
}

description = "webapp"
