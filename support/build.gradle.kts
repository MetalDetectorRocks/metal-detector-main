dependencies {
  implementation("org.apache.commons:commons-text")
  implementation("org.apache.httpcomponents:httpclient:${rootProject.extra["httpClientVersion"]}")
  api("org.springframework.security:spring-security-oauth2-client:${rootProject.extra["springSecurityVersion"]}")
  api("io.jsonwebtoken:jjwt:${rootProject.extra["jsonwebtokenVersion"]}")
}

description = "support"
