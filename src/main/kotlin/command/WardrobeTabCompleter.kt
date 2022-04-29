package command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission

class WardrobeTabCompleter private constructor(): TabCompleter {
    companion object {
        val IMPL: WardrobeTabCompleter by lazy { WardrobeTabCompleter() }
    }

    private var arguments = arrayListOf<String>()

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val player = sender as Player
        if (player.hasPermission(Permission.DEFAULT_PERMISSION.name)) {
            setArgumentsForAdmin(args)
        } else {
            setArgumentsForPlayer(args)
        }
        return arguments
    }

    private fun setArgumentsForAdmin(args: Array<out String>) {
        when(args.size) {
            1 -> arguments = arrayListOf("reload", "open", "reset", "check", "allow")
            2 -> setSecondArgument(args, true)
            3 -> setThirdArgument(args)
            4 -> setFourthArgument(args)
            else -> Unit
        }
    }

    private fun setSecondArgument(args: Array<out String>, isAdmin: Boolean = false) {
        arguments.clear()
        args[0].apply {
            when {
                isAdmin && (equals("open", true) || equals("reset", true)) -> {
                    Bukkit.getOnlinePlayers().forEach {
                        if (it.name.lowercase().startsWith(args.last())) {
                            arguments.add(it.name)
                        }
                    }
                }
                isAdmin && equals("allow", true) -> {
                    arrayListOf("helmet", "chestplate", "leggings", "boots").forEach {
                        if (it.lowercase().startsWith(args.last().lowercase())) {
                            arguments.add(it.lowercase())
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    private fun setThirdArgument(args: Array<out String>) {
        arguments.clear()
        if (args[0].equals("reset", true)) {
            arrayListOf("page", "all", "slot").forEach {
                if (it.lowercase().startsWith(args.last().lowercase())) {
                    arguments.add(it)
                }
            }
        }
    }

    private fun setFourthArgument(args: Array<out String>) {
        arguments.clear()
        if (args[0].equals("reset", true)) {
            when {
                args[2].equals("page", true) -> {
                    arrayListOf("1", "2").forEach {
                        if (it.lowercase().startsWith(args.last().lowercase())) {
                            arguments.add(it)
                        }
                    }
                }
                args[2].equals("slot", true) -> {
                    (1..18).forEach {
                        if (it.toString().lowercase().startsWith(args.last().lowercase())) {
                            arguments.add(it.toString().lowercase())
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    private fun setArgumentsForPlayer(args: Array<out String>) {
        when(args.size) {
            1 -> arguments = arrayListOf("open", "reset")
            2 -> setSecondArgument(args)
            3 -> setThirdArgument(args)
            4 -> setFourthArgument(args)
            else -> Unit
        }
    }
}