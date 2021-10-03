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
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

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
            then("team") {
                then("create") {
                    requires { isOp }
                    then("teamName" to string(StringType.QUOTABLE_PHRASE)) {
                        executes {
                            val teamName: String by it
                            if (teamName.length < 2 || teamName.length > 8) {
                                return@executes sender.sendMessage(text("팀 이름은 최소 2글자 최대 8글자 입니다."))
                            }
                            if (CustomSMPPlugin.teamsName.values.contains(teamName.trim())) {
                                return@executes sender.sendMessage(text("해당 팀 이름은 이미 사용중인 이름입니다."))
                            }
                            val teamUuid = UUID.randomUUID()
                            DataManager.addToTeamUuids(teamUuid)
                            DataManager.setTeamNameWithUuid(teamUuid, teamName)
                            sender.sendMessage(text("팀 [${teamName}]이 생성되었습니다. '/smp team addPlayer $teamName nickname' 명령어를 이용해서 팀원을 추가하세요."))
                        }
                    }
                }
                then("addPlayer") {
                    requires { isOp }
                    then("teamName" to string(StringType.QUOTABLE_PHRASE)) {
                        then("player" to player()) {
                            executes { arguments ->
                                val teamName: String by arguments
                                val player: Player by arguments
                                println(CustomSMPPlugin.isInTeam[player.uniqueId]!!)
                                if (!CustomSMPPlugin.teamsName.values.contains(teamName)) {
                                    return@executes sender.sendMessage(text("일치하는 팀 이름이 없습니다."))
                                }
                                val teamUuid = CustomSMPPlugin.teamsName.filterValues { it == teamName }.keys.first()
                                if (CustomSMPPlugin.isInTeam[player.uniqueId]!!) {
                                    return@executes sender.sendMessage(text("플레이어 ${player.name}님은 이미 팀에 할당되어 있습니다."))
                                } else {
                                    if (CustomSMPPlugin.teamsMember[teamUuid] == null || CustomSMPPlugin.teamsMember[teamUuid]!!.isEmpty()) {
                                        DataManager.setTeamMembersWithUuid(teamUuid, listOf(player.uniqueId))
                                    } else {
                                        DataManager.setTeamMembersWithUuid(
                                            teamUuid,
                                            CustomSMPPlugin.teamsMember[teamUuid]!!.plus(player.uniqueId)
                                        )
                                    }
                                    DataManager.setIsInTeamWithUuid(player.uniqueId, true)
                                    DataManager.setPlayerTeamWithUuid(player.uniqueId, teamUuid)
                                    sender.sendMessage(text("플레이어 ${player.name}님이 [${teamName}] 팀에 할당되었습니다."))
                                    player.playSound(
                                        sound(
                                            Key.key("block.note_block.pling"),
                                            Sound.Source.AMBIENT,
                                            10.0f,
                                            2.0f
                                        )
                                    )
                                    player.sendMessage(text("당신은 [${teamName}] 팀에 할당되었습니다."))
                                }
                            }
                        }
                    }
                }
                then("removePlayer") {
                    requires { isOp }
                    then("teamName" to string(StringType.QUOTABLE_PHRASE)) {
                        then("player" to player()) {
                            executes { arguments ->
                                val teamName: String by arguments
                                val player: Player by arguments
                                if (!CustomSMPPlugin.teamsName.values.contains(teamName)) {
                                    return@executes sender.sendMessage(text("일치하는 팀 이름이 없습니다."))
                                }
                                val teamUuid = CustomSMPPlugin.teamsName.filterValues { it == teamName }.keys.first()
                                if (
                                    CustomSMPPlugin.teamsMember[teamUuid] == null
                                    || CustomSMPPlugin.teamsMember[teamUuid]!!.isEmpty()
                                ) {
                                    return@executes sender.sendMessage(text("해당 팀은 구성 인원이 없습니다."))
                                } else if (!CustomSMPPlugin.teamsMember[teamUuid]!!.contains(player.uniqueId)
                                ) {
                                    return@executes sender.sendMessage(text("플레이어 ${player.name}님은 [${teamName}] 팀에 없습니다."))
                                } else {
                                    DataManager.setTeamMembersWithUuid(
                                        teamUuid,
                                        CustomSMPPlugin.teamsMember[teamUuid]!!.minus(player.uniqueId)
                                    )
                                    DataManager.setIsInTeamWithUuid(player.uniqueId, false)

                                    sender.sendMessage(text("플레이어 ${player.name}님이 [${teamName}] 팀에서 제거되었습니다."))
                                    player.playSound(
                                        sound(
                                            Key.key("block.note_block.pling"),
                                            Sound.Source.AMBIENT,
                                            10.0f,
                                            2.0f
                                        )
                                    )
                                    player.sendMessage(text("당신은 [${teamName}] 팀에서 제거되었습니다."))
                                }
                            }
                        }
                    }
                }
                then("delete") {
                    requires { isOp }
                    then("teamName" to string(StringType.QUOTABLE_PHRASE)) {
                        executes {
                            val teamName: String by it
                            if (!CustomSMPPlugin.teamsName.values.contains(teamName)) {
                                return@executes sender.sendMessage(text("일치하는 팀 이름이 없습니다."))
                            }
                            val teamUuid =
                                CustomSMPPlugin.teamsName.filterValues { team -> team == teamName }.keys.first()
                            CustomSMPPlugin.teamsMember[teamUuid]!!.forEach { member ->
                                DataManager.setIsInTeamWithUuid(
                                    member,
                                    false
                                )
                            }
                            DataManager.removeTeamWithUuid(teamUuid)
                            sender.playSound(
                                sound(
                                    Key.key("block.note_block.pling"),
                                    Sound.Source.AMBIENT,
                                    10.0f,
                                    2.0f
                                )
                            )
                            sender.sendMessage(text("[${teamName}] 팀이 제거되었습니다."))
                        }
                    }
                }
                then("list") {
                    requires { isOp }
                    executes {
                        var teams = listOf<HashMap<String, String>>()
                        CustomSMPPlugin.teamsUuid.forEach { team ->
                            run {
                                val tempMap = HashMap<String, String>()
                                var tempTeamMembers: String = ""
                                if (CustomSMPPlugin.teamsName[team] == null) {
                                    return@forEach
                                }
                                tempMap["name"] = CustomSMPPlugin.teamsName[team]!!
                                if (CustomSMPPlugin.teamsMember[team] == null || CustomSMPPlugin.teamsMember[team]!!.isEmpty()) {
                                    tempTeamMembers = "없음"
                                } else {
                                    var playerList: List<String> = emptyList()
                                    CustomSMPPlugin.teamsMember[team]!!.forEach { player ->
                                        run {
                                            playerList = playerList.plus(plugin.server.getPlayer(player)!!.name)
                                        }
                                    }
                                    tempTeamMembers = playerList.joinToString(", ")
                                }
                                tempMap["members"] = tempTeamMembers
                                teams = teams.plus(tempMap)
                            }
                        }
                        if (teams.isEmpty()) {
                            return@executes sender.sendMessage(text("존재하는 팀이 없습니다."))
                        }
                        var resultText = text("")
                        teams.forEach { team ->
                            run {
                                resultText = resultText.append(
                                    text("팀 이름: ").append(
                                        text("${team["name"]!!}\n").decorate(TextDecoration.BOLD)
                                    )
                                )
                                resultText = resultText.append(
                                    text("팀 멤버: ").append(
                                        text("${team["members"]!!}\n").decorate(TextDecoration.BOLD)
                                    )
                                )
                                resultText = resultText.append(text("\n"))
                            }
                        }
                        resultText =
                            resultText.append(
                                text("총 ").append(
                                    text("${teams.size}개").decorate(TextDecoration.BOLD)
                                ).append(
                                    text("의 팀이 있습니다.")
                                )
                            )
                        sender.sendMessage(resultText)
                    }
                }
            }
        }
    }
}
