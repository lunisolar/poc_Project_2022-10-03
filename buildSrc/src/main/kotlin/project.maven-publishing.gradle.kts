
plugins {
    `maven-publish`
}

publishing {
    publications {

        create<MavenPublication>(Project2022.publicationName) {
            from(components["java"])
        }

    }
}