plugins {
  `java-library`
}

dependencies {
  api(libs.springBootStarterDataJpa)

  implementation(libs.postgres)
  implementation(libs.lombok)

  annotationProcessor(libs.lombok)

  implementation(rootProject.projects.support)

  testImplementation(libs.springBootStarterTest)
  testRuntimeOnly(libs.jupiterLauncher)
  testImplementation(libs.h2)
  testImplementation(libs.mockito)
}

description = "persistence"
