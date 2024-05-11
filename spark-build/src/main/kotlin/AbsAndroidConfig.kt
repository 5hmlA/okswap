import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonToolOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

abstract class AbsAndroidConfig : Plugin<Project> {


    /**
     * ```kotlin
     *     override fun pluginConfigs(): PluginManager.() -> Unit = {
     *         //有需要的话执行父类逻辑
     *         super.pluginConfigs().invoke(this)
     *         //执行自己的逻辑
     *         apply("kotlin-android")
     *     }
     * ```
     */
    abstract fun pluginConfigs(): PluginManager.() -> Unit

    /**
     * ```kotlin
     *     override fun androidExtensionConfig(): AndroidExtension.(Project, VersionCatalog) -> Unit {
     *         return { project, versionCatalog ->
     *             //有需要的话执行父类逻辑
     *             super.androidExtensionConfig().invoke(this,project,versionCatalog)
     *             //自己特有的逻辑
     *         }
     *     }
     * ```
     */
    abstract fun androidExtensionConfig(): AndroidExtension.(Project, VersionCatalog) -> Unit


    abstract fun kotlinOptionsConfig(): KotlinCommonToolOptions.(Project) -> Unit

    /**
     * ```kotlin
     *     override fun dependenciesConfig(): DependencyHandlerScope.(VersionCatalog) -> Unit = { vlibs: VersionCatalog ->
     *         //有需要的话执行父类逻辑
     *         super.dependenciesConfig().invoke(this, vlibs)
     *         //自己特有的逻辑
     *     }
     * ```
     */
    abstract fun dependenciesConfig(): DependencyHandlerScope.(VersionCatalog) -> Unit


    override fun apply(target: Project) {
        println("========================================= start $this ${target.name}".red)
        with(target) {
            with(pluginManager) {
                pluginConfigs()()
            }
            val catalog = vlibs
            android?.apply {
                androidExtensionConfig()(target, catalog)
            }
            tasks.withType<KotlinCompile>().configureEach {
                kotlinOptions {
                    kotlinOptionsConfig()(target)
                }
            }
            dependencies {
                dependenciesConfig()(catalog)
            }
        }
        println("============================================== end $this ${target.name}".red)
    }
}