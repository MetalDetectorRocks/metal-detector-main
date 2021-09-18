dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")

  annotationProcessor("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")

  implementation(project(":support"))

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "junit", module = "junit")
  }
}

description = "telegram"
