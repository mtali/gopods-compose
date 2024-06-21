// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.jetbrains.kotlin.android) apply false
  alias(libs.plugins.spotless)
}

subprojects {
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
  }

  apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("${layout.buildDirectory}/**/*.kt")
      ktlint().editorConfigOverride(
        mapOf(
          "indent_size" to "2",
          "continuation_indent_size" to "2",
          "max_line_length" to 150
        )
      )
      licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
      trimTrailingWhitespace()
      endWithNewline()
    }
    format("xml") {
      target("**/*.xml")
      targetExclude("**/build/**/*.xml")
      // Look for the first XML tag that isn't a comment (<!--) or the xml declaration (<?xml)
      licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
      trimTrailingWhitespace()
      endWithNewline()
    }
  }
}