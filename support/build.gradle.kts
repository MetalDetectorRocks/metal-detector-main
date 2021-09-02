dependencies {
  implementation("org.apache.commons:commons-text")
  implementation("org.apache.httpcomponents:httpclient:${rootProject.extra["httpClientVersion"]}")
  implementation("org.springframework:spring-jdbc:${rootProject.extra["springVersion"]}")
  api("org.springframework.security:spring-security-oauth2-client:${rootProject.extra["springSecurityVersion"]}")
  api("io.jsonwebtoken:jjwt:${rootProject.extra["jsonwebtokenVersion"]}")
  testImplementation("org.simplify4u:slf4j-mock:${rootProject.extra["slf4jMockVersion"]}")
}

description = "support"
