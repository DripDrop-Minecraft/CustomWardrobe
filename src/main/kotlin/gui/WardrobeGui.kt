package gui

import Wardrobe
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import utils.ClickedSlot
import utils.DataManager
import utils.GuiTask

object WardrobeGui {
    private val BACKGROUND = ItemStack(Material.DIRT).apply {
        type = Material.valueOf("BLACK_STAINED_GLASS_PANE")
        itemMeta?.setDisplayName("")
    }
    private val armors = arrayListOf(Armor.HELMET, Armor.CHESTPLATE, Armor.LEGGINGS, Armor.BOOTS)
    private var currentGuiName: String = ""

    fun createWardrobeFirstPage(player: Player) {
        val inventory = createWardrobePage(player, "1")
        checkEquippedSlotsInPage2(player)
        player.openInventory(inventory)
    }

    fun createWardrobeSecondPage(player: Player) = player.openInventory(createWardrobePage(player, "2"))

    private fun createBasicBackground(inventory: Inventory, page: String) {
        if (page.equals("1", true)) {
            armors.forEach {
                setBasicBackground(it, this::page1SetBasicBackgroundForArmor)
            }
        } else {

        }
    }

    fun createAvailableSlotBackground(inventory: Inventory, name: String, player: Player) {

    }

    fun setItemBackground(customItemStack: CustomItemStack): ItemStack {

    }

    fun createEmptyButton(customItemStack: CustomItemStack): ItemStack {

    }

    fun createReadyButton(customItemStack: CustomItemStack): ItemStack {

    }

    fun createEquippedButton(customItemStack: CustomItemStack): ItemStack {

    }

    fun createGoBackAndCloseButton(inventory: Inventory): ItemStack {

    }

    fun createNextAndPreviousButton(inventory: Inventory): ItemStack {

    }

    private fun createCustomItemStack(customItemStack: CustomItemStack): ItemStack {

    }

    private fun page1SetBasicBackgroundForArmor(slot: Int, armor: Armor, bg: ItemStack) {
        when(slot) {
            in 1..9 -> {}
            in 10..18 -> {}
            in 19..27 -> {}
            in 28..36 -> {}
            in 37..45 -> {}
            else -> Unit
        }
    }

    private fun setBasicBackground(armor: Armor, handler: (Int, Armor, ItemStack) -> Unit) {
        val config = Wardrobe.IMPL.getConfiguration().getConfig()
        val background = ItemStack(Material.DIRT).apply {
            type = Material.valueOf("BLACK_STAINED_GLASS_PANE")
        }
        config.apply {
            (1..45).forEach {
                handler(it, armor, background)
            }
        }
    }

    private fun createWardrobePage(player: Player, page: String): Inventory {
        val title = "${Wardrobe.IMPL.config.getString("Title") ?: "DripDrop Wardrobe"} [$page/2]"
        currentGuiName = ChatColor.translateAlternateColorCodes('&', title)
        val wardrobePage = Bukkit.createInventory(player, 54, currentGuiName)
        wardrobePage.apply {
            (45..53).forEach {
                setItem(it, BACKGROUND)
            }
            createGoBackAndCloseButton(this)
            createNextAndPreviousButton(this)
            createBasicBackground(this, page)
            createAvailableSlotBackground(this, currentGuiName, player)
            createCheckButton(CreatedInventory(this, player, currentGuiName))
        }
        return wardrobePage
    }

    private fun createCheckButton(ci: CreatedInventory) {
        ci.inventory.apply {
            var buttonCheck: String
            var checkSlot1: String
            var checkSlot2: String
            var checkSlot3: String
            var checkSlot4: String
            val material = "STAINED_GLASS_PANE"
            (36..44).forEach {
                buttonCheck = getItem(it)?.type.toString()
                if (buttonCheck.contains("PINK_DYE") || buttonCheck.contains("PINK DYE")) {
                    checkSlot1 = getItem(it - 36)?.type.toString()
                    checkSlot2 = getItem(it - 27)?.type.toString()
                    checkSlot3 = getItem(it - 18)?.type.toString()
                    checkSlot4 = getItem(it - 9)?.type.toString()
                    if (checkSlot1.contains(material) && checkSlot2.contains(material)
                        && checkSlot3.contains(material) && checkSlot4.contains(material)) {
                        setItem(it, createEmptyButton(CustomItemStack(it, this, ci.name)))
                    }
                } else if (buttonCheck.contains("LIME_DYE") || buttonCheck.contains("LIME DYE")) {
                    GuiTask.checkArmor(ClickedSlot(
                        it,
                        this,
                        ci.player,
                        ci.name
                    ))
                }
            }
        }
    }

    private fun checkEquippedSlotsInPage2(player: Player) {
        Wardrobe.IMPL.getSecondPage().apply {
            getConfig().getConfigurationSection(player.uniqueId.toString())?.let {
                it.getKeys(false).forEach { path ->
                    if (!path.contains("name")) {
                        setWardrobePage2(player, path, this)
                    }
                }
            }
        }
    }

    private fun setWardrobePage2(player: Player, path: String, manager: DataManager) {
        val id = player.uniqueId.toString()
        manager.apply {
            fun setItemStackForPlayer(armor: Armor, itemStack: ItemStack?) {
                getConfig().set("${id}.${path}.${armor.type}", itemStack ?: "none")
            }
            getConfig().apply {
                getString("${id}.${path}.Button")?.let {
                    if (it.contains("Equipped")) {
                        setItemStackForPlayer(Armor.HELMET, player.inventory.helmet)
                        setItemStackForPlayer(Armor.CHESTPLATE, player.inventory.chestplate)
                        setItemStackForPlayer(Armor.LEGGINGS, player.inventory.leggings)
                        setItemStackForPlayer(Armor.BOOTS, player.inventory.boots)
                    }
                }
            }
            saveConfig()
            reloadConfig()
        }
    }
}

enum class Armor(val type: String) {
    HELMET("Helmet"),
    CHESTPLATE("Chestplate"),
    LEGGINGS("Leggings"),
    BOOTS("Boots")
}

data class CreatedInventory(
    val inventory: Inventory,
    val player: Player,
    val name: String
)

data class CustomItemStack(
    val slot: Int,
    val inventory: Inventory,
    val title: String
)