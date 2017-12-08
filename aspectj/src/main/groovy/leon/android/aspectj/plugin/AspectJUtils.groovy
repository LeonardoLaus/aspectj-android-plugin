package leon.android.aspectj.plugin

import com.android.build.gradle.BasePlugin
import com.android.build.gradle.internal.variant.BaseVariantData

class AspectJUtils {
    static List<BaseVariantData> getVariantDataList(BasePlugin plugin) {
        return plugin.variantManager.variantScopes.collect { variantScope ->
            variantScope.getVariantData()
        }
    }
}