dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.apache.commons:commons-text:${libs.versions.commonsText.get()}")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  implementation(rootProject.projects.support)

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  testImplementation("org.apache.commons:commons-text:${libs.versions.commonsText.get()}")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

description = "butler"
