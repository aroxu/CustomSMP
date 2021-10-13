package me.aroxu.customsmp.events

import me.aroxu.customsmp.CustomSMPPlugin
import me.aroxu.customsmp.CustomSMPPlugin.Companion.isInWar
import me.aroxu.customsmp.CustomSMPPlugin.Companion.plugin
import me.aroxu.customsmp.CustomSMPPlugin.Companion.survivalLife
import me.aroxu.customsmp.CustomSMPPlugin.Companion.warLife
import me.aroxu.customsmp.database.DataManager
import me.aroxu.customsmp.utils.BetterMaxHealth
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.Sound.sound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent


class KillEvent : Listener {
    private var lastKilledPlayer: Player? = null

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val target = event.entity

        val sm = Bukkit.getServer().scoreboardManager
        val sc = sm.mainScoreboard
        if (sc.getObjective("didWar") == null)
            sc.registerNewObjective("didWar", "dummy", text("didWar"))
        val ob = sc.getObjective("didWar")!!

        if (event.entity.killer is Player) {
            val killer = target.killer!!
            target.playerTime
            BetterMaxHealth.setMaxHealth(killer, BetterMaxHealth.getMaxHealth(killer) + 1)
        }
        if (isInWar[target.uniqueId]!!) {
            warLife[target.uniqueId] = warLife[target.uniqueId]!!.minus(1)
            DataManager.setWarLifeWithUuid(target.uniqueId, warLife[target.uniqueId]!!)
            if (warLife[target.uniqueId]!! <= 0) {
                target.gameMode = GameMode.SPECTATOR
                plugin.server.onlinePlayers.forEach {
                    run {
                        if (it.uniqueId == target.uniqueId) {
                            it.sendMessage(
                                text("당신의 전쟁 목숨이 전부 소진되어 관전자가 되었습니다.")
                                    .color(TextColor.color(0xFF0000)).decorate(TextDecoration.BOLD)
                            )

                            it.location.block.type = Material.CHEST
                            (it.location.block.state as Chest).inventory.contents = it.enderChest.contents
                            (it.location.block.state as Chest).customName = "${it.name}의 엔드상자"
                            it.enderChest.clear()
                        } else {
                            it.playSound(
                                sound(
                                    Key.key("block.note_block.pling"),
                                    Sound.Source.AMBIENT,
                                    10.0f,
                                    2.0f
                                )
                            )
                            it.sendMessage(
                                text("플레이어 ${target.name}님이 전쟁 목숨을 전부 소진하였습니다.")
                                    .color(TextColor.color(0xFF0000)).decorate(TextDecoration.BOLD)
                            )
                        }
                    }
                }
            }
            if (CustomSMPPlugin.teamsMember[CustomSMPPlugin.playerTeam[target.uniqueId]]!!.all { Bukkit.getPlayer(it)?.gameMode == GameMode.SPECTATOR }) {
                CustomSMPPlugin.warTeams.filter { team -> team.first == CustomSMPPlugin.playerTeam[target.uniqueId] || team.second == CustomSMPPlugin.playerTeam[target.uniqueId] }
                    .forEach { targetTeams ->
                        run {
                            CustomSMPPlugin.warTeams = CustomSMPPlugin.warTeams.minus(
                                CustomSMPPlugin.warTeams[CustomSMPPlugin.warTeams.indexOf(targetTeams)]
                            )
                            CustomSMPPlugin.teamsMember[targetTeams.first]!!.forEach { targetPlayer ->
                                DataManager.setIsInWarWithUuid(targetPlayer, false)
                            }
                            CustomSMPPlugin.teamsMember[targetTeams.second]!!.forEach { targetPlayer ->
                                DataManager.setIsInWarWithUuid(targetPlayer, false)
                            }

                            if (targetTeams.first == CustomSMPPlugin.playerTeam[target.uniqueId]) {
                                if (ob.getScore(targetTeams.first.toString()).score == 1) {
                                    if (!CustomSMPPlugin.teamsRegion[targetTeams.first].isNullOrEmpty()) {
                                        CustomSMPPlugin.teamsRegion[targetTeams.first]!!.forEach { region ->
                                            DataManager.removeRegionWithName(region)
                                        }
                                    }
                                }
                                else{
                                    if(sc.getObjective("WarCooldown") == null) sc.registerNewObjective("WarCooldown","dummy",text("WarCooldown"))
                                    val cob = sc.getObjective("WarCooldown")!!
                                    cob.getScore(targetTeams.first.toString()).score = 1
                                    Bukkit.getServer().scheduler.runTaskLater(plugin,
                                        Runnable{
                                                cob.getScore(targetTeams.first.toString()).score = 0
                                    }, 2000L)
                                }
                            }

                            if (targetTeams.second == CustomSMPPlugin.playerTeam[target.uniqueId]){
                                if (ob.getScore(targetTeams.second.toString()).score == 1) {
                                    if (!CustomSMPPlugin.teamsRegion[targetTeams.second].isNullOrEmpty()) {
                                        CustomSMPPlugin.teamsRegion[targetTeams.second]!!.forEach { region ->
                                            DataManager.removeRegionWithName(region)
                                        }
                                    }
                                }
                                else{
                                    if(sc.getObjective("WarCooldown") == null) sc.registerNewObjective("WarCooldown","dummy",text("WarCooldown"))
                                    val cob = sc.getObjective("WarCooldown")!!
                                    cob.getScore(targetTeams.second.toString()).score = 1
                                    Bukkit.getServer().scheduler.runTaskLater(plugin,
                                        Runnable{
                                            cob.getScore(targetTeams.second.toString()).score = 0
                                        }, 2000L)
                                }
                            }

                                ob.getScore(targetTeams.first.toString()).score = 1
                                ob.getScore(targetTeams.second.toString()).score = 1

                        }
                        CustomSMPPlugin.teamsMember[CustomSMPPlugin.playerTeam[target.uniqueId]]!!.forEach { targetPlayer ->
                            Bukkit.getPlayer(targetPlayer)
                                ?.teleport(Location(Bukkit.getWorld("world"), 0.0, 120.0, 0.0))
                            Bukkit.getPlayer(targetPlayer)?.gameMode = GameMode.SURVIVAL
                        }
                        lastKilledPlayer = target
                    }
            } else {
                survivalLife[target.uniqueId] = survivalLife[target.uniqueId]!!.minus(1)
                DataManager.setSurvivalLifeWithUuid(target.uniqueId, survivalLife[target.uniqueId]!!)
                if (survivalLife[target.uniqueId]!! <= 0) {
                    target.teleport(Location(Bukkit.getWorld("world"), 0.0, 120.0, 0.0))
                    plugin.server.onlinePlayers.forEach {
                        run {
                            if (it.uniqueId == target.uniqueId) {
                                it.sendMessage(
                                    text("당신의 생존 목숨이 전부 소진되어 노예 상태가 되었습니다.")
                                        .color(TextColor.color(0xFF0000)).decorate(TextDecoration.BOLD)
                                )
                            } else {
                                it.playSound(
                                    sound(
                                        Key.key("block.note_block.pling"),
                                        Sound.Source.AMBIENT,
                                        10.0f,
                                        2.0f
                                    )
                                )
                                it.sendMessage(
                                    text("플레이어 ${target.name}님이 생존 목숨이 전부 소진되어 노예 상태가 되었습니다.")
                                        .color(TextColor.color(0xFF0000)).decorate(TextDecoration.BOLD)
                                )
                            }
                        }
                    }
                }
            }
        }

        @EventHandler
        fun onPlayerRespawn(event: PlayerRespawnEvent) {
            val target = event.player

            if (survivalLife[target.uniqueId]!! == 0) {
                event.respawnLocation = Location(Bukkit.getWorld("world"), 0.0, 120.0, 0.0)
            } else {
                if (target.bedSpawnLocation == null) {
                    val isTargetInTeam = CustomSMPPlugin.isInTeam[target.uniqueId]
                    if (isTargetInTeam!!) {
                        val targetTeam = CustomSMPPlugin.playerTeam[target.uniqueId]
                        val targetTeamName = CustomSMPPlugin.teamsName[targetTeam]
                        val targetRegion = CustomSMPPlugin.regionsPos[targetTeamName]

                        if (targetRegion != null) {
                            val y = target.world.getHighestBlockYAt(targetRegion[0].toInt(), targetRegion[1].toInt())
                                .toDouble()
                            event.respawnLocation =
                                Location(Bukkit.getWorld("world"), targetRegion[0], y, targetRegion[1])
                        }
                    }
                }
            }
            if (lastKilledPlayer != null && target == lastKilledPlayer) {
                event.respawnLocation = (Location(Bukkit.getWorld("world"), 0.0, 120.0, 0.0))
                lastKilledPlayer = null
            }
        }
    }
}
