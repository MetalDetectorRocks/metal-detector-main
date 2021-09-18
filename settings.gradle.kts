include(
  ":butler", ":discogs", ":spotify",
  ":persistence", ":support", ":webapp",
  ":telegram"
)

rootProject.name = "metal-detector"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")
