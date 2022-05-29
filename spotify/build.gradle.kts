dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web:${libs.versions.springBoot.get()}")

  implementation("org.apache.commons:commons-text:${libs.versions.commonsText.get()}")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  implementation(rootProject.projects.support)

  testImplementation("org.springframework.boot:spring-boot-starter-test:${libs.versions.springBoot.get()}")
}

description = "spotify"
