dependencies {
  implementation(libs.springBootStarterWeb)

  implementation(libs.commonsText)
  implementation(libs.lombok)

  annotationProcessor(libs.lombok)

  implementation(rootProject.projects.support)

  testImplementation(libs.springBootStarterTest)
}

description = "spotify"
