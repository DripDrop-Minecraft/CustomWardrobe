package listener

import Wardrobe
import gui.Armor
import gui.CheckPlayerGui
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import utils.DataManager

class CheckPlayerGuiListener private constructor(): Listener {
    companion object {
        val IMPL: CheckPlayerGuiListener by lazy { CheckPlayerGuiListener() }
    }
    private val specifiedMaterial = "STAINED_GLASS_PANE"

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (!CheckPlayerGui.isGuiOpened) {
            return
        }
        Wardrobe.IMPL.apply {
            event.view.title.apply {
                closeWardrobeInventory(event, if (contains("[1/2]")) getFirstPage() else getSecondPage())
            }
        }
    }

    private fun closeWardrobeInventory(event: InventoryCloseEvent, manager: DataManager) {
        CheckPlayerGui.checkPlayerMain?.let {
            manager.apply {
                getConfig().set("${it.uniqueId}.name", it.name)
                (0..8).forEach { num ->
                    setInventoryItem(event, num, this)
                }
                saveConfig()
                reloadConfig()
            }
        }
    }

    private fun setInventoryItem(event: InventoryCloseEvent, number: Int, manager: DataManager) {
        CheckPlayerGui.checkPlayerMain?.let {
            val path = "${it.uniqueId}.Slot-${number + if (manager.pageName == "page1") 1 else 10}."
            event.inventory.apply {
                setArmor(manager, "${path}${Armor.HELMET.type}", getItem(number))
                setArmor(manager, "${path}${Armor.CHESTPLATE.type}", getItem(number + 9))
                setArmor(manager, "${path}${Armor.LEGGINGS.type}", getItem(number + 18))
                setArmor(manager, "${path}${Armor.BOOTS.type}", getItem(number + 27))
                setButton(manager, "${path}Button", getItem(number + 36))
            }
        }
    }

    private fun setArmor(manager: DataManager, path: String, itemStack: ItemStack?) {
        manager.getConfig().apply {
            if (itemStack == null || itemStack.type.toString().contains(specifiedMaterial)) {
                set(path, "none")
            } else {
                set(path, itemStack)
            }
        }
    }

    private fun setButton(manager: DataManager, path: String, itemStack: ItemStack?) {
        manager.getConfig().apply {
            itemStack.apply {
                val state = when {
                    this == null -> "Locked"
                    type.toString().contains(Regex("LIME(_|\\s)DYE")) -> "Equipped"
                    type.toString().contains(Regex("GRAY(_|\\s)DYE")) -> "Empty"
                    type.toString().contains(Regex("PINK(_|\\s)DYE")) -> "Ready"
                    else -> "Locked"
                }
                set(path, state)
            }
        }
    }
}