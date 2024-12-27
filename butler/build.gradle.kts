val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
  implementation(libs.springBootStarterWeb)
  implementation(libs.commonsText)
  implementation(libs.lombok)

  implementation(rootProject.projects.support)

  annotationProcessor(libs.lombok)

  testImplementation(libs.commonsText)
  testImplementation(libs.springBootStarterTest)

  testImplementation(libs.mockito)
  mockitoAgent(libs.mockito) { isTransitive = false }
}

tasks {
  test {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
  }
}

description = "butler"
