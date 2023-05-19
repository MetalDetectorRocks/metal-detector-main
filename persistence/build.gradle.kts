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
  testImplementation(libs.h2)
  testImplementation(libs.mockitoInline)
}

description = "persistence"
