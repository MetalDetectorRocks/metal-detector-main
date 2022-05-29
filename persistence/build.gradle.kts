plugins {
  id("java-library")
}

dependencies {
  api("org.springframework.boot:spring-boot-starter-data-jpa:${libs.versions.springBoot.get()}")

  implementation("org.postgresql:postgresql:${libs.versions.postgresql.get()}")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  implementation(rootProject.projects.support)

  testImplementation("org.springframework.boot:spring-boot-starter-test:${libs.versions.springBoot.get()}")
  testImplementation("com.h2database:h2:${libs.versions.h2.get()}")
  testImplementation("org.mockito:mockito-inline:${libs.versions.mockito.get()}")
}

description = "persistence"
