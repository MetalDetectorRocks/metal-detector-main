dependencies {
  implementation(libs.springBootStarterWeb)
  implementation(libs.commonsText)
  implementation(libs.lombok)

  implementation(rootProject.projects.support)

  annotationProcessor(libs.lombok)

  testImplementation(libs.commonsText)
  testImplementation(libs.mockitoInline)
  testImplementation(libs.springBootStarterTest)
}

description = "butler"
