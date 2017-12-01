package leon.android.aspectj.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidAspectJPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //extension
        project.extensions.create('aspectjOptions', AspectJExtension)
        def isApplication = project.plugins.withType(AppPlugin)
        if (isApplication) {
            //构建时间
            project.gradle.addListener(new BuildTimeTrace())
            AppExtension android = project.extensions.getByType(AppExtension)
            android.registerTransform(new AspectJTransform(project))
        }
    }
}