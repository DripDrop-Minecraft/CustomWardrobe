package gui

import Wardrobe
import armors
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
        val availableBackground = ItemStack(Material.DIRT)
        try {
            customItemStack.apply {
                availableBackground.apply {
                    type = getSlotMaterial(slot)
                    itemMeta?.lore = getLore(slot, title) {
                        itemMeta?.setDisplayName(ChatColor.translateAlternateColorCodes('&', it))
                    }
                }
            }
        } catch (e: Exception) {
            Wardrobe.IMPL.logger.severe(e.localizedMessage)
        }
        return availableBackground
    }

    fun createEmptyButton(customItemStack: CustomItemStack): ItemStack {
        return ItemStack(Material.ACACIA_BOAT)
    }

    fun createReadyButton(customItemStack: CustomItemStack): ItemStack {
        return ItemStack(Material.ACACIA_BOAT)
    }

    fun createEquippedButton(customItemStack: CustomItemStack): ItemStack {
        return ItemStack(Material.ACACIA_BOAT)
    }

    fun createGoBackAndCloseButton(inventory: Inventory): ItemStack {
        return ItemStack(Material.ACACIA_BOAT)
    }

    fun createNextAndPreviousButton(inventory: Inventory): ItemStack {
        return ItemStack(Material.ACACIA_BOAT)
    }

    private fun createCustomItemStack(customItemStack: CustomItemStack): ItemStack {
        return ItemStack(Material.ACACIA_BOAT)
    }

    private fun getLore(slot: Int, title: String, setName: (String) -> Unit): List<String> {
        val loreList = arrayListOf<String>()
        val map = hashMapOf<Armor, String>()
        Wardrobe.IMPL.getConfiguration().apply {
            fun setName(armor: Armor, p1: Int, p2: Int): String = getArmorSlotConfigName(title, armor, p1, p2)
            when(slot) {
                in 0..8 -> map[Armor.HELMET] = setName(Armor.HELMET, slot + 1, slot + 10)
                in 9..17 -> map[Armor.CHESTPLATE] = setName(Armor.CHESTPLATE, slot - 8, slot + 1)
                in 18..26 -> map[Armor.LEGGINGS] = setName(Armor.LEGGINGS, slot - 17, slot - 8)
                in 27..35 -> map[Armor.BOOTS] = setName(Armor.BOOTS, slot - 26, slot -17)
                else -> throw IllegalArgumentException("Found slot in wrong position when getting lore!")
            }
            map.forEach {
                setName(it.value)
                getConfig().getStringList("Availabel-Slot.${it.key.type}-Slot.Lore").forEach { lore ->
                    loreList.add(ChatColor.translateAlternateColorCodes('&', lore))
                }
            }
        }
        return loreList
    }

    private fun getArmorSlotConfigName(title: String, armor: Armor, posInPage1: Int, posInPage2: Int): String {
        Wardrobe.IMPL.apply {
            val pos = if (title.contains("[1/2]")) posInPage1 else posInPage2
            return "Availabel-${pos}.${armor.type}-${pos}.Name"
        }
    }
    private fun getSlotMaterial(slot: Int): Material = when {
        slot % 9 == 0 -> Material.valueOf("RED_STAINED_GLASS_PANE")
        slot % 9 == 1 -> Material.valueOf("ORANGE_STAINED_GLASS_PANE")
        slot % 9 == 2 -> Material.valueOf("YELLOW_STAINED_GLASS_PANE")
        slot % 9 == 3 -> Material.valueOf("LIME_STAINED_GLASS_PANE")
        slot % 9 == 4 -> Material.valueOf("GREEN_STAINED_GLASS_PANE")
        slot % 9 == 5 -> Material.valueOf("LIGHT_STAINED_GLASS_PANE")
        slot % 9 == 6 -> Material.valueOf("BLUE_STAINED_GLASS_PANE")
        slot % 9 == 7 -> Material.valueOf("MAGENTA_STAINED_GLASS_PANE")
        slot % 9 == 8 -> Material.valueOf("PURPLE_STAINED_GLASS_PANE")
        else -> throw IllegalArgumentException("Found slot in wrong position when getting material!")
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