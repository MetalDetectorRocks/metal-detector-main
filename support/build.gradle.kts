plugins {
  `java-library`
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
  implementation(libs.springBootStarterSecurity)
  implementation(libs.springJdbc)
  api(libs.springSecurityOAuth2Client)
  implementation(libs.commonsText)
  implementation(libs.httpClient)
  implementation(libs.lombok)
  implementation(libs.jacksonDatatypeJsr310)

  annotationProcessor(libs.lombok)

  api(libs.jjwtApi)
  implementation(libs.bundles.jjwt)

  testImplementation(libs.slf4jTest)
  testImplementation(libs.springBootStarterTest)
  testRuntimeOnly(libs.jupiterLauncher)

  testImplementation(libs.mockito)
  mockitoAgent(libs.mockito) { isTransitive = false }
}

tasks {
  test {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
  }
}

description = "support"
