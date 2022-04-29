package utils

import TAG
import Wardrobe
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader

class DataManager(private val wardrobe: Wardrobe) {
    private lateinit var fileConfig: FileConfiguration
    private lateinit var file: File
    private lateinit var fileNameInside: String
    private val externalPath = "plugins${File.separator}CustomWardrobe${File.separator}"
    var pageName = ""

    fun init(fileName: String): DataManager {
        fileNameInside = fileName
        file = File(externalPath, fileName)
        fileConfig = YamlConfiguration.loadConfiguration(file)
        if (!file.exists()) {
            wardrobe.logger.info("${ChatColor.GREEN}$TAG ${ChatColor.YELLOW}初始化配置文件$fileName")
            if (fileName.contains("config.yml")) {
                wardrobe.logger.info("${ChatColor.GREEN}$TAG ${ChatColor.YELLOW}配置文件${fileName}已保存")
                wardrobe.saveResource(fileName, false)
            } else {
                pageName = fileName.removeSuffix(".yml")
                saveConfig()
            }
        }
        loadConfig(fileName)
        return this
    }

    fun reloadConfig() {
        loadConfig(fileNameInside)
        wardrobe.logger.info("${ChatColor.GREEN}$TAG ${ChatColor.YELLOW}配置文件${file.name}已重新加载")
    }

    fun getConfig(): FileConfiguration = fileConfig

    fun saveConfig() {
        wardrobe.logger.apply {
            try {
                fileConfig.save(file)
                info("${ChatColor.GREEN}$TAG ${ChatColor.YELLOW}配置文件${file.name}已保存")
            } catch (e:Exception) {
                severe("${ChatColor.GREEN}$TAG ${ChatColor.RED}无法保存数据到${file.name}！")
            }
        }
    }

    private fun loadConfig(fileName: String) {
        wardrobe.apply {
            getResource(fileName)?.let {
                val default = YamlConfiguration.loadConfiguration(InputStreamReader(it))
                fileConfig.setDefaults(default)
                logger.info("${ChatColor.GREEN}$TAG ${ChatColor.YELLOW}加载文件${file.name}")
            }
        }
    }
}