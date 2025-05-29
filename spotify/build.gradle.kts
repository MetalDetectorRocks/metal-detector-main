dependencies {
  implementation(libs.springBootStarterWeb)

  implementation(libs.commonsText)
  implementation(libs.lombok)

  annotationProcessor(libs.lombok)

  implementation(rootProject.projects.support)

  testImplementation(libs.springBootStarterTest)
  testRuntimeOnly(libs.jupiterLauncher)
}

description = "spotify"
