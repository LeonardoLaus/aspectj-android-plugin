package leon.android.aspectj.plugin

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Project

class AJCCompile {
    def project
    def inpath = []
    def aspectpath = []
    def classpath = []
    def ajcArgs = []
    def bootclasspath
    def encoding
    def sourceCompatibility
    def targetCompatibility
    def destinationDir

    AJCCompile(Project project) {
        this.project = project
    }

    void compile() {
        final def log = project.logger
        def args = [
                '-classpath', classpath.join(File.pathSeparator),
                '-bootclasspath', bootclasspath,
                '-d', destinationDir,
                '-target', targetCompatibility,
                '-source', sourceCompatibility,
                '-encoding', encoding,
                '-showWeaveInfo'
        ]
        if (!inpath.isEmpty()) {
            args.add('-inpath')
            args.add(inpath.join(File.pathSeparator))
        }
        if (!aspectpath.isEmpty()) {
            args.add('-aspectpath')
            args.add(aspectpath.join(File.pathSeparator))
        }
        if (!ajcArgs.isEmpty()) {
            if (!ajcArgs.contains('-warn')
                    && !ajcArgs.contains('nowarn')) {
                args.add('-nowarn')
            }
            args.addAll(ajcArgs)
        } else {
            args.add('-nowarn')
        }

        log.debug "ajc args: " + Arrays.toString(args)

        MessageHandler handler = new MessageHandler(true)
        Main main = new Main()
        main.run(args as String[], handler)
        for (IMessage message : handler.getMessages(null, true)) {
            switch (message.getKind()) {
                case IMessage.ABORT:
                case IMessage.ERROR:
                case IMessage.FAIL:
                    log.error message.message, message.thrown
                    break
                case IMessage.WARNING:
                    log.warn message.message, message.thrown
                    break
                case IMessage.INFO:
                    log.info message.message, message.thrown
                    break
                case IMessage.DEBUG:
                    log.debug message.message, message.thrown
                    break
            }
        }
        main.quit()
    }
}