# FeralFriends
An app for the feral cat community application so crazy cat ladies like 
my wife can look on a map in their local area and identify stray cats. 
This will help users communicate and collaborate on how to help local 
stray cats by feeding them, getting them proper shelter and getting them 
spayed or neutered. They can also share the progress of these cats via pictures.
The app could also be extended for other animals like dogs, birds, dragons, etc.
# Installation
### Tools needed
  - [Android studio](https://developer.android.com/studio)
  - [Git](https://git-scm.com/)
  - [AVD Manager](https://developer.android.com/studio/run/managing-avds)
    * API 28 Q for Pixel
  - [Google Cloud Platform API Manager](https://console.developers.google.com/apis/dashboard)
    * You will need to register an API key
### Install Steps
  1. Clone this repository: [Clone instructions](https://confluence.atlassian.com/bitbucket/clone-a-repository-223217891.html)
  2. Open Android Studio
  3. Open "Existing project"
  4. Choose the directory where you cloned the repository.
  5. Hit the [play or run](https://developer.android.com/training/basics/firstapp/running-app) button to start the AVD emulator.
  6. Use the application
# APIs and Development tools used
  * [Google Places API](https://cloud.google.com/maps-platform/places)
  * [DynamoDB](https://aws.amazon.com/dynamodb/)
  * [AndroidX](https://developer.android.com/jetpack/androidx)
  * [Google Maps API](https://cloud.google.com/maps-platform)
# Gradle File
```gradle
apply plugin: 'com.android.application'

android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.example.feralfriends"
        minSdkVersion 24
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'androidx.appcompat:appcompat:1.0.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    implementation 'com.google.android.gms:play-services-maps:17.0.0'
    implementation 'com.google.android.gms:play-services-location:16.0.0'
    implementation 'com.google.code.gson:gson:2.8.6'
    implementation 'com.google.maps.android:android-maps-utils:0.6.2'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test:runner:1.1.0'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.1.0'
    implementation 'com.google.android.material:material:1.0.0'
    implementation 'com.google.android.gms:play-services-auth:17.0.0'
}
```
# File Structure
```bash
../app/src/ #Here is where all of our source code is
|
├── debug
│   └── res
│       └── values
│           └── google_maps_api.xml
├── main
│   ├── AndroidManifest.xml
│   ├── java
│   │   └── com
│   │       └── example
│   │           └── feralfriends
│   │               ├── Database
│   │               │   └── DatabaseAccess.java
│   │               ├── DatePickerFragment.java
│   │               ├── FriendActivity.java
│   │               ├── LoginActivity.java
│   │               ├── MapsActivity.java
│   │               └── models
│   │                   └── FeralFriend.java
│   └── res(XML/ UI Files)
├── release
│   └── res
│       └── values
│           └── google_maps_api.xml
└── test
    └── java
        └── com
            └── example
                └── feralfriends
                    └── ExampleUnitTest.java
```
# Contributors
  * Tyler Woods: Computer Science - [Contact](tmw42@students.uwf.edu)
  * Mark Bikakis: Cyber Security - [Contact](mjb91@students.uwf.edu)
  * Hunter Brock: Computer Science - [Contact](hcb14@students.uwf.edu)
