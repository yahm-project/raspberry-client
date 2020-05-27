/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 */

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.72"

    // Apply the application plugin to add support for building a CLI application.
    application
}

group = "it.unibo.yahm"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")


    // Pi4J
    implementation("com.pi4j:pi4j-parent:1.2")

    //RxJava
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.8.1")
    implementation("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:2.8.1")

    // Tensorflow
    implementation("org.tensorflow:tensorflow:+")
    /*implementation("org.tensorflow:tensorflow-lite:2.2.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:0.0.0-nightly")
    implementation("org.tensorflow:tensorflow-lite-support:0.0.0-nightly")*/
}

application {
    // Define the main class for the application.
    mainClassName = "it.unibo.yahm.AppKt"
}
