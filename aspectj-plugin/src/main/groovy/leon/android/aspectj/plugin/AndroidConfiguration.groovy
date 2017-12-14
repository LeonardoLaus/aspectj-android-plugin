package leon.android.aspectj.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project

class AndroidConfiguration {
    def final project
    def final isAppPlugin
    def final isLibraryPlugin
    def final variants
    def final plugin

    AndroidConfiguration(Project project) {
        this.project = project
        this.isAppPlugin = project.plugins.withType(AppPlugin)
        this.isLibraryPlugin = project.plugins.withType(LibraryPlugin)
        if (!isAppPlugin && !isLibraryPlugin) {
            throw new GradleException("'android' or 'library' plugin required.")
        }
        if (isAppPlugin) {
            this.plugin = project.plugins.getPlugin(AppPlugin)
            this.variants = project.android.applicationVariants
        } else {
            this.plugin = project.plugins.getPlugin(LibraryPlugin)
            this.variants = project.android.libraryVariants
        }
    }
    /**
     * Return boot classpath.
     * @return Collection of classes.
     */
    List<File> getBootClasspath() {
        if (this.project.android.hasProperty('bootClasspath')) {
            return this.project.android.bootClasspath
        } else {
            return this.plugin.runtimeJarList
        }
    }

    AspectJExtension aspectjOptions() {
        return this.project.extensions.getByType(AspectJExtension)
    }
}