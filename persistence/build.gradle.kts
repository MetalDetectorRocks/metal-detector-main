plugins {
  id("java-library")
}

dependencies {
  api("org.springframework.boot:spring-boot-starter-data-jpa")

  implementation("org.postgresql:postgresql:${libs.versions.postgresql.get()}")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  implementation(rootProject.projects.support)

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("com.h2database:h2")
  testImplementation("org.mockito:mockito-inline")
}

description = "persistence"
