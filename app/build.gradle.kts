import com.android.build.api.variant.FilterConfiguration
import org.gradle.kotlin.dsl.support.serviceOf
import java.util.Properties
import org.gradle.process.ExecOperations

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.coveralls)
    kotlin("kapt")
    id("jacoco")
}

// =========================================================================
// KOTLIN & DEPENDENCY CONFIGURATIONS
// =========================================================================

configurations.configureEach {
    resolutionStrategy {
        force(libs.kotlin.stdlib)
    }
}

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}

allOpen {
    annotation("com.myAllVideoBrowser.OpenForTesting")
}

jacoco {
    version = "0.8.1"
}

// =========================================================================
// BUILD CONFIGURATION VARIABLES
// =========================================================================


val abiFilterRaw = (project.findProperty("ABI_FILTERS") as? String ?: "").trim()

val abiFilterProperty = abiFilterRaw
    .split(Regex("[;,\n]"))
    .map { it.trim() }
    .filter { it.isNotBlank() }

println("ABI_FILTERS: $abiFilterProperty")
val activeFilters = abiFilterProperty.ifEmpty {
    listOf("arm64-v8a")
}

val isSingleAbiRequested = activeFilters.size == 1

val splitApksEnv = System.getenv("SPLITS_INCLUDE")?.lowercase()?.toBoolean() ?: true

val splitApks = if (isSingleAbiRequested) false else splitApksEnv

println("Mode::: SingleAbi=$isSingleAbiRequested, SplitApks=$splitApks, Filters=$abiFilterProperty")

val abiCodes = mapOf(
    "armeabi-v7a" to 1,
    "arm64-v8a" to 2,
    "x86" to 3,
    "x86_64" to 4
)

// =========================================================================
// ANDROID CONFIGURATION
// =========================================================================

android {
    namespace = "com.myAllVideoBrowser"
    compileSdk = libs.versions.targetSdk.get().toInt()
    ndkVersion = libs.versions.ndk.get()

    // Compile Options
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    // Dependencies Info
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    // Packaging Options
    packaging {
        resources {
            excludes += listOf(
                "mozilla/public-suffix-list.txt",
                "META-INF/*.kotlin_module",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "**/*.prof",
                "**/*.profm",
                "META-INF/com.android.tools/package-helpers/baseline-profiles/*.prof",
                "assets/dexopt/baseline.prof"
            )
        }
        jniLibs {
            useLegacyPackaging = true
            keepDebugSymbols += listOf(
                "**/libffmpeg.zip.so",
                "**/libpython.zip.so",
                "**/libffmpeg.so",
                "**/libffprobe.so",
                "**/libgojni.so",
                "**/libpython.so",
                "**/libqjs.so"
            )
        }
    }

    // Signing Configurations
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    // Default Config
    defaultConfig {
        applicationId = "com.myAllVideoBrowser"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 338
        versionName = "0.8.21.2"

        if (isSingleAbiRequested) {
            splits {
                abi {
                    isEnable = false
                }
            }
            ndk {
                abiFilters.clear()
                abiFilters.addAll(activeFilters)
            }
        } else if (splitApks) {
            splits {
                abi {
                    isEnable = true
                    reset()
                    include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
                    isUniversalApk = true
                }
            }
        } else {
            splits { abi { isEnable = false } }
        }
    }

    // Build Types
    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            enableUnitTestCoverage = false
            enableAndroidTestCoverage = false
        }
        release {
            enableUnitTestCoverage = false
            enableAndroidTestCoverage = false
            val isSigningAvailable = !System.getenv("KEYSTORE_PASSWORD").isNullOrBlank()
            if (isSigningAvailable) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = null
                println("No signing keys found. Building UNSIGNED apk.")
            }

            isMinifyEnabled = true
            isShrinkResources = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Data Binding & Build Features
    dataBinding {
        enable = true
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    // Test Options
    testOptions {
        unitTests.all {
            it.exclude("**/*")
        }
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    // Android Components - Version Code Adjustment
    androidComponents {
        onVariants(selector().all()) { variant ->
            variant.outputs.forEach { output ->
                val name = if (splitApks) {
                    output.filters.find {
                        it.filterType == FilterConfiguration.FilterType.ABI
                    }?.identifier
                } else if (isSingleAbiRequested) {
                    activeFilters[0]
                } else {
                    null
                }

                val baseAbiCode = abiCodes[name]
                if (baseAbiCode != null) {
                    output.versionCode.set(baseAbiCode + (output.versionCode.get()))
                }
            }
        }
    }

    // Lint Options
    lint {
        abortOnError = false
    }

    // Source Sets
    sourceSets {
        getByName("main") {
            jniLibs.srcDir("src/main/jniLibs")
        }
    }

    applicationVariants.all {
        val variant = this
        if (isSingleAbiRequested) {
            outputs.all {
                val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
                val abiName = activeFilters[0]
                // This generates: app-arm64-v8a-release-unsigned.apk
                output.outputFileName = "app-$abiName-${variant.name}-unsigned.apk"
                println("⚙️ F-Droid APK Rename: ${output.outputFileName}")
            }
        }
    }
}

// =========================================================================
// DEPENDENCIES
// =========================================================================

dependencies {
    println("\n📦 Resolving Dependencies...")

    // Core Android Libraries
    implementation(libs.activity)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.webkit)
    implementation(libs.coreKtx)
    implementation(libs.coreSplashscreen)

    // should fix ssl crashes on old devices
    implementation(libs.conscrypt.android)

    // Kotlin
    implementation(libs.kotlin.stdlib)

    // Coroutines & Work Manager
    implementation(libs.workRuntimeKtx)
    implementation(libs.workRxjava3)
    implementation(libs.fragmentKtx)
    implementation(libs.concurrentFuturesKtx)

    // Lifecycle Components
    implementation(libs.lifecycleExtensions)
    implementation(libs.lifecycleCommonJava8)
    implementation(libs.lifecycleLivedata)
    implementation(libs.lifecycleViewmodel)

    // Room Database
    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)
    implementation(libs.roomRxjava3)
    ksp(libs.roomCompiler)

    // Key value DB
    implementation(libs.mmkv)

    implementation(libs.kotlinx.coroutines.rx3)

    // Dagger 2 - Dependency Injection
    implementation(libs.daggerRuntime)
    implementation(libs.daggerAndroid)
    implementation(libs.daggerAndroidSupport)
    ksp(libs.daggerCompiler)
    ksp(libs.daggerAndroidProcessor)

    // Network - OkHttp & Retrofit
    implementation(libs.okHttpRuntime)
    implementation(libs.okHttpLogging)
    implementation(libs.retrofitRuntime)
    implementation(libs.retrofitGson)
    implementation(libs.retrofitRxjava3)
    implementation(libs.persistentCookieJar)

    // RxJava 3
    implementation(libs.rxjava3)
    implementation(libs.rxandroid3)

    // Media & Video Processing
    implementation(libs.youtubedl)
    implementation(libs.youtubedl.ffmpeg)
    implementation(libs.ffmpegKit)
    implementation(libs.media3Exoplayer)
    implementation(libs.media3ExoplayerDash)
    implementation(libs.media3ExoplayerHls)
    implementation(libs.media3ExoplayerRtsp)
    implementation(libs.media3Ui)
    implementation(libs.media3Extractor)
    implementation(libs.media3Database)
    implementation(libs.media3Decoder)
    implementation(libs.media3Datasource)
    implementation(libs.media3Common)
    implementation(libs.media3DatasourceOkhttp)

    // Image Loading
    implementation(libs.glideRuntime)

    // Utilities
    implementation(libs.kotlinxSerializationJson)
    implementation(libs.kotlinxSerializationCore)
    implementation(libs.timeago)

    // Desugar for Java 8+ APIs
    coreLibraryDesugaring(libs.desugarJdk)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoKotlin)
    androidTestImplementation(libs.testRunner)
    androidTestImplementation(libs.mockitoAndroid)
    androidTestImplementation(libs.espressoCore)
    androidTestImplementation(libs.espressoIntents)

    println("✓ Dependencies resolved\n")
}

// =========================================================================
// KSP CONFIGURATION
// =========================================================================

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

// =========================================================================
// COVERALLS CONFIGURATION
// =========================================================================

tasks.named("coveralls") {
    dependsOn("check")
    onlyIf { System.getenv("COVERALLS_REPO_TOKEN") != null }
}

