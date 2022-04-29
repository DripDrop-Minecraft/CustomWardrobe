package command

import TAG
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission

class WardrobeCommand private constructor(): CommandExecutor {
    companion object {
        val IMPL: WardrobeCommand by lazy { WardrobeCommand() }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as Player
        return if (player.hasPermission(Permission.DEFAULT_PERMISSION.name)) {
            commandsForAdmin(player, args)
        } else {
            commandsForPlayer(player, args)
        }
    }

    private fun commandsForAdmin(player: Player, args: Array<out String>): Boolean = when(args.size) {
        0 -> showHelpForAdmin(player)
        1 -> executeWithoutArgs(args[0], true)
        2 -> executeWithOneArg(args)
        3 -> executeWithTwoArgs(args)
        4 -> executeWithThreeArgs(args)
        else -> invalidCommandHint(player)
    }

    private fun commandsForPlayer(player: Player, args: Array<out String>): Boolean = when(args.size) {
        0 -> showHelp(player)
        1 -> executeWithoutArgs(args[0])
        else -> invalidCommandHint(player)
    }

    private fun showHelpForAdmin(player: Player): Boolean {
        val help = """
            =====Wardrobe指令=====
            | 重新加载配置：/wardrobe reload
            | 打开自己衣柜：/wardrobe open
            | 重置自己衣柜：/wardrobe reset <页码或装备槽序号> (page 1, page 2, all, Slot 1, 2,.., 18)
            | 检查自己装备：/wardrobe check <页码> (page 1,2)
            | 打开指定玩家衣柜：/wardrobe open <player>
            | 重置指定玩家衣柜：/wardrobe reset <player> <页码或装备槽序号> (page 1, page 2, all, Slot 1, 2,.., 18)
            | 检查指定玩家装备：/wardrobe check <player> <页码> (page 1,2)
            =====================
        """.trimIndent()
        player.sendMessage("${ChatColor.GREEN}${help}")
        return true
    }

    private fun showHelp(player: Player): Boolean {
        val help = """
            =====Wardrobe指令=====
            | 打开衣柜：/wardrobe open
            | 重置衣柜：/wardrobe reset <页码或装备槽序号> (page 1, page 2, all, slot 1~18)
            | 检查装备：/wardrobe check <页码> (page 1, page 2)
            =====================
        """.trimIndent()
        player.sendMessage("${ChatColor.GREEN}${help}")
        return true
    }

    private fun invalidCommandHint(player: Player): Boolean {
        player.sendMessage("${ChatColor.RED}无效的Wardrobe指令！")
        return false
    }

    private fun executeWithoutArgs(cmd: String, isAdmin: Boolean = false): Boolean {
        cmd.apply {
            return when {
                equals("reload", true) && isAdmin -> reloadConfig()
                equals("open", true) -> openMyGui()
                else -> false
            }
        }
    }

    private fun reloadConfig(): Boolean {
        // TODO: 重新读取配置
        Bukkit.broadcastMessage("${ChatColor.YELLOW}CustomWardrobe已重新加载配置")
        Bukkit.getConsoleSender().sendMessage("${ChatColor.GREEN}$TAG ${ChatColor.RED}已重新加载配置")
        return true
    }

    private fun openMyGui(): Boolean {
        // TODO: 打开玩家自己的衣柜
        return true
    }

    private fun executeWithOneArg(args: Array<out String>): Boolean {
        // TODO
        return true
    }

    private fun executeWithTwoArgs(args: Array<out String>): Boolean {
        // TODO
        return true
    }

    private fun executeWithThreeArgs(args: Array<out String>): Boolean {
        // TODO
        return true
    }

}