plugins {
  id("java-library")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework:spring-jdbc")
  api("org.springframework.security:spring-security-oauth2-client")
  implementation("org.apache.commons:commons-text:${libs.versions.commonsText.get()}")
  implementation("org.apache.httpcomponents.client5:httpclient5")
  implementation("org.projectlombok:lombok:${libs.versions.lombok.get()}")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")

  api("io.jsonwebtoken:jjwt-api:${libs.versions.jsonwebtoken.get()}")
  implementation("io.jsonwebtoken:jjwt-impl:${libs.versions.jsonwebtoken.get()}")
  implementation("io.jsonwebtoken:jjwt-jackson:${libs.versions.jsonwebtoken.get()}")

  testImplementation("com.github.valfirst:slf4j-test:${libs.versions.slf4jTest.get()}")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.mockito:mockito-inline")
}

description = "support"
