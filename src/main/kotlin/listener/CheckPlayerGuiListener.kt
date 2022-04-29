package listener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class CheckPlayerGuiListener private constructor(): Listener {
    companion object {
        val IMPL: CheckPlayerGuiListener by lazy { CheckPlayerGuiListener() }
    }

    fun register(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {

    }
}