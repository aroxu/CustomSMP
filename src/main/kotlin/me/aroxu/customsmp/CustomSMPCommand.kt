package me.aroxu.customsmp

import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.LiteralNode
import me.aroxu.customsmp.CustomSMPPlugin.Companion.plugin
import me.aroxu.customsmp.database.DataManager
import me.aroxu.customsmp.utils.BetterMaxHealth
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.Sound.sound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import java.util.*

/**
 * @author aroxu
 */

object CustomSMPCommand {
    fun register(builder: LiteralNode) {
        builder.apply {
            then("about") { executes { sender.sendMessage("CustomSMP by aroxu.") } }
            then("lifeset") {
                requires { isOp }
                then("survival") {
                    then("player" to player()) {
                        then("life" to int(0, Int.MAX_VALUE)) {
                            executes {
                                val player: Player by it
                                val life: Int by it
                                DataManager.setSurvivalLifeWithUuid(player.uniqueId, life)
                                sender.sendMessage("플레이어 ${player.name}의 생존 목숨을 ${life}(으)로 설정하였습니다.")
                            }
                        }
                    }
                }
                then("war") {
                    then("player" to player()) {
                        then("life" to int(0, Int.MAX_VALUE)) {
                            executes {
                                val player: Player by it
                                val life: Int by it
                                DataManager.setWarLifeWithUuid(player.uniqueId, life)
                                sender.sendMessage("플레이어 ${player.name}의 전쟁 목숨을 ${life}(으)로 설정하였습니다.")
                            }
                        }
                    }
                }
            }
            then("maxhealth") {
                requires { isOp }
                then("player" to player()) {
                    then("maxHealth" to double(0.0, Double.MAX_VALUE)) {
                        executes {
                            val player: Player by it
                            val maxHealth: Int by it
                            BetterMaxHealth.setMaxHealth(player, maxHealth.toDouble())
                            sender.sendMessage("플레이어 ${player.name}의 최대 체력을 ${maxHealth}(으)로 설정하였습니다.")
                        }
                    }
                }
            }
            then("warTestMode") {
                requires { player.uniqueId == UUID.fromString("762dea11-9c45-4b18-95fc-a86aab3b39ee") }
                then("enable") {
                    then("player" to player()) {
                        executes {
                            val player: Player by it
                            CustomSMPPlugin.isInWar[player.uniqueId] = true
                            DataManager.setIsInWarWithUuid(player.uniqueId, true)
                            sender.sendMessage("플레이어 ${player.name}에게 전쟁 테스트 모드를 활성화 하였습니다.")
                        }
                    }
                }
                then("disable") {
                    then("player" to player()) {
                        executes {
                            val player: Player by it
                            CustomSMPPlugin.isInWar[player.uniqueId] = false
                            DataManager.setIsInWarWithUuid(player.uniqueId, false)
                            sender.sendMessage("플레이어 ${player.name}에게 전쟁 테스트 모드를 비활성화 하였습니다.")
                        }
                    }
                }
            }
            then("status") {
                requires { isOp }
                then("player" to player()) {
                    executes {
                        val player: Player by it
                        val targetPlayerSurvivalLife = CustomSMPPlugin.survivalLife[player.uniqueId]
                        val targetPlayerWarLife = CustomSMPPlugin.warLife[player.uniqueId]
                        val isTargetPlayerInWar = CustomSMPPlugin.isInWar[player.uniqueId]
                        val isTargetPlayerInWarStatusText: String = if (isTargetPlayerInWar!!) {
                            "예"
                        } else {
                            "아니요"
                        }
                        sender.sendMessage(
                            "플레이어 ${player.name}의 상태는 다음과 같습니다:\n최대 체력: ${
                                BetterMaxHealth.getMaxHealth(
                                    player
                                ).toInt()
                            }\n남은 생존 목숨: $targetPlayerSurvivalLife\n남은 전쟁 목숨: $targetPlayerWarLife\n전쟁 진행중: $isTargetPlayerInWarStatusText"
                        )
                    }
                }
            }
            then("teamset") {
                requires { isOp }
                then("teamName" to string(StringType.QUOTABLE_PHRASE)) {
                    then("players" to players()) {
                        executes { arguments ->
                            val teamName: String by arguments
                            val players: Collection<Player> by arguments
//                            if (players.size < 3) {
//                                sender.playSound(
//                                    sound(
//                                        Key.key("block.note_block.bass"),
//                                        Sound.Source.AMBIENT,
//                                        10.0f,
//                                        0.1f
//                                    )
//                                )
//                                sender.sendMessage(text("팀 생성 거절됨: 팀은 최소 2인 부터 구성 가능합니다.")
//                                    .decorate(TextDecoration.BOLD).color(TextColor.color(0xFFA500)))
//                                return@executes
//                            }
//                            if (players.size > 5) {
//                                sender.playSound(
//                                    sound(
//                                        Key.key("block.note_block.bass"),
//                                        Sound.Source.AMBIENT,
//                                        10.0f,
//                                        0.1f
//                                    )
//                                )
//                                sender.sendMessage(text("팀 생성 거절됨: 팀은 최대 5인 까지 구성 가능합니다.")
//                                    .decorate(TextDecoration.BOLD).color(TextColor.color(0xFFA500)))
//                                return@executes
//                            }
//                            val playersToText = players.joinToString(separator = ", ") { it.name }
//                            val team = plugin.server.scoreboardManager.mainScoreboard.registerNewTeam(
//                                UUID.randomUUID().toString().replace("-", "").substring(0, 15)
//                            )
//                            team.displayName(text(teamName))
//                            team.setAllowFriendlyFire(false)
//                            players.forEach { target ->
//                                run {
//                                    plugin.server.scoreboardManager.mainScoreboard.getTeam(teamName)
//                                        ?.addEntry(target.uniqueId.toString())
//                                    target.sendMessage("당신은 [${teamName}] 팀에 추가되셨습니다.")
//                                }
//                            }
//                            sender.sendMessage(text("팀 [${teamName}]을(를) 만들고 다음 플레이어(들)를(을) 해당 팀에 추가하였습니다:\n$playersToText"))
                        }
                    }
                }
            }
            then("teamadd") {
                requires { isOp }
                then("teamName" to string(StringType.QUOTABLE_PHRASE)) {
                    then("players" to players()) {
                        executes { arguments ->
                            val teamName: String by arguments
                            val players: Collection<Player> by arguments
                            val playersToText = players.joinToString(separator = ", ") { it.name }
//                            plugin.server.scoreboardManager.mainScoreboard.teams.forEach { team ->
//                                run {
//                                    if (team.displayName() == text(teamName)) {
//                                        if (team.entries.size + players.size > 5) {
//                                            sender.playSound(
//                                                sound(
//                                                    Key.key("block.note_block.bass"),
//                                                    Sound.Source.AMBIENT,
//                                                    10.0f,
//                                                    0.1f
//                                                )
//                                            )
//                                            return@executes sender.sendMessage(
//                                                text("팀원 추가 거절됨: 팀은 최대 5인 까지 구성 가능합니다.")
//                                                    .decorate(TextDecoration.BOLD).color(TextColor.color(0xFFA500))
//                                            )
//                                        }
//                                        players.forEach { target ->
//                                            run {
//                                                if (team.entries.contains(target.uniqueId.toString())) {
//                                                    sender.playSound(
//                                                        sound(
//                                                            Key.key("block.note_block.bass"),
//                                                            Sound.Source.AMBIENT,
//                                                            10.0f,
//                                                            0.1f
//                                                        )
//                                                    )
//                                                    return@executes sender.sendMessage(
//                                                        text("팀원 추가 거절됨: ${target.name}은 이미 해당 팀의 팀원 입니다.")
//                                                            .decorate(TextDecoration.BOLD)
//                                                            .color(TextColor.color(0xFFA500))
//                                                    )
//                                                }
//                                            }
//                                        }
//                                        players.forEach { target ->
//                                            run {
//                                                if (target.scoreboard.teams.size < 1) {
//                                                    sender.playSound(
//                                                        sound(
//                                                            Key.key("block.note_block.bass"),
//                                                            Sound.Source.AMBIENT,
//                                                            10.0f,
//                                                            0.1f
//                                                        )
//                                                    )
//                                                    return@executes sender.sendMessage(
//                                                        text("팀원 추가 거절됨: ${target.name}은 이미 다른 팀의 팀원 입니다.")
//                                                            .decorate(TextDecoration.BOLD)
//                                                            .color(TextColor.color(0xFFA500))
//                                                    )
//                                                }
//                                            }
//                                        }
//                                        players.forEach { target ->
//                                            run {
//                                                team.addEntry(target.uniqueId.toString())
//                                                sender.playSound(
//                                                    sound(
//                                                        Key.key("block.anvil.use"),
//                                                        Sound.Source.AMBIENT,
//                                                        10.0f,
//                                                        2.0f
//                                                    )
//                                                )
//                                                target.sendMessage("당신은 [${teamName}] 팀에 추가되셨습니다.")
//                                                return@executes sender.sendMessage(text("팀 [${teamName}]에 다음 플레이어(들)를(을) 추가하였습니다:\n$playersToText"))
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            sender.playSound(
//                                sound(
//                                    Key.key("block.note_block.bass"),
//                                    Sound.Source.AMBIENT,
//                                    10.0f,
//                                    0.1f
//                                )
//                            )
//                            sender.sendMessage(
//                                text("팀원 추가 거절됨: 해당 팀을 찾을 수 없습니다.")
//                                    .decorate(TextDecoration.BOLD).color(TextColor.color(0xFFA500))
//                            )
                        }
                    }
                }
            }
            then("teamrm") {
                requires { isOp }
                executes {

                }
            }
            then("teamdel") {
                requires { isOp }
                then("teamName" to string(StringType.QUOTABLE_PHRASE)) {
                    executes {
                        val teamName: String by it
//                        plugin.server.scoreboardManager.mainScoreboard.teams.forEach { team ->
//                            run {
//                                if (team.displayName() == text(teamName)) {
//                                    team.unregister()
//                                    sender.playSound(
//                                        sound(
//                                            Key.key("block.anvil.destroy"),
//                                            Sound.Source.AMBIENT,
//                                            10.0f,
//                                            1.0f
//                                        )
//                                    )
//                                    return@executes sender.sendMessage(text("팀 [${teamName}]을 제거하였습니다."))
//                                }
//                            }
//                        }
//                        sender.playSound(
//                            sound(
//                                Key.key("block.note_block.bass"),
//                                Sound.Source.AMBIENT,
//                                10.0f,
//                                0.1f
//                            )
//                        )
//                        sender.sendMessage(
//                            text("팀 삭제 거절됨: 해당 팀을 찾을 수 없습니다.")
//                                .decorate(TextDecoration.BOLD).color(TextColor.color(0xFFA500))
//                        )
                    }
                }
            }
        }
    }
}
