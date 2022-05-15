package utils

import Wardrobe
import gui.Armor
import gui.CustomItemStack
import gui.WardrobeGui
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object GuiTask {
    private const val invalidMaterial = "STAINED_GLASS_PANE"
    private val air = ItemStack(Material.AIR)

    fun shiftClick(clickedSlot: ClickedSlot, item: ItemStack): Boolean {
        clickedSlot.apply {
            (0..53).forEach {
                inventory.getItem(it)?.type.toString().apply {
                    if (!(contains(invalidMaterial)
                          && !contains("STAINED_GLASS_PANE(15)")
                          && !contains("BLACK_STAINED_GLASS_PANE"))
                    ) {
                        return false
                    }
                }
                return when(it) {
                    in 0..8 -> item.setSpecifiedArmor(this, it, 36, 0, Armor.HELMET)
                    in 9..17 -> item.setSpecifiedArmor(this, it, 27, -9, Armor.HELMET)
                    in 18..26 -> item.setSpecifiedArmor(this, it, 18, -18, Armor.HELMET)
                    in 27..35 -> item.setSpecifiedArmor(this, it, 9, -27, Armor.HELMET)
                    else -> false
                }
            }
            return false
        }
    }

    fun setAllowedItem(clickedSlot: ClickedSlot, item: ItemStack, itemOnCursor: ItemStack) {
        clickedSlot.apply {
            val allowedItem = AllowedItem(
                isClicked = item.type.toString().contains(invalidMaterial)
                        || !item.type.toString().contains("BLACK_STAINED_GLASS_PANE"),
                clickedItem = ClickedItem(
                    item = item,
                    itemOnCursor = itemOnCursor,
                    typeOnCursor = itemOnCursor.type.toString(),
                    itemLore = itemOnCursor.itemMeta?.lore ?: emptyList()
                )
            )
            when(slot) {
                in 0..8 -> setSpecifiedAllowedItem(allowedItem, clickedSlot, SlotPositionOffset(
                    offset0 = 36, offset1 = 0, offset2 = 9, offset3 = 18, offset4 = 27
                ))
                in 9..17 -> setSpecifiedAllowedItem(allowedItem, clickedSlot, SlotPositionOffset(
                    offset0 = 27, offset1 = -9, offset2 = 0, offset3 = 9, offset4 = 18
                ))
                in 18..26 -> setSpecifiedAllowedItem(allowedItem, clickedSlot, SlotPositionOffset(
                    offset0 = 18, offset1 = -18, offset2 = -9, offset3 = 0, offset4 = 9
                ))
                in 27..35 -> setSpecifiedAllowedItem(allowedItem, clickedSlot, SlotPositionOffset(
                    offset0 = 9, offset1 = -27, offset2 = -18, offset3 = -9, offset4 = 0
                ))
                in 36..44 -> {
                    if (item.type.toString().contains(Regex("PINK(_|\\s)DYE"))) {
                        if (title.contains("[1/2]")) {
                            setButton(true)
                        } else if (title.contains("[2/2]")) {
                            setButton(false)
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    private fun ClickedSlot.setButton(isFirstPage: Boolean) {
        val bg = CustomItemStack(slot - 36, inventory, title)
        (36..44).forEach {
            if (it != slot) {
                inventory.getItem(it)?.let { itm ->
                    if (itm.type.toString().contains(Regex("LIME(_|\\s)DYE"))) {
                        clearArmor(player)
                        equipArmor(this)
                        val background = CustomItemStack(it - 36, inventory, title)
                        if (inventory.getItem(it - 36)?.type.toString().contains(invalidMaterial)
                            && inventory.getItem(it - 27)?.type.toString().contains(invalidMaterial)
                            && inventory.getItem(it - 18)?.type.toString().contains(invalidMaterial)
                            && inventory.getItem(it - 9)?.type.toString().contains(invalidMaterial)
                        ) {
                            inventory.setItem(it, WardrobeGui.createReadyButton(background))
                        } else {
                            inventory.setItem(it, WardrobeGui.createEmptyButton(background))
                        }
                        inventory.setItem(slot - 36, WardrobeGui.createEquippedButton(bg))
                    }
                }
            }
        }
        setDataManger(bg, isFirstPage)
    }

    private fun ClickedSlot.setDataManger(bg: CustomItemStack, isFirstPage: Boolean) {
        val manager2: DataManager
        val manager1 = if (isFirstPage) {
            manager2 = Wardrobe.IMPL.getSecondPage()
            Wardrobe.IMPL.getFirstPage()
        } else {
            manager2 = Wardrobe.IMPL.getFirstPage()
            Wardrobe.IMPL.getSecondPage()
        }
        manager1.getConfig().getConfigurationSection(player.uniqueId.toString()).apply {
            if (this != null) {
                getKeys(false).forEach { key ->
                    if (!key.contains("name") && !"${this}.${key}.Button".contains("Equipped")) {
                        clearArmor(player)
                        equipArmor(this@setDataManger)
                        inventory.setItem(slot, WardrobeGui.createEquippedButton(bg))
                        manager2.let { dm ->
                            dm.getConfig().set("${this}.${key}.Button", "Ready")
                            dm.saveConfig()
                            dm.reloadConfig()
                        }
                    }
                }
            }
            if (giveEquippedArmor(player)) {
                equipArmor(this@setDataManger)
                inventory.setItem(slot, WardrobeGui.createEquippedButton(bg))
            }
        }
    }

    fun clearArmor(player: Player) {
        player.inventory.apply {
            helmet = null
            chestplate = null
            leggings = null
            boots = null
        }
    }

    fun equipArmor(clickedSlot: ClickedSlot) {
        clickedSlot.apply {
            inventory.apply {
                fun getArmorOrNull(offset: Int): ItemStack? = getItem(slot + offset).run {
                    when {
                        this?.type.toString().contains(invalidMaterial) -> this?.clone()
                        else -> null
                    }
                }
                player.inventory.apply {
                    helmet = getArmorOrNull(-36)
                    chestplate = getArmorOrNull( -27)
                    leggings = getArmorOrNull(-18)
                    boots = getArmorOrNull(-9)
                }
            }
        }
    }

    fun giveEquippedArmor(player: Player): Boolean {
        var count = 0
        var freeSpace = 0
        fun countArmors(vararg i: ItemStack?) {
            i.forEach {
                if (it != null) {
                    count++
                }
            }
        }
        player.apply {
            inventory.apply {
                fun equip(vararg i: ItemStack?) {
                    i.forEach {
                        if (it != null) {
                            addItem(it)
                            when (it) {
                                helmet -> helmet = air
                                chestplate -> chestplate = air
                                leggings -> leggings = air
                                boots -> boots = air
                                else -> Unit
                            }
                        }
                    }
                }
                countArmors(helmet, chestplate, leggings, boots)
                forEach {
                    if (it == null) {
                        freeSpace++
                    }
                    if (freeSpace >= 5) {
                        equip(helmet, chestplate, leggings, boots)
                        return true
                    }
                }
            }
            sendMessage(ChatColor.translateAlternateColorCodes('&', Wardrobe.IMPL.getConfiguration()
                .getConfig().getString("Wardrobe_Message.No_Space") ?: "没有足够空间容纳盔甲！"
            ))
            playSound(location, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
            return false
        }
    }

    fun shiftClickInventory(clickedSlot: ClickedSlot) {
        clickedSlot.apply {
            val itemStack = inventory.getItem(slot)
            when {
                itemStack == null || itemStack.type.toString().contains(invalidMaterial) -> Unit
                slot in 0..8 -> setItem(SlotPositionOffset(36,0, 9, 18, 27))
                slot in 9..17 -> setItem(SlotPositionOffset(27,-9, 0, 9, 18))
                slot in 18..26 -> setItem(SlotPositionOffset(18,-18, -9, 0, 9))
                slot in 27..35 -> setItem(SlotPositionOffset(9,-27, -18, -9, 0))
                else -> Unit
            }
        }
    }

    fun checkHelmet(clickedSlot: ClickedSlot, item: ClickedItem): Boolean = item.setSpecifiedArmor(
        clickedSlot,
        36,
        Armor.HELMET
    )

    fun checkChestPlate(clickedSlot: ClickedSlot, item: ClickedItem): Boolean = item.setSpecifiedArmor(
        clickedSlot,
        27,
        Armor.CHESTPLATE
    )

    fun checkLeggings(clickedSlot: ClickedSlot, item: ClickedItem): Boolean = item.setSpecifiedArmor(
        clickedSlot,
        18,
        Armor.LEGGINGS
    )

    fun checkBoots(clickedSlot: ClickedSlot, item: ClickedItem): Boolean = item.setSpecifiedArmor(
        clickedSlot,
        9,
        Armor.BOOTS
    )

    fun checkArmor(clickedSlot: ClickedSlot) {
        clickedSlot.apply {
            player.apply {
                inventory.apply {
                    hashMapOf(
                        slot - 36 to helmet,
                        slot - 27 to chestplate,
                        slot - 18 to leggings,
                        slot - 9 to boots
                    ).forEach {
                        setItem(it.key, it.value?.clone() ?: WardrobeGui.setItemBackground(CustomItemStack(
                            it.key, inventory, title
                        )))
                    }
                }
            }
        }
    }

    private fun setSpecifiedAllowedItem(allowedItem: AllowedItem, clickedSlot: ClickedSlot, offsets: SlotPositionOffset) {
        clickedSlot.apply {
            allowedItem.apply {
                if (isClicked) {
                    if (clickedItem.typeOnCursor.isNotEmpty()
                        && checkHelmet(clickedSlot, clickedItem)
                    ) {
                        inventory.setItem(slot, clickedItem.itemOnCursor)
                        player.setItemOnCursor(null)
                    }
                } else {
                    val checkButton = inventory.getItem(slot + offsets.offset0)?.data?.itemType.toString()
                    if (checkButton.contains(Regex("LIME(_|\\s)DYE"))) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes(
                            '&', Wardrobe.IMPL.getConfiguration()
                                .getConfig().getString("Wardrobe_Message.Modify_Armor_Denied") ?: "没有使用权限！"
                        ))
                        player.playSound(player.location, "ENTITY_VILLAGER_NO", 1.0f, 1.0f)
                    }
                    if (checkHelmet(clickedSlot, clickedItem)) {
                        inventory.setItem(slot, clickedItem.itemOnCursor)
                        player.setItemOnCursor(clickedItem.item)
                    }
                    if (clickedItem.item.type.toString() == "AIR") {
                        setItem(offsets)
                    }
                }
            }
        }
    }

    private infix fun ClickedSlot.setItem(offsets: SlotPositionOffset) {
        val itemStack = inventory.getItem(slot)
        val background = WardrobeGui.setItemBackground(CustomItemStack(slot, inventory, title))
        player.inventory.addItem(itemStack)
        inventory.setItem(slot, background)
        offsets.apply {
            if (inventory.getItem(slot + offset1)?.type.toString().contains(invalidMaterial)
                && inventory.getItem(slot + offset2)?.type.toString().contains(invalidMaterial)
                && inventory.getItem(slot + offset3)?.type.toString().contains(invalidMaterial)
                && inventory.getItem(slot + offset4)?.type.toString().contains(invalidMaterial)) {
                inventory.setItem(slot + offset0, background)
            }
        }
    }

    private fun ClickedItem.setSpecifiedArmor(clickedSlot: ClickedSlot, offset: Int, armor: Armor): Boolean {
        val displayName = itemOnCursor.itemMeta?.displayName ?: "none"
        Wardrobe.IMPL.getConfiguration().apply {
            val condition0 = typeOnCursor.contains("_${armor.type.uppercase()}")
            val condition1 = (getConfig().getString("Allow-Item.${armor.type}-Slot.Specific-Check-Lore") ?: "").run {
                !equals("none", true) && itemLore.any { it.contains(this) }
            }
            val condition2 = (getConfig().getString("Allow-Item.${armor.type}-Slot.Name") ?: "").run {
                displayName.contains(ChatColor.translateAlternateColorCodes('&', this))
            }
            val condition3 = (getConfig().getString("Allow-Item.${armor.type}-Slot.Type") ?: "").run {
                !equals("none",true) && itemLore.any { it.contains(this) }
            }
            val condition4 = (getConfig().getString("Allow-Item.${armor.type}-Slot.Lore") ?: "").run {
                !equals("none",true) && contains(typeOnCursor)
            }

            return if (condition0 || condition1 || (condition2 && condition3 && condition4)) {
                setPlayerInventory(clickedSlot, this@setSpecifiedArmor, clickedSlot.slot + offset)
                true
            } else {
                false
            }
        }
    }

    private fun ItemStack.setSpecifiedArmor(
        clickedSlot: ClickedSlot, position: Int,
        offset: Int,
        offset2: Int,
        armor: Armor
    ): Boolean {
        clickedSlot.apply {
            val displayName = itemMeta?.displayName ?: "none"
            val lores = itemMeta?.lore ?: emptyList()
            Wardrobe.IMPL.getConfiguration().apply {
                val condition0 = inventory.getItem(position)?.type.toString().contains("_${armor.type.uppercase()}")
                val condition1 = (getConfig().getString("Allow-Item.${armor.type}-Slot.Specific-Check-Lore") ?: "").run {
                    !equals("none", true) && lores.any { it.contains(this) }
                }
                val condition2 = (getConfig().getString("Allow-Item.${armor.type}-Slot.Name") ?: "").run {
                    displayName.contains(ChatColor.translateAlternateColorCodes('&', this))
                }
                val condition3 = (getConfig().getString("Allow-Item.${armor.type}-Slot.Type") ?: "").run {
                    !equals("none",true) && lores.any { it.contains(this) }
                }
                val condition4 = (getConfig().getString("Allow-Item.${armor.type}-Slot.Lore") ?: "").run {
                    !equals("none",true) && contains(type.toString())
                }

                return if (condition0 || condition1 || (condition2 && condition3 && condition4)) {
                    inventory.apply {
                        setItem(position, this@setSpecifiedArmor)
                        setItem(position + offset, WardrobeGui.createReadyButton(
                            CustomItemStack(position + offset2, inventory, title)
                        ))
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun setPlayerInventory(clickedSlot: ClickedSlot, item: ClickedItem, position: Int) {
        clickedSlot.apply {
            inventory.setItem(slot, item.itemOnCursor)
            player.setItemOnCursor(null)
            inventory.setItem(position, WardrobeGui.createReadyButton(
                CustomItemStack(position, inventory, title)
            ))
        }
    }
}

data class AllowedItem(
    val isClicked: Boolean,
    val clickedItem: ClickedItem
)
data class SlotPositionOffset(
    val offset0: Int,
    val offset1: Int,
    val offset2: Int,
    val offset3: Int,
    val offset4: Int
)

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