buildscript {
    extra["smackVersion"] = "4.4.6"
    extra["koinVersion"] = "3.5.0"
    extra["PUBLISH_GROUP_ID"] = "io.github.chriscamed"
    extra["PUBLISH_VERSION"] = "0.0.5"
    extra["PUBLISH_ARTIFACT_ID"] = "XMPPMessenger"
}
val smackVersion: String by extra
val koinVersion: String by extra
val PUBLISH_GROUP_ID: String by extra
val PUBLISH_VERSION: String by extra
val PUBLISH_ARTIFACT_ID: String by extra
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

if (project.hasProperty("publish")) {
    apply(from = "publish-remote.gradle")
}

android {
    namespace = "com.medios.xmppmessenger"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    kotlinOptions {
        jvmTarget = "19"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha13")
    implementation("org.igniterealtime.smack:smack-tcp:${smackVersion}")
    implementation("org.igniterealtime.smack:smack-android-extensions:${smackVersion}")
    implementation("org.igniterealtime.smack:smack-extensions:${smackVersion}")
    implementation("org.igniterealtime.smack:smack-im:${smackVersion}")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}