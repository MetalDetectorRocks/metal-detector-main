plugins {
  `java-library`
}

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
  testImplementation(libs.mockitoInline)
}

description = "support"
