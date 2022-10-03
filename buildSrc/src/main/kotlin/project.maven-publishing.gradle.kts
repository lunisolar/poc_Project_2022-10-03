
plugins {
    `maven-publish`
}

publishing {
    publications {

        create<MavenPublication>(Meta.Build.publicationName) {
            from(components["java"])
        }

    }
}