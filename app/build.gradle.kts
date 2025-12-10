plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "uqac.dim.transportbus"
    compileSdk = 34

    defaultConfig {
        applicationId = "uqac.dim.transportbus"
        minSdk = 30
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Ajoutez cette ligne pour la gestion des SMS
    implementation ("com.google.android.gms:play-services-auth:20.6.0")
    implementation ("com.googlecode.libphonenumber:libphonenumber:8.13.18")
    implementation("com.google.firebase:firebase-bom:32.0.0")
    implementation ("com.google.firebase:firebase-auth")
    // Dernière version de Firebase Firestore
    implementation ("com.google.firebase:firebase-firestore:24.0.0")
    // Dernière version de Google Play Services
    implementation ("com.google.android.gms:play-services-base:18.0.0")
    implementation ("com.google.android.gms:play-services-auth:20.1.0")  // Exemple pour les services d'authentification
    implementation ("com.google.firebase:firebase-database:20.0.0")  // Exemple pour la base de données Firebase
    implementation ("com.google.firebase:firebase-firestore:24.1.0")  // Exemple pour Firestore
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.google.firebase:firebase-firestore:24.6.1")
    implementation("com.google.firebase:firebase-database:20.0.3")
    implementation("com.google.firebase:firebase-storage:20.2.1") // Corrected
    implementation("androidx.recyclerview:recyclerview:1.3.1")   // Corrected
    implementation("com.google.firebase:firebase-core:21.1.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

}
