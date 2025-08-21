// Файл верхнего уровня Gradle: здесь задаются общие настройки для всех модулей проекта.
plugins {
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.kotlin.android) apply false
	alias(libs.plugins.hilt) apply false
	alias(libs.plugins.ksp) apply false
}

buildscript {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
	dependencies {
		classpath(libs.gradle)
		classpath(libs.kotlin.gradle.plugin)
		classpath(libs.hilt.android.gradle.plugin)
		classpath(libs.androidx.navigation.safe.args.gradle.plugin)
		classpath(libs.sqlDelightGradle)
	}
}

