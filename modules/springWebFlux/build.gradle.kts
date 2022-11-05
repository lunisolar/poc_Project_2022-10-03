import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("project.spring-conventions")
	id("project.lava.rdf.spring-conventions")
}

java.sourceCompatibility = JavaVersion.VERSION_17


dependencies {

	//<editor-fold desc="internal">

	implementation(project(":modules:javaModule"))

	//</editor-fold>

	//<editor-fold desc="external">

	implementation("org.springframework.boot:spring-boot-starter-webflux") {
		exclude(module = "spring-boot-starter-logging")
	}
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")        
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")

	//</editor-fold>
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
