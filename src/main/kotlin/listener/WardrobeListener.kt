package listener

import Wardrobe
import gui.CheckPlayerGui
import gui.WardrobeGui
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import utils.ClickedSlot
import utils.DataManager
import utils.GuiTask

class WardrobeListener private constructor(): Listener {
    companion object {
        val IMPL: WardrobeListener by lazy { WardrobeListener() }
    }
    private val invalidMaterial = "STAINED_GLASS_PANE"

    @EventHandler
    fun onInventoryOpen(event: InventoryClickEvent) {
        event.apply {
            val player = whoClicked as Player
            val clickedSlot = ClickedSlot(
                slot = slot, inventory = inventory, player = player, title =  view.title
            )
            if ((!view.title.contains("[1/2]") && !view.title.contains("[2/2]"))
                || clickedInventory == null || currentItem == null) {
                return
            }
            updatePlayerInv(player, clickedSlot)
            clickButtonAction(player)
            clickButtonAction(player, clickedSlot) {
                if (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
                    GuiTask.shiftClickInventory(it)
                } else {
                    currentItem?.let { item ->
                        GuiTask.setAllowedItem(it, item, player.itemOnCursor)
                        player.updateInventory()
                    }
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryClickEvent) {
        if (!CheckPlayerGui.isGuiOpened) {
            return
        }
        event.apply {
            val player = whoClicked as Player
            Wardrobe.IMPL.apply {
                if (view.title.contains("[1/2]")) {
                    getFirstPage().closeAction(player, event)
                } else if (view.title.contains("[1/2]")) {
                    getSecondPage().closeAction(player, event)
                }
            }
        }
    }

    private fun DataManager.closeAction(player: Player, event: InventoryClickEvent) {
        getConfig().apply {
            set("${player.uniqueId}.name", player.name)
            var basePath = ""
            var path = ""
            var helmetPath = ""
            var chestplatePath = ""
            var leggingsPath = ""
            var bootsPath = ""
            (0..8).forEach {
                basePath = "${player.uniqueId}.Slot-${it + 1}."
                path = "${basePath}Button"
                helmetPath = "${basePath}Helmet"
                chestplatePath = "${basePath}Chestplate"
                leggingsPath = "${basePath}Leggings"
                bootsPath = "${basePath}Boots"
                event.apply {
                    set(helmetPath, "none")
                    set(chestplatePath, "none")
                    set(leggingsPath, "none")
                    set(bootsPath, "none")
                    if (!inventory.getItem(it)?.type.toString().contains(invalidMaterial)) {
                        set(helmetPath, inventory.getItem(it))
                    }
                    if (!inventory.getItem(it + 9)?.type.toString().contains(invalidMaterial)) {
                        set(chestplatePath, inventory.getItem(it + 9))
                    }
                    if (!inventory.getItem(it + 18)?.type.toString().contains(invalidMaterial)) {
                        set(leggingsPath, inventory.getItem(it + 18))
                    }
                    if (!inventory.getItem(it + 27)?.type.toString().contains(invalidMaterial)) {
                        set(bootsPath, inventory.getItem(it + 27))
                    }
                }
                val checkButton = event.inventory.getItem(it + 36)?.type.toString()
                when {
                    checkButton.contains(Regex("LIME(_|\\s)DYE")) -> set(path, "Equipped")
                    checkButton.contains(Regex("GRAY(_|\\s)DYE")) -> set(path, "Empty")
                    checkButton.contains(Regex("PINK(_|\\s)DYE")) -> set(path, "Ready")
                    else -> set(path, "Locked")
                }
                saveConfig()
                reloadConfig()
            }
        }
    }

    private fun InventoryClickEvent.clickButtonAction(player: Player, clickedSlot: ClickedSlot, action: (ClickedSlot) -> Unit) {
        clickedInventory?.let {
            val presentButton = when(slot) {
                in 0..8 -> it.getItem(slot + 36)
                in 9..17 -> it.getItem(slot + 27)
                in 18..26 -> it.getItem(slot + 18)
                in 27..35 -> it.getItem(slot + 9)
                else -> ItemStack(Material.DIRT)
            }
            Wardrobe.IMPL.getConfiguration().getConfig().apply {
                if (presentButton?.type.toString().contains(Regex("LIME(_|\\s)DYE"))) {
                    player.sendMessage(ChatColor.RED.toString() + ChatColor.translateAlternateColorCodes(
                        '&', getString("Wardrobe_Message.Modify_Armor_Denied") ?: "没有修改权限！"
                    ))
                    return
                }
                action(clickedSlot)
            }
        }
    }

    private fun InventoryClickEvent.clickButtonAction(player: Player) {
        isCancelled = true
        val condition0 = !inventory.getItem(slot)?.type.toString().contains(invalidMaterial)
        Wardrobe.IMPL.getConfiguration().getConfig().apply {
            fun setPlayerInv(): Player = player.apply {
                inventory.addItem(itemOnCursor.clone())
                setItemOnCursor(null)
            }

            val condition1 = slot == getInt("Next-Page-Button.Slot") && getBoolean("Next-Page-Button.Enable")
            val condition2 = slot == getInt("Previous-Page-Button.Slot") && getBoolean("Previous-Page-Button.Enable")
            val condition3 = slot == getInt("Go-Back-Button.Slot") && getBoolean("Go-Back-Button.Enable")
            val condition4 = slot == getInt("Close-Button.Slot") && getBoolean("Close-Button.Enable")

            when {
                condition0 && condition1 -> setPlayerInv().apply {
                    WardrobeGui.createWardrobeSecondPage(this)
                }
                condition0 && condition2 -> setPlayerInv().apply {
                    WardrobeGui.createWardrobeFirstPage(this)
                }
                condition0 && condition3 -> setPlayerInv().apply {
                    WardrobeGui.createWardrobeSecondPage(this)
                    getString("Go-Back-Button.Command")?.let { performCommand(it) }
                }
                condition0 && condition4 -> setPlayerInv().apply {
                    closeInventory()
                }
                else -> Unit
            }
        }
    }

    private fun InventoryClickEvent.updatePlayerInv(player: Player, clickedSlot: ClickedSlot) {
        clickedInventory?.let {
            if (it.type == InventoryType.PLAYER && (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT)) {
                currentItem?.let { item ->
                    if (GuiTask.shiftClick(clickedSlot, item)) {
                        clickedInventory?.setItem(slot, null)
                        player.updateInventory()
                    }
                }
            }
        }
    }
}