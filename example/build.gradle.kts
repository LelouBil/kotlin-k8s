plugins {
    java
    id("com.ncorti.kotlin.gradle.template.plugin")
}

dependencies {
    //jackson
    implementation(platform("tools.jackson:jackson-bom:3.0.0"))
    // Now declare Jackson modules WITHOUT versions
    implementation("tools.jackson.core:jackson-databind")
    implementation("tools.jackson.dataformat:jackson-dataformat-yaml")
    implementation("jakarta.validation:jakarta.validation-api:4.0.0-M1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
}

//templateExampleConfig {
//    message.set("Just trying this gradle plugin...")
//}
