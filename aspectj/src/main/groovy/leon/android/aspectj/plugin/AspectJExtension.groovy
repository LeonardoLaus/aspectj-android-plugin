package leon.android.aspectj.plugin

class AspectJExtension {
    def ajrt = '1.8.12'
    def includeJars = []
    def excludeJars = []
    def ajcArgs = []

    void includeJars(String... jars) {
        if (jars != null) {
            includeJars.addAll(jars)
        }
    }

    void excludeJars(String... jars) {
        if (jars != null) {
            excludeJars.addAll(jars)
        }
    }

    void ajcArgs(String... args) {
        if (args != null) {
            ajcArgs.addAll(args)
        }
    }
}