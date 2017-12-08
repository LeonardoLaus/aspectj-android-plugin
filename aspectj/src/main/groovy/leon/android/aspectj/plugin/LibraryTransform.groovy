package leon.android.aspectj.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

class LibraryTransform extends AspectJTransform {

    LibraryTransform(Project project) {
        super(project)
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY
    }

    @Override
    Set<? super QualifiedContent.Scope> getReferencedScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }
}