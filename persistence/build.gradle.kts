plugins {
  id("java-library")
}

dependencies {
  api("org.springframework.boot:spring-boot-starter-data-jpa")

  implementation("org.postgresql:postgresql:${rootProject.extra["postgresqlVersion"]}")
  implementation("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")

  annotationProcessor("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")

  implementation(project(":support"))

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "junit", module = "junit")
  }
  testImplementation("com.h2database:h2:${rootProject.extra["h2Version"]}")
}

description = "persistence"
