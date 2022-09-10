dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")

  implementation("org.apache.commons:commons-text:${libs.versions.commonsText.get()}")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  implementation(rootProject.projects.support)

  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

description = "spotify"
