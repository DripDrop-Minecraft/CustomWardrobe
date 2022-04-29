package utils

import Wardrobe
import gui.Armor
import org.bukkit.entity.Player

object DataTask {

    fun resetAllWardrobe(player: Player): Boolean {
        Wardrobe.IMPL.apply {
            return resetWardrobe(player, getFirstPage()) && resetWardrobe(player, getSecondPage())
        }
    }

    fun resetWardrobeInSpecifiedPage(player: Player, page: String): Boolean = when {
        page.equals("1", true) -> resetWardrobe(player, Wardrobe.IMPL.getFirstPage())
        page.equals("2", true) -> resetWardrobe(player, Wardrobe.IMPL.getSecondPage())
        else -> false
    }

    fun resetWardrobeInSpecifiedSlot(player: Player, slot: String): Boolean {
        val slotPath = "Slot-$slot"
        Wardrobe.IMPL.apply {
            return when {
                slot.toInt() in 1..9 -> resetWardrobeSlot(player, getFirstPage(), slotPath)
                slot.toInt() in 10..18 -> resetWardrobeSlot(player, getSecondPage(), slotPath)
                else -> false
            }
        }
    }

    private fun resetWardrobeSlot(player: Player, manager: DataManager, slot: String): Boolean {
        val id = player.uniqueId.toString()
        manager.apply {
            getConfig().apply {
                set("${id}.name", player.name)
                set("${id}.${slot}.${Armor.HELMET.type}", "none")
                set("${id}.${slot}.${Armor.CHESTPLATE.type}", "none")
                set("${id}.${slot}.${Armor.LEGGINGS.type}", "none")
                set("${id}.${slot}.${Armor.BOOTS.type}", "none")
                set("${id}.${slot}.Button", "Locked")
            }
            saveConfig()
            reloadConfig()
            return true
        }
    }

    private fun resetWardrobe(player: Player, manager: DataManager): Boolean {
        manager.apply {
            getConfig().set(player.uniqueId.toString(), null)
            saveConfig()
            reloadConfig()
            return manager.getConfig().getConfigurationSection(player.uniqueId.toString()) == null
        }
    }
}