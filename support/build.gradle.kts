plugins {
  id("java-library")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.apache.commons:commons-text:${libs.versions.apacheCommonsText.get()}")
  implementation("org.apache.httpcomponents:httpclient:${libs.versions.httpClient.get()}")
  implementation("org.springframework:spring-jdbc:${libs.versions.spring.get()}")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${libs.versions.jackson.get()}")

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  api("org.springframework.security:spring-security-oauth2-client:${libs.versions.springSecurity.get()}")
  api("io.jsonwebtoken:jjwt:${libs.versions.jsonwebtoken.get()}")

  testImplementation("org.simplify4u:slf4j-mock:${libs.versions.slf4jMock.get()}")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "junit", module = "junit")
  }
  testImplementation("org.mockito:mockito-inline:${libs.versions.mockito.get()}")
}

description = "support"
