package gui

import Wardrobe
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object CheckPlayerGui {
    private var path: String = ""
    private var playerName: String = ""
    var checkPlayerMain: Player? = null
    var isGuiOpened = false
    fun checkName(playerName: String): Boolean {
        this.playerName = playerName
        Wardrobe.IMPL.getFirstPage().getConfig().apply {
            getConfigurationSection("")?.getKeys(false)?.forEach {
                getString("${it}.name")?.let { name ->
                    if (name.contains(playerName)) {
                        path = it
                        return true
                    }
                }
            }
            return false
        }
    }

    fun checkWardrobeFirstPage(player: Player) {
        checkWardrobeGui(player)
        WardrobeGui.createWardrobeFirstPage(player)
    }

    fun checkWardrobeSecondPage(player: Player) {
        checkWardrobeGui(player)
        WardrobeGui.createWardrobeSecondPage(player)
    }

    private fun checkWardrobeGui(player: Player) {
        var temp = Bukkit.getPlayer(path)
        if (temp == null) {
            for (checkPlayer in Bukkit.getOnlinePlayers()) {
                if (checkPlayer.uniqueId.toString().contains(path)) {
                    temp = checkPlayer
                    checkPlayerMain = checkPlayer
                    break
                }
            }
        }
        temp?.let {
            if (it.openInventory.title.contains("Wardrobe",true)) {
                player.inventory.addItem(player.itemOnCursor)
                player.setItemOnCursor(null)
                it.closeInventory()
            }
        }
        isGuiOpened = true
    }
}