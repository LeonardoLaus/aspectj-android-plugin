package leon.android.aspectj.plugin

import com.android.build.gradle.BasePlugin
import com.android.build.gradle.internal.variant.BaseVariantData
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency

class AspectJUtils {
    static List<BaseVariantData> getVariantDataList(BasePlugin plugin) {
        return plugin.variantManager.variantScopes.collect { variantScope ->
            variantScope.getVariantData()
        }
    }

    static boolean isAspectjRTContains(Project project) {
        def dependencies = project.configurations.getAsMap()
        dependencies.any { String name, Configuration configuration ->
            return configuration.dependencies.any { Dependency dependency ->
                dependency.group == 'org.aspectj' && dependency.name == 'aspectjrt'
            }
        }
    }
}