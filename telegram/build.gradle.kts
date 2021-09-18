dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  implementation(project(":support"))

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "junit", module = "junit")
  }
}

description = "telegram"
