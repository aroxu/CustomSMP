package me.aroxu.customsmp.events

import kotlin.math.*
import me.aroxu.customsmp.CustomSMPPlugin
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.player.*
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveEvent : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val destination = event.to
        val sm = Bukkit.getServer().scoreboardManager
        val sc = sm.mainScoreboard
        if (sc.getObjective("RegionJoined") == null)
                sc.registerNewObjective("RegionJoined", "dummy", text("RegionJoined"))
        val ob = sc.getObjective("RegionJoined")!!
        if(sc.getObjective("WarCooldown") == null)
            sc.registerNewObjective("WarCooldown","dummy",text("WarCooldown"))
        val cob = sc.getObjective("WarCooldown")!!
        if(target.world == Bukkit.getWorld("world")){
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
                    if (CustomSMPPlugin.warTeams.any { u ->
                                if (CustomSMPPlugin.teamsRegion[u.first] == null ||
                                                CustomSMPPlugin.teamsRegion[u.second] == null
                                ) {
                                    return@run
                                }
                                CustomSMPPlugin.teamsRegion[u.first]!!.any { it == region } ||
                                        CustomSMPPlugin.teamsRegion[u.second]!!.any { it == region } &&
                                        CustomSMPPlugin.playerTeam[target.uniqueId] != null &&
                                        (u.first == CustomSMPPlugin.playerTeam[target.uniqueId]!! ||
                                        u.second == CustomSMPPlugin.playerTeam[target.uniqueId]!!)
                            } && CustomSMPPlugin.isInWar[target.uniqueId]!!
                    )
                            return@run

                    val regionTeam = CustomSMPPlugin.teamsRegion.filterValues { it.contains(region) }
                    if(regionTeam.isNotEmpty() && cob.getScore(regionTeam.keys.first().toString()).score == 1) return@run

                    if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (destination.x in x1..x2 && destination.z in z1..z2) {
                            event.isCancelled = true
                        }
                    }
                    else if (isTargetInTeam &&
                                    targetRegion.contains(region) &&
                                    ob.getScore(target.uniqueId.toString()).score != 1
                    ) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (destination.x in x1..x2 && destination.z in z1..z2) {
                            target.bedSpawnLocation = target.location
                            ob.getScore(target.uniqueId.toString()).score = 1
                        }
                    }
                    if (CustomSMPPlugin.survivalLife[target.uniqueId]!! < 1) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (event.from.x in x1..x2 &&
                                        event.from.z in z1..z2 &&
                                        (destination.x !in x1..x2 || destination.z !in z1..z2)
                        ) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val destination = event.to
        val sm = Bukkit.getServer().scoreboardManager
        val sc = sm.mainScoreboard
        if(sc.getObjective("WarCooldown") == null)
            sc.registerNewObjective("WarCooldown","dummy",text("WarCooldown"))
        val cob = sc.getObjective("WarCooldown")!!

        CustomSMPPlugin.regionsName.forEach { region ->
            run {
                if (CustomSMPPlugin.warTeams.any { u ->
                        if (CustomSMPPlugin.teamsRegion[u.first] == null ||
                            CustomSMPPlugin.teamsRegion[u.second] == null
                        ) {
                            return@run
                        }
                        CustomSMPPlugin.teamsRegion[u.first]!!.any { it == region } ||
                                CustomSMPPlugin.teamsRegion[u.second]!!.any { it == region } &&
                                CustomSMPPlugin.playerTeam[target.uniqueId] != null &&
                                (u.first == CustomSMPPlugin.playerTeam[target.uniqueId]!! ||
                                        u.second == CustomSMPPlugin.playerTeam[target.uniqueId]!!)
                    } && CustomSMPPlugin.isInWar[target.uniqueId]!!
                )
                    return@run

                val regionTeam = CustomSMPPlugin.teamsRegion.filterValues { it.contains(region) }
                if(regionTeam.isNotEmpty() && cob.getScore(regionTeam.keys.first().toString()).score == 1) return@run

                if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                    val regionPos = CustomSMPPlugin.regionsPos[region]!!

                    val x1 = min(regionPos[0], regionPos[2])
                    val x2 = max(regionPos[0], regionPos[2])
                    val z1 = min(regionPos[1], regionPos[3])
                    val z2 = max(regionPos[1], regionPos[3])

                    if (destination.x in x1..x2 && destination.z in z1..z2) {
                        event.isCancelled = true
                    }
                }
                if (CustomSMPPlugin.survivalLife[target.uniqueId]!! < 1) {
                    val regionPos = CustomSMPPlugin.regionsPos[region]!!

                    val x1 = min(regionPos[0], regionPos[2])
                    val x2 = max(regionPos[0], regionPos[2])
                    val z1 = min(regionPos[1], regionPos[3])
                    val z2 = max(regionPos[1], regionPos[3])

                    if (event.from.x in x1..x2 &&
                                    event.from.z in z1..z2 &&
                                    (destination.x !in x1..x2 || destination.z !in z1..z2)
                    ) {
                        event.isCancelled = true
                    }
                }
            }
        }

    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val block = event.clickedBlock
        val sm = Bukkit.getServer().scoreboardManager
        val sc = sm.mainScoreboard
        if(sc.getObjective("WarCooldown") == null)
            sc.registerNewObjective("WarCooldown","dummy",text("WarCooldown"))
        val cob = sc.getObjective("WarCooldown")!!

        if(target.world == Bukkit.getWorld("world")) {
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
                    if (CustomSMPPlugin.warTeams.any { u ->
                            if (CustomSMPPlugin.teamsRegion[u.first] == null ||
                                CustomSMPPlugin.teamsRegion[u.second] == null
                            ) {
                                return@run
                            }
                            CustomSMPPlugin.teamsRegion[u.first]!!.any { it == region } ||
                                    CustomSMPPlugin.teamsRegion[u.second]!!.any { it == region } &&
                                    CustomSMPPlugin.playerTeam[target.uniqueId] != null &&
                                    (u.first == CustomSMPPlugin.playerTeam[target.uniqueId]!! ||
                                            u.second == CustomSMPPlugin.playerTeam[target.uniqueId]!!)
                        } && CustomSMPPlugin.isInWar[target.uniqueId]!!
                    )
                        return@run

                    val regionTeam = CustomSMPPlugin.teamsRegion.filterValues { it.contains(region) }
                    if (regionTeam.isNotEmpty() && cob.getScore(
                            regionTeam.keys.first().toString()
                        ).score == 1
                    ) return@run

                    if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (block != null && block.location.x in x1..x2 && block.location.z in z1..z2) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val entity = event.rightClicked
        val sm = Bukkit.getServer().scoreboardManager
        val sc = sm.mainScoreboard
        if(sc.getObjective("WarCooldown") == null)
            sc.registerNewObjective("WarCooldown","dummy",text("WarCooldown"))
        val cob = sc.getObjective("WarCooldown")!!
        if(target.world == Bukkit.getWorld("world")) {
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
                    if (CustomSMPPlugin.warTeams.any { u ->
                            if (CustomSMPPlugin.teamsRegion[u.first] == null ||
                                CustomSMPPlugin.teamsRegion[u.second] == null
                            ) {
                                return@run
                            }
                            CustomSMPPlugin.teamsRegion[u.first]!!.any { it == region } ||
                                    CustomSMPPlugin.teamsRegion[u.second]!!.any { it == region } &&
                                    CustomSMPPlugin.playerTeam[target.uniqueId] != null &&
                                    (u.first == CustomSMPPlugin.playerTeam[target.uniqueId]!! ||
                                            u.second == CustomSMPPlugin.playerTeam[target.uniqueId]!!)
                        } && CustomSMPPlugin.isInWar[target.uniqueId]!!
                    )
                        return@run

                    val regionTeam = CustomSMPPlugin.teamsRegion.filterValues { it.contains(region) }
                    if (regionTeam.isNotEmpty() && cob.getScore(
                            regionTeam.keys.first().toString()
                        ).score == 1
                    ) return@run

                    if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (entity.location.x in x1..x2 && entity.location.z in z1..z2) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val block = event.block
        val sm = Bukkit.getServer().scoreboardManager
        val sc = sm.mainScoreboard
        if(sc.getObjective("WarCooldown") == null)
            sc.registerNewObjective("WarCooldown","dummy",text("WarCooldown"))
        val cob = sc.getObjective("WarCooldown")!!

        if(target.world == Bukkit.getWorld("world")) {
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
                    if (CustomSMPPlugin.warTeams.any { u ->
                            if (CustomSMPPlugin.teamsRegion[u.first] == null ||
                                CustomSMPPlugin.teamsRegion[u.second] == null
                            ) {
                                return@run
                            }
                            CustomSMPPlugin.teamsRegion[u.first]!!.any { it == region } ||
                                    CustomSMPPlugin.teamsRegion[u.second]!!.any { it == region } &&
                                    CustomSMPPlugin.playerTeam[target.uniqueId] != null &&
                                    (u.first == CustomSMPPlugin.playerTeam[target.uniqueId]!! ||
                                            u.second == CustomSMPPlugin.playerTeam[target.uniqueId]!!)
                        } && CustomSMPPlugin.isInWar[target.uniqueId]!!
                    )
                        return@run

                    val regionTeam = CustomSMPPlugin.teamsRegion.filterValues { it.contains(region) }
                    if (regionTeam.isNotEmpty() && cob.getScore(
                            regionTeam.keys.first().toString()
                        ).score == 1
                    ) return@run

                    if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (block.location.x in x1..x2 && block.location.z in z1..z2) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val block = event.block
        val sm = Bukkit.getServer().scoreboardManager
        val sc = sm.mainScoreboard
        if(sc.getObjective("WarCooldown") == null)
            sc.registerNewObjective("WarCooldown","dummy",text("WarCooldown"))
        val cob = sc.getObjective("WarCooldown")!!

        if(target.world == Bukkit.getWorld("world")) {
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
                    if (CustomSMPPlugin.warTeams.any { u ->
                            if (CustomSMPPlugin.teamsRegion[u.first] == null ||
                                CustomSMPPlugin.teamsRegion[u.second] == null
                            ) {
                                return@run
                            }
                            CustomSMPPlugin.teamsRegion[u.first]!!.any { it == region } ||
                                    CustomSMPPlugin.teamsRegion[u.second]!!.any { it == region } &&
                                    CustomSMPPlugin.playerTeam[target.uniqueId] != null &&
                                    (u.first == CustomSMPPlugin.playerTeam[target.uniqueId]!! ||
                                            u.second == CustomSMPPlugin.playerTeam[target.uniqueId]!!)
                        } && CustomSMPPlugin.isInWar[target.uniqueId]!!
                    )
                        return@run

                    val regionTeam = CustomSMPPlugin.teamsRegion.filterValues { it.contains(region) }
                    if (regionTeam.isNotEmpty() && cob.getScore(
                            regionTeam.keys.first().toString()
                        ).score == 1
                    ) return@run

                    if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (block.location.x in x1..x2 && block.location.z in z1..z2) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerBukkitFill(event: PlayerBucketFillEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val block = event.blockClicked
        val sm = Bukkit.getServer().scoreboardManager
        val sc = sm.mainScoreboard
        if(sc.getObjective("WarCooldown") == null)
            sc.registerNewObjective("WarCooldown","dummy",text("WarCooldown"))
        val cob = sc.getObjective("WarCooldown")!!

        if(target.world == Bukkit.getWorld("world")) {
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
                    if (CustomSMPPlugin.warTeams.any { u ->
                            if (CustomSMPPlugin.teamsRegion[u.first] == null ||
                                CustomSMPPlugin.teamsRegion[u.second] == null
                            ) {
                                return@run
                            }
                            CustomSMPPlugin.teamsRegion[u.first]!!.any { it == region } ||
                                    CustomSMPPlugin.teamsRegion[u.second]!!.any { it == region } &&
                                    CustomSMPPlugin.playerTeam[target.uniqueId] != null &&
                                    (u.first == CustomSMPPlugin.playerTeam[target.uniqueId]!! ||
                                            u.second == CustomSMPPlugin.playerTeam[target.uniqueId]!!)
                        } && CustomSMPPlugin.isInWar[target.uniqueId]!!
                    )
                        return@run

                    val regionTeam = CustomSMPPlugin.teamsRegion.filterValues { it.contains(region) }
                    if (regionTeam.isNotEmpty() && cob.getScore(
                            regionTeam.keys.first().toString()
                        ).score == 1
                    ) return@run

                    if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (block.location.x in x1..x2 && block.location.z in z1..z2) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerBucketEmpty(event: PlayerBucketEmptyEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val block = event.blockClicked
        val sm = Bukkit.getServer().scoreboardManager
        val sc = sm.mainScoreboard
        if(sc.getObjective("WarCooldown") == null)
            sc.registerNewObjective("WarCooldown","dummy",text("WarCooldown"))
        val cob = sc.getObjective("WarCooldown")!!

        if(target.world == Bukkit.getWorld("world")) {
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
                    if (CustomSMPPlugin.warTeams.any { u ->
                            if (CustomSMPPlugin.teamsRegion[u.first] == null ||
                                CustomSMPPlugin.teamsRegion[u.second] == null
                            ) {
                                return@run
                            }
                            CustomSMPPlugin.teamsRegion[u.first]!!.any { it == region } ||
                                    CustomSMPPlugin.teamsRegion[u.second]!!.any { it == region } &&
                                    CustomSMPPlugin.playerTeam[target.uniqueId] != null &&
                                    (u.first == CustomSMPPlugin.playerTeam[target.uniqueId]!! ||
                                            u.second == CustomSMPPlugin.playerTeam[target.uniqueId]!!)
                        } && CustomSMPPlugin.isInWar[target.uniqueId]!!
                    )
                        return@run

                    val regionTeam = CustomSMPPlugin.teamsRegion.filterValues { it.contains(region) }
                    if (regionTeam.isNotEmpty() && cob.getScore(
                            regionTeam.keys.first().toString()
                        ).score == 1
                    ) return@run

                    if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (block.location.x in x1..x2 && block.location.z in z1..z2) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerAttemptPickupItem(event: PlayerAttemptPickupItemEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val item = event.item
        val sm = Bukkit.getServer().scoreboardManager
        val sc = sm.mainScoreboard
        if(sc.getObjective("WarCooldown") == null)
            sc.registerNewObjective("WarCooldown","dummy",text("WarCooldown"))
        val cob = sc.getObjective("WarCooldown")!!

        if(target.world == Bukkit.getWorld("world")) {
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
                    if (CustomSMPPlugin.warTeams.any { u ->
                            if (CustomSMPPlugin.teamsRegion[u.first] == null ||
                                CustomSMPPlugin.teamsRegion[u.second] == null
                            ) {
                                return@run
                            }
                            CustomSMPPlugin.teamsRegion[u.first]!!.any { it == region } ||
                                    CustomSMPPlugin.teamsRegion[u.second]!!.any { it == region } &&
                                    CustomSMPPlugin.playerTeam[target.uniqueId] != null &&
                                    (u.first == CustomSMPPlugin.playerTeam[target.uniqueId]!! ||
                                            u.second == CustomSMPPlugin.playerTeam[target.uniqueId]!!)
                        } && CustomSMPPlugin.isInWar[target.uniqueId]!!
                    )
                        return@run

                    val regionTeam = CustomSMPPlugin.teamsRegion.filterValues { it.contains(region) }
                    if (regionTeam.isNotEmpty() && cob.getScore(
                            regionTeam.keys.first().toString()
                        ).score == 1
                    ) return@run

                    if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (item.location.x in x1..x2 && item.location.z in z1..z2) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent){
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val entity = event.caught

        if(target.world == Bukkit.getWorld("world")) {
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
                    if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (entity != null) {
                            if (entity.location.x in x1..x2 && entity.location.z in z1..z2) {
                                event.isCancelled = true
                            }
                        }
                    }
                }
            }
        }
    }

}
