package com.joeshuff.headhunters.commands.skulls

import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.data.models.SkullDBData
import com.joeshuff.headhunters.util.SkullController
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ResummonCommand(
    private val plugin: HeadHuntersPlugin,
    private val teamDatabaseHandler: TeamDatabaseHandler,
    private val skullDatabaseHandler: SkullDatabaseHandler,
    private val skullController: SkullController
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}Only players can use this command.")
            return true
        }

        val team = teamDatabaseHandler.getTeamForPlayer(sender)
        if (team == null) {
            sender.sendMessage("{${ChatColor.RED}You must be part of a team to resummon skulls.")
            return true
        }

        val allHeads = skullDatabaseHandler.getSkullData(team.id).filter { it.earned }
        if (allHeads.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}Your team has not earned any skulls to resummon.")
            return true
        }

        // Resummon the skull
        val shrineLocation = team.shrineLocation
        if (shrineLocation == null) {
            sender.sendMessage("Your team does not have a shrine set. Cannot resummon skull.")
            return true
        }

        openPagedInventory(sender, allHeads, shrineLocation, 0)

        return true
    }

    private fun openPagedInventory(player: Player, skulls: List<SkullDBData>, shrineLocation: Location, page: Int) {
        val inventorySize = 27
        val inventory =
            Bukkit.createInventory(null, inventorySize, "${ChatColor.DARK_PURPLE}Earned Skulls (Page ${page + 1})")

        // Fill the current page with skulls
        val startIndex = page * 21
        val endIndex = (page + 1) * 21
        val currentSkulls = skulls.subList(startIndex.coerceAtMost(skulls.size), endIndex.coerceAtMost(skulls.size))

        var inventorySlot = 1 // Start at column 2 (skipping the first column)
        for (skullData in currentSkulls) {
            val entityType = EntityType.fromName(skullData.entityType) ?: continue
            val item = skullController.getSkullItemStack(entityType, skullData.earnedBy)

            inventory.setItem(inventorySlot, item)

            // Move to the next valid slot, skipping the first and last columns
            do {
                inventorySlot++
            } while (inventorySlot % 9 == 0 || inventorySlot % 9 == 8) // Skip columns 1 and 9
        }

        // Add navigation buttons
        if (page > 0) {
            inventory.setItem(9, createNavigationItem("${ChatColor.GREEN}Previous Page", Material.ARROW)) // Bottom-left
        }
        if (endIndex < skulls.size) {
            inventory.setItem(17, createNavigationItem("${ChatColor.GREEN}Next Page", Material.ARROW)) // Bottom-right
        }

        // Open the inventory
        player.openInventory(inventory)

        registerInventoryListener(inventory, shrineLocation)
    }

    private fun registerInventoryListener(inventory: Inventory, shrineLocation: Location) {
        fun extractPageNumber(title: String): Int {
            val regex = Regex("""Page (\d+)""")
            val match = regex.find(title) ?: return 0
            return match.groupValues[1].toInt() - 1
        }

        Bukkit.getPluginManager().registerEvents(object : Listener {
            @EventHandler
            fun onInventoryClose(event: InventoryCloseEvent) {
                if (inventory == event.inventory) {
                    plugin.logger.info("Unregistering inventory listener as closed")
                    HandlerList.unregisterAll(this)
                }
            }

            @EventHandler
            fun onInventoryClick(event: InventoryClickEvent) {
                val player = event.whoClicked as? Player ?: return

                if (inventory == event.inventory) {
                    plugin.logger.info("Unregistering inventory listener as event")
                    HandlerList.unregisterAll(this)
                }

                val inventoryTitle = event.view.title

                if (inventoryTitle.startsWith("${ChatColor.DARK_PURPLE}Earned Skulls")) {
                    event.isCancelled = true // Prevent item pickup

                    val item = event.currentItem ?: return
                    val meta = item.itemMeta ?: return

                    when (meta.displayName) {
                        "${ChatColor.GREEN}Next Page" -> {
                            val currentPage = extractPageNumber(inventoryTitle)
                            openPagedInventory(player,
                                skullDatabaseHandler.getSkullData(teamDatabaseHandler.getTeamForPlayer(player)!!.id)
                                    .filter { it.earned },
                                shrineLocation,
                                currentPage + 1
                            )
                        }

                        "${ChatColor.GREEN}Previous Page" -> {
                            val currentPage = extractPageNumber(inventoryTitle)
                            openPagedInventory(player,
                                skullDatabaseHandler.getSkullData(teamDatabaseHandler.getTeamForPlayer(player)!!.id)
                                    .filter { it.earned },
                                shrineLocation,
                                currentPage - 1
                            )
                        }

                        else -> {
                            skullController.spawnSkullAtLocation(item, shrineLocation)
                            player.closeInventory()
                        }
                    }
                }
            }
        }, plugin)
    }

    private fun createNavigationItem(name: String, material: Material): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta?.setDisplayName(name)
        item.itemMeta = meta
        return item
    }
}