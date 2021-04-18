dependencies {
  implementation("org.apache.commons:commons-text")
  implementation("org.apache.httpcomponents:httpclient:${rootProject.extra["httpClientVersion"]}")
  api("io.jsonwebtoken:jjwt:${rootProject.extra["jsonwebtokenVersion"]}")
}

description = "support"
