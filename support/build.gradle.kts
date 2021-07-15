dependencies {
  implementation("org.apache.commons:commons-text")
  implementation("org.apache.httpcomponents:httpclient:${rootProject.extra["httpClientVersion"]}")
  implementation("org.springframework:spring-jdbc:${rootProject.extra["springJdbcVersion"]}")
  api("org.springframework.security:spring-security-oauth2-client:${rootProject.extra["springSecurityVersion"]}")
  api("io.jsonwebtoken:jjwt:${rootProject.extra["jsonwebtokenVersion"]}")
}

description = "support"
