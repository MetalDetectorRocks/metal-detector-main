dependencies {
  api("org.springframework.boot:spring-boot-starter-data-jpa")

  implementation("org.postgresql:postgresql:${rootProject.extra["postgresqlVersion"]}")

  implementation(project(":support"))
}

description = "persistence"
