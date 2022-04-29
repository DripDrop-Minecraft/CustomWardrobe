package listener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin

class WardrobeListener private constructor(): Listener {
    companion object {
        val IMPL: WardrobeListener by lazy { WardrobeListener() }
    }

    fun register(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    @EventHandler
    fun onInventoryOpen(event: InventoryClickEvent) {

    }

    @EventHandler
    fun onInventoryClose(event: InventoryClickEvent) {

    }
}