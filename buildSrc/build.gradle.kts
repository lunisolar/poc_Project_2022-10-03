plugins {
    `kotlin-dsl`
}

java {
    modularity.inferModulePath.set(false)

    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}


dependencies {

    implementation(libs.freefairLombok.plugin)

    implementation(libs.kotlin.plugin)
    implementation(libs.kotlin.allopen.plugin)
    implementation(libs.kotlin.lombok.plugin)

    implementation(libs.changelog.plugin)

    implementation(libs.spring.boot.plugin)
    implementation(libs.spring.dep.mgt.plugin)
    
}
