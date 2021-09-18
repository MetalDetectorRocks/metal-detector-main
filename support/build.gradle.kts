plugins {
  id("java-library")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.apache.commons:commons-text:${rootProject.extra["apacheCommonsTextVersion"]}")
  implementation("org.apache.httpcomponents:httpclient:${rootProject.extra["httpClientVersion"]}")
  implementation("org.springframework:spring-jdbc:${rootProject.extra["springVersion"]}")
  implementation("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${rootProject.extra["jacksonVersion"]}")

  annotationProcessor("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")

  api("org.springframework.security:spring-security-oauth2-client:${rootProject.extra["springSecurityVersion"]}")
  api("io.jsonwebtoken:jjwt:${rootProject.extra["jsonwebtokenVersion"]}")

  testImplementation("org.simplify4u:slf4j-mock:${rootProject.extra["slf4jMockVersion"]}")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "junit", module = "junit")
  }
  testImplementation("org.mockito:mockito-inline:${rootProject.extra["mockitoVersion"]}")
}

description = "support"
