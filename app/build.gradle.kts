plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.luma"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.luma"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility =JavaVersion.VERSION_11
                targetCompatibility =JavaVersion.VERSION_11

    }
}






dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    implementation("io.noties.markwon:core:4.6.2") // إضافة مكتبة Markwon الأساسية
    implementation( "io.noties.markwon:ext-tables:4.6.2") // إضافة دعم الجداول
}
