package leon.android.aspectj.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidAspectJPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //extension
        def aspectjOptions = project.extensions.create('aspectjOptions', AspectJExtension)

        // dependencies aspectjrt
        project.repositories.mavenCentral()
        project.afterEvaluate {
            def hasAspectjRT = AspectJUtils.isAspectjRTContains(project)
            if (!hasAspectjRT) {
                project.dependencies.add('implementation', "org.aspectj:aspectjrt:${aspectjOptions.ajrt}")
                println("apsectjOptions ajrt=${aspectjOptions.ajrt}")
            } else {
                println('already dependency org.aspectj:aspectjrt')
            }
        }
        //构建时间
        project.gradle.addListener(new BuildTimeTrace())

        def isApplication = project.plugins.withType(AppPlugin)
        def isLibrary = project.plugins.withType(LibraryPlugin)
        if (isApplication) {
            //Transform Api
            AppExtension android = project.extensions.getByType(AppExtension)
            android.registerTransform(new AspectJTransform(project))
        } else if (isLibrary) {
            LibraryExtension library = project.extensions.getByType(LibraryExtension)
            library.registerTransform(new LibraryTransform(project))
        }
    }
}