package me.aroxu.customsmp.events

import me.aroxu.customsmp.CustomSMPPlugin
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.*
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import kotlin.math.*


class PlayerMoveEvent : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val targetPosition = target.location
        val destination = event.to
        val sm = Bukkit.getServer().scoreboardManager
        val sc = sm.mainScoreboard
        if (sc.getObjective("RegionJoined") == null) sc.registerNewObjective("RegionJoined","dummy",text("RegionJoined"))
        val ob = sc.getObjective("RegionJoined")!!
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
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
                else if(isTargetInTeam && targetRegion.contains(region) && ob.getScore(target.uniqueId.toString()).score == 0){

                    val regionPos = CustomSMPPlugin.regionsPos[region]!!

                    val x1 = min(regionPos[0], regionPos[2])
                    val x2 = max(regionPos[0], regionPos[2])
                    val z1 = min(regionPos[1], regionPos[3])
                    val z2 = max(regionPos[1], regionPos[3])

                    if (destination.x in x1..x2 && destination.z in z1..z2) {
                        target.bedSpawnLocation = null
                        ob.getScore(target.uniqueId.toString()).score = 1
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
        val targetPosition = target.location
        val destination = event.to
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
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
            }
        }
    }

    @EventHandler
    fun onVehicleMove(event: VehicleMoveEvent) {
        if (!event.vehicle.passengers.isNullOrEmpty()) {
            val target = event.vehicle.passengers[0]
            val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
            val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
            val targetPosition = target.location
            val destination = event.to
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
                    if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (destination.x in x1..x2 && destination.z in z1..z2) {
                            target.eject()
                            target.teleport(event.from)
                            event.vehicle.teleport(event.from)
                        }
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

        CustomSMPPlugin.regionsName.forEach { region ->
            run {
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

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val entity = event.rightClicked
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
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

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val target = event.damager
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val entity = event.entity

        if (target is Player || (event.damager as Arrow).shooter is  Player || (event.damager as Trident).shooter is Player) {
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
                    if (targetRegion == null || !isTargetInTeam!! || !targetRegion.contains(region)) {
                        val regionPos = CustomSMPPlugin.regionsPos[region]!!

                        val x1 = min(regionPos[0], regionPos[2])
                        val x2 = max(regionPos[0], regionPos[2])
                        val z1 = min(regionPos[1], regionPos[3])
                        val z2 = max(regionPos[1], regionPos[3])

                        if (entity.location.x in x1..x2 && entity.location.z in z1..z2) {
                            event.isCancelled = true
                        }
                        if (target.location.x in x1..x2 && target.location.z in z1..z2) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
        else event.isCancelled = false
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val block = event.block
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
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

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val block = event.block
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
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

    @EventHandler
    fun onBlockIgnite(event: BlockIgniteEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target?.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target?.uniqueId]]
        val block = event.block

        if (target != null) {
            CustomSMPPlugin.regionsName.forEach { region ->
                run {
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
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
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

    @EventHandler
    fun onPlayerBucketEmpty(event: PlayerBucketEmptyEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val block = event.blockClicked
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
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

    @EventHandler
    fun onPlayerAttemptPickupItem(event: PlayerAttemptPickupItemEvent) {
        val target = event.player
        val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
        val targetRegion = CustomSMPPlugin.teamsRegion[CustomSMPPlugin.playerTeam[target.uniqueId]]
        val item = event.item
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
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

    @EventHandler
    fun onBlockPistonExtend(event: BlockPistonExtendEvent) {
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
                val regionPos = CustomSMPPlugin.regionsPos[region]!!

                val x1 = min(regionPos[0], regionPos[2])
                val x2 = max(regionPos[0], regionPos[2])
                val z1 = min(regionPos[1], regionPos[3])
                val z2 = max(regionPos[1], regionPos[3])

                event.blocks.forEach { block ->
                    if (block.location.x in x1..x2 && block.location.z in z1..z2) {
                        event.isCancelled = true
                    }
                }
            }
        }
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
                val regionPos = CustomSMPPlugin.regionsPos[region]!!

                val x1 = min(regionPos[0], regionPos[2])
                val x2 = max(regionPos[0], regionPos[2])
                val z1 = min(regionPos[1], regionPos[3])
                val z2 = max(regionPos[1], regionPos[3])

                event.blockList().removeIf { it.location.x in x1..x2 && it.location.z in z1..z2 }
            }
        }
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent){
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
                val regionPos = CustomSMPPlugin.regionsPos[region]!!

                val x1 = min(regionPos[0], regionPos[2])
                val x2 = max(regionPos[0], regionPos[2])
                val z1 = min(regionPos[1], regionPos[3])
                val z2 = max(regionPos[1], regionPos[3])

                event.blockList().removeIf { it.location.x in x1..x2 && it.location.z in z1..z2 }
            }
        }
    }
}
