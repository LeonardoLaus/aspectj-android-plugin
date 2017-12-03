package leon.android.aspectj.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.google.common.collect.ImmutableSet
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

/**
 * Created by roothost on 2017/12/1.
 */

class AspectJTransform extends Transform {

    private static final ASPECTJRT = "aspectjrt"
    private Project project
    private JavaCompile javaCompile
    def bootclasspath

    AspectJTransform(Project project) {
        this.project = project
        def configuration = new AndroidConfiguration(this.project)

        this.project.afterEvaluate {
            configuration.variants.all { variant ->
                this.javaCompile = variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile
                this.bootclasspath = configuration.bootClasspath.join(File.pathSeparator)
            }
        }
    }

    @Override
    String getName() {
        return "AspectJTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

//    PROJECT(1),
//    SUB_PROJECTS(4),
//    EXTERNAL_LIBRARIES(16),
//    TESTED_CODE(32),
//    PROVIDED_ONLY(64),
//    /** @deprecated */
//    @Deprecated
//    PROJECT_LOCAL_DEPS(2),
//    /** @deprecated */
//    @Deprecated
//    SUB_PROJECTS_LOCAL_DEPS(8);
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        def name = QualifiedContent.Scope.PROJECT_LOCAL_DEPS.name()
        def deprecated = QualifiedContent.Scope.class.getField(name).isAnnotationPresent(Deprecated.class)
        println("PROJECT_LOCAL_DEPS is deprecated?(${deprecated})")
        if (deprecated)
            return TransformManager.SCOPE_FULL_PROJECT
        return ImmutableSet.<QualifiedContent.Scope> builder()
                .addAll(TransformManager.SCOPE_FULL_PROJECT)
                .add(QualifiedContent.Scope.PROJECT_LOCAL_DEPS)
                .add(QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS)
                .build()
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        //是否依赖aspectjtr
        def aspectJrtAvailable = transformInvocation.inputs.any { TransformInput transformInput ->
            transformInput.jarInputs.any { JarInput jarInput ->
                println('any loop transformInput.jarInputs ' + jarInput.file.absolutePath)
                jarInput.file.absolutePath.contains(ASPECTJRT)
            }
        }
        if (aspectJrtAvailable) {
            println('aspect transform start.')
            weaveAspectTransform(transformInvocation)
            println('aspect transform end.')
        } else {
            println('cannot find dependencies for aspectjtr in classpath.')
            //将input对外输出 交给下一个任务处理
            transformInvocation.inputs.each { TransformInput transformInput ->
                //遍历源码目录
                transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                    //获取output目录
                    def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                            directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    //将input的目录复制到output指定目录
                    FileUtils.copyDirectory(directoryInput.file, dest)
                    println("directoryInput = ${directoryInput.name}")
                }
                //遍历Jar 一般是第三方依赖库jar文件
                transformInput.jarInputs.each { JarInput jarInput ->
                    // 重命名输出文件（同目录copyFile会冲突）
                    def jarName = jarInput.name
                    def md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
                    if (jarName.endsWith('.jar')) {
                        jarName = jarName.substring(0, jarName.length() - 4)
                    }
                    //生成输出路径
                    def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                            jarInput.contentTypes, jarInput.scopes, Format.JAR)
                    //将输入内容复制到输出
                    FileUtils.copyFile(jarInput.file, dest)
                    println("jarInput = ${jarInput.name}")
                }
            }
        }
    }

    private void weaveAspectTransform(TransformInvocation transformInvocation) {
        AJCCompile ajcCompile = new AJCCompile(project)
        if (javaCompile != null) {
            ajcCompile.encoding = javaCompile.options.encoding
            ajcCompile.sourceCompatibility = javaCompile.sourceCompatibility
            ajcCompile.targetCompatibility = javaCompile.targetCompatibility
        }
        ajcCompile.bootclasspath = this.bootclasspath

        //create aspect destination dir
        File aspectDirFile = transformInvocation.outputProvider.getContentLocation('aspect',
                getOutputTypes(), getScopes(), Format.DIRECTORY)
        if (aspectDirFile.exists()) {
            println("delete aspect directory: ${aspectDirFile.absolutePath}")
            FileUtils.deleteDirectoryContents(aspectDirFile)
        }
        FileUtils.mkdirs(aspectDirFile)
        ajcCompile.destinationDir = aspectDirFile.absolutePath
        ajcCompile.ajcArgs = project.aspectjOptions.ajcArgs
        def includeJars = project.aspectjOptions.includeJars
        def excludeJars = project.aspectjOptions.excludeJars
        transformInvocation.inputs.each { TransformInput input ->
            //源码输出到ajc编译器
            input.directoryInputs.each { DirectoryInput directoryInput ->
                println("directoryInput= ${directoryInput.file.absolutePath}")
                ajcCompile.aspectpath.add(directoryInput.file)
                ajcCompile.classpath.add(directoryInput.file)
                ajcCompile.inpath.add(directoryInput.file)
            }
            //处理依赖Jar
            input.jarInputs.each {JarInput jarInput->
                println("deal with jar input: ${jarInput.file.absolutePath}")
                ajcCompile.aspectpath.add(jarInput.file)
                ajcCompile.classpath.add(jarInput.file)

            }
        }

    }
}