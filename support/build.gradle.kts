plugins {
  id("java-library")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework:spring-jdbc")
  api("org.springframework.security:spring-security-oauth2-client")
  implementation("org.apache.commons:commons-text:${libs.versions.commonsText.get()}")
  implementation("org.apache.httpcomponents:httpclient:${libs.versions.httpClient.get()}")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${libs.versions.jackson.get()}")

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  api("io.jsonwebtoken:jjwt:${libs.versions.jsonwebtoken.get()}")

  testImplementation("com.github.valfirst:slf4j-test:${libs.versions.slf4jTest.get()}")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.mockito:mockito-inline:${libs.versions.mockito.get()}")
}

description = "support"
