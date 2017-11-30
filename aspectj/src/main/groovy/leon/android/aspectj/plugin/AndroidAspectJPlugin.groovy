package leon.android.aspectj.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidAspectJPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //extension
        project.extensions.create('aspectjOptions', AspectJExtension)
    }
}