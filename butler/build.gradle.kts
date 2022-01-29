dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web:${libs.versions.springBoot.get()}")
  implementation("org.apache.commons:commons-text:${libs.versions.commonsText.get()}")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  implementation(rootProject.projects.support)

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  testImplementation("org.apache.commons:commons-text:${libs.versions.commonsText.get()}")
  testImplementation("org.mockito:mockito-inline:${libs.versions.mockito.get()}")
  testImplementation("org.springframework.boot:spring-boot-starter-test:${libs.versions.springBoot.get()}") {
    exclude(group = "junit", module = "junit")
  }
}

description = "butler"
