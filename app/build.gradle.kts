import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id ("kotlinx-serialization")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.testapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.testapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Set value part
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${properties.getProperty("SUPABASE_ANON_KEY")}\"")
        buildConfigField("String", "SUPABASE_URL", "\"${properties.getProperty("SUPABASE_URL")}\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    configurations.all {
        exclude(group = "xmlpull", module = "xmlpull")
        exclude(group = "xpp3", module = "xpp3_min")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.safe.args.generator)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("xmlpull:xmlpull:1.1.3.4d_b4_min")
    implementation("androidx.navigation:navigation-compose:2.8.7")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")

    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")


    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")

    kapt("androidx.hilt:hilt-compiler:1.2.0")

    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    implementation ("com.airbnb.android:lottie-compose:6.1.0")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.24.2-alpha")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("io.coil-kt:coil-compose:2.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")

    // Supabase
    implementation ("io.github.jan-tennert.supabase:postgrest-kt:2.4.0")
    implementation ("io.github.jan-tennert.supabase:storage-kt:2.4.0")

    // Ktor
    implementation ("io.ktor:ktor-client-android:2.3.0")
    implementation ("io.ktor:ktor-client-core:2.3.0")
    implementation ("io.ktor:ktor-utils:2.3.0")


    implementation ("io.coil-kt:coil-compose:2.5.0")
    implementation ("org.apache.poi:poi-ooxml:5.2.3")
    implementation ("org.apache.xmlbeans:xmlbeans:5.1.1")
    implementation ("androidx.camera:camera-camera2:1.4.1")
    implementation ("androidx.camera:camera-lifecycle:1.0.0")
    implementation ("androidx.camera:camera-view:1.4.1")

    implementation("org.apache.commons:commons-imaging:1.0-alpha3")


    implementation ("com.airbnb.android:lottie-compose:6.1.0")


}