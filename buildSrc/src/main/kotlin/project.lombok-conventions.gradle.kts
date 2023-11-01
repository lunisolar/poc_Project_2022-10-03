plugins {
    id("project.java-conventions")

    // Not only adds Lombok as annotation processor but also makes it compatible with Java doc tasks.
    id("io.freefair.lombok")
}