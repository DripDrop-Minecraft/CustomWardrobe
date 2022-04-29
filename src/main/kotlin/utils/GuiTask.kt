package utils

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object GuiTask {
    fun shiftClick(slot: ClickedSlot, item: ItemStack): Boolean {
        return true
    }

    fun setAllowedItem(slot: ClickedSlot, item: ItemStack, itemOnCursor: ItemStack) {

    }

    fun clearArmor(player: Player) {

    }

    fun equipArmor(slot: ClickedSlot) {

    }

    fun giveEquippedArmor(player: Player): Boolean {
        return true
    }

    fun shiftClickInventory(slot: ClickedSlot) {

    }

    fun checkHelmet(slot: ClickedSlot, item: ClickedItem): Boolean {
        return true
    }

    fun checkChestPlate(slot: ClickedSlot, item: ClickedItem): Boolean {
        return true
    }

    fun checkBoots(slot: ClickedSlot, item: ClickedItem): Boolean {
        return true
    }

    fun checkArmor(slot: ClickedSlot) {

    }
}

data class ClickedSlot(
    val slot: Int,
    val inventory: Inventory,
    val player: Player,
    val title: String
)

data class ClickedItem(
    val item: ItemStack,
    val itemOnCursor: ItemStack,
    val typeOnCursor: String,
    val itemLore: List<String>
)