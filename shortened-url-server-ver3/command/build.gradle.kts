apply(plugin = "org.springframework.boot")

dependencies {
    implementation(project(":shortened-url-server-ver3:common"))
    implementation(project(":shortened-url-server-ver3:domain"))
    implementation(project(":shortened-url-server-ver3:infrastructure:jpa"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-tx")
}
