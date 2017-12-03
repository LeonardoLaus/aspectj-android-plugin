package leon.android.aspectj.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project

class AndroidConfiguration {
    def final project
    def final hasApp
    def final hasLib
    def final variants
//    def final bootClassPaths

    AndroidConfiguration(Project project) {
        this.project = project
        this.hasApp = project.plugins.withType(AppPlugin)
        this.hasLib = project.plugins.withType(LibraryPlugin)
        if (!hasApp && !hasLib) {
            throw new GradleException("'android' or 'library' plugin required.")
        }
        if (hasApp) {
            this.variants = project.android.applicationVariants
        } else {
            this.variants = project.android.libraryVariants
        }
//        if (project.android.hasProperty('bootClasspath')) {
//            this.bootClassPaths = project.android.bootclasspath
//        } else {
//            def plugin = project.plugins.getPlugin(hasApp ? AppPlugin : LibraryPlugin)
//            this.bootClassPaths = plugin.runtimeJarList
//        }
    }
    /**
     * Return boot classpath.
     * @return Collection of classes.
     */
    List<File> getBootClasspath() {
        if (project.android.hasProperty('bootClasspath')) {
            return project.android.bootClasspath
        } else {
            def plugin = project.plugins.getPlugin(hasApp ? AppPlugin : LibraryPlugin)
            return plugin.runtimeJarList
        }
    }
}