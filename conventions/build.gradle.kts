plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    `java-library`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.1"
}

fun String.print() {
    println("\u001B[93m✨ $name >> ${this}\u001B[0m")
}

fun sysprop(name: String, def: String): String {
//    getProperties中所谓的"system properties"其实是指"java system"，而非"operation system"，概念完全不同，使用getProperties获得的其实是虚拟机的变量形如： -Djavaxxxx。
//    getenv(): 访问某个系统的环境变量(operation system properties)
    return System.getProperty(name, def)
}

repositories {
    gradlePluginPortal()
    google()
}


dependencies {
    //includeBuild()中拿不到项目的properties，这里通过System.property取
//    编译插件的时候就会用到，不需要配置，编译的时候修改就行了
//    val agp = sysprop("dep.agp.ver", "8.2.0")
//    val kagp = sysprop("dep.kagp.ver", "1.9.24")
//    val pgp = sysprop("dep.pgp.ver", "0.9.4")

    val agpVersion = "8.2.0"
//    compileOnly("com.android.tools.build:gradle:$agpVersion")
    compileOnly("com.android.tools.build:gradle-api:$agpVersion")
//    compileOnly("com.gradle.publish:plugin-publish-plugin:1.2.1")
//    https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-gradle-plugin
//    https://plugins.gradle.org/plugin/org.jetbrains.kotlin.android
//    https://github.com/JetBrains/kotlin/
//    kotlin("gradle-plugin", "1.9.24") == org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.24
    compileOnly(kotlin(module = "gradle-plugin", version = "1.9.24"))
    implementation("com.google.protobuf:protobuf-gradle-plugin:0.9.4")
    // help->dependencies只会输出implementation的库的依赖关系
}


"======== class = ${this.javaClass.superclass}".print()
"======== superclass= ${this.javaClass.superclass}".print()
"======== rootProject= $rootProject".print()

group = "io.github.5hmlA"
version = "0.1"

//学习如何使用 agp api
// https://github.com/android/gradle-recipes/tree/agp-8.4
publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/5hmlA/sparkj")
            credentials {
                username = System.getenv("GITHUB_USER")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        maven {
            //name会成为任务名字的一部分 publishOspPublicationTo [LocalTest] Repository
            name = "LocalTest"
            setUrl("${rootDir}/repo")
        }
    }
}

tasks.create("before publishPlugins") {
    doFirst {
        " >> do First before publishPlugins".print()
//        这个task在执行publishPlugins这个task之前执行，此时无法获取到下面的extension
//        val plugins = extensions.getByType<GradlePluginDevelopmentExtension>().plugins
//        plugins.forEach {
//            println("- plugin -- ${it.name} ${it.id} ${it.displayName}")
//        }
    }
    tasks.findByName("publishPlugins")?.dependsOn(this)
}

tasks.findByName("publishPlugins")?.doFirst {
    //不太明白为什么这里也报错 Extension of type 'GradlePluginDevelopmentExtension' does not exist
    println("xxxxxxxxxxxxxxxxxx ${this.javaClass}")
    val plugins = rootProject.extensions.getByType<GradlePluginDevelopmentExtension>().plugins
    plugins.removeIf {
        it.displayName.isNullOrEmpty()
    }
    plugins.forEach {
        println("- plugin xxxxxxxxxxxxxxxxxx-- ${it.name} ${it.id} ${it.displayName}")
    }
}

gradlePlugin {
    website = "https://github.com/5hmlA/conventions"
    vcsUrl = "https://github.com/5hmlA/conventions"
    plugins {
        register("android-config") {
            id = "${group}.android"
            displayName = "android config plugin"
            description = "android build common config for build.gradle, this will auto add android necessary dependencies"
            tags = listOf("config", "android", "convention")
            implementationClass = "AndroidConfig"
        }
        register("android-compose") {
            id = "${group}.android.compose"
            displayName = "android compose config plugin"
            description = "android compose config for build.gradle, necessary related settings for compose will be automatically set"
            tags = listOf("compose", "config", "android", "convention")
            implementationClass = "AndroidComposeConfig"
        }
        register("protobuf-config") {
            id = "${group}.protobuf"
            displayName = "protobuf config plugin"
            description = "protobuf config for any gradle project, necessary configuration and dependencies will be automatically set up"
            tags = listOf("protobuf", "config", "convention")
            implementationClass = "ProtobufConfig"
        }

//        因为xxx.gradle.kts注册插件的时候不会设置displayName 尝试这里覆盖注册，结果无效，
//        publishTask里会检测所有的plugin,被认为是重复注册了直接报错,所以同一个plugin再创建个id
        create("proto-convention") {
            id = "${group}.protobuf-convention"
            displayName = "protobuf convention plugin"
            description = "protobuf convention for any gradle project, necessary configuration and dependencies will be automatically set up"
            tags = listOf("protobuf", "config", "convention")
            implementationClass = "ProtobufConventionPlugin"
        }

    }
    //因为通过 xxx.gradle.kts创建的预编译脚本 会自动创建plugin但是没设置displayName和description
    //所以这里判断补充必要数据否则发布不了，执行 [plugin portal -> publishPlugins]的时候会报错
    val plugins = extensions.getByType<GradlePluginDevelopmentExtension>().plugins
//    这里不修改 上传的时候再处理
//    plugins.forEach {
//        if (it.displayName.isNullOrEmpty()) {
//            it.id = "$group.${it.id}"
//            it.displayName = "protobuf convention plugin"
//            it.description = "protobuf convention for any gradle project, necessary configuration and dependencies will be automatically set up"
//            it.tags = listOf("protobuf", "config", "convention")
//        }
//    }
    plugins.forEach {
        "- plugin -- ${it.name} ${it.id} ${it.displayName}".print()
    }
    "插件地址: https://plugins.gradle.org/u/ZuYun".print()
//    https://plugins.gradle.org/docs/mirroring
//    The URL to mirror is https://plugins.gradle.org/m2/
    "插件下载地址: https://plugins.gradle.org/m2/".print()
}
