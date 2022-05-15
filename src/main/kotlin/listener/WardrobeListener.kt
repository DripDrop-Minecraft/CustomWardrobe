package listener

import Wardrobe
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class WardrobeListener private constructor(): Listener {
    companion object {
        val IMPL: WardrobeListener by lazy { WardrobeListener() }
    }

    @EventHandler
    fun onInventoryOpen(event: InventoryClickEvent) {

    }

    @EventHandler
    fun onInventoryClose(event: InventoryClickEvent) {

    }
}