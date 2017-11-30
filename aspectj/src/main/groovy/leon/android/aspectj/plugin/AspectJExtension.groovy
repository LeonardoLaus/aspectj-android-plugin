package leon.android.aspectj.plugin

class AspectJExtension {
    def includeJar = []
    def excludeJar = []
    def ajcArgs = []

    void includeJar(String... jars) {
        if (jars != null) {
            includeJar.addAll(jars)
        }
    }

    void excludeJar(String... jars) {
        if (jars != null) {
            excludeJar.addAll(jars)
        }
    }

    void ajcArgs(String... args) {
        if (args != null) {
            ajcArgs.addAll(args)
        }
    }
}