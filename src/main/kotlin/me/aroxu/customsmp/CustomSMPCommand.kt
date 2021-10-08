package me.aroxu.customsmp

import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.LiteralNode
import io.github.monun.kommand.wrapper.Position2D
import me.aroxu.customsmp.CustomSMPPlugin.Companion.plugin
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
            then("lifeSet") {
                requires { isOp || isConsole }
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
            then("maxHealth") {
                requires { isOp || isConsole }
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
                requires { isOp || isConsole }
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
                        val isTargetInTeam = CustomSMPPlugin.isInTeam[player.uniqueId]
                        val isTargetPlayerInTeamStatusText: String = if (isTargetInTeam!!) {
                            "예"
                        } else {
                            "아니요"
                        }
                        var teamNameText = "\n"
                        if (isTargetInTeam) {
                            teamNameText =
                                "\n팀: ${CustomSMPPlugin.teamsName[CustomSMPPlugin.playerTeam[player.uniqueId]!!]!!}"
                        }
                        sender.sendMessage(
                            "플레이어 ${player.name}의 상태는 다음과 같습니다:\n최대 체력: ${
                                BetterMaxHealth.getMaxHealth(
                                    player
                                ).toInt()
                            }\n남은 생존 목숨: $targetPlayerSurvivalLife\n남은 전쟁 목숨: $targetPlayerWarLife\n전쟁 진행중: $isTargetPlayerInWarStatusText\n팀 소속 여부: $isTargetPlayerInTeamStatusText${teamNameText}"
                        )
                    }
                }
            }
            then("team") {
                then("addPlayer") {
                    requires { isOp || isConsole }
                    then("teamName" to string(StringType.QUOTABLE_PHRASE)) {
                        then("player" to player()) {
                            executes { arguments ->
                                val teamName: String by arguments
                                val player: Player by arguments
                                if (!CustomSMPPlugin.teamsName.values.contains(teamName)) {
                                    return@executes sender.sendMessage(text("일치하는 팀 이름이 없습니다."))
                                }
                                val teamUuid = CustomSMPPlugin.teamsName.filterValues { it == teamName }.keys.first()
                                if (CustomSMPPlugin.isInTeam[player.uniqueId]!!) {
                                    return@executes sender.sendMessage(text("플레이어 ${player.name}님은 이미 팀에 할당되어 있습니다."))
                                } else if (!(CustomSMPPlugin.teamsMember[teamUuid] == null
                                            || CustomSMPPlugin.teamsMember[teamUuid]!!.isEmpty())
                                    && CustomSMPPlugin.teamsMember[teamUuid]!!.size >= 5
                                ) {
                                    return@executes sender.sendMessage(text("[${teamName}] 탐은 이미 최대 인원수에 도달하였습니다."))
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
                    requires { isOp || isConsole }
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
                then("create") {
                    requires { isOp || isConsole }
                    then("teamName" to string(StringType.QUOTABLE_PHRASE)) {
                        executes {
                            val teamName: String by it
                            if (teamName.length < 2 || teamName.length > 8) {
                                return@executes sender.sendMessage(text("팀 이름은 최소 2글자 최대 8글자 입니다."))
                            }
                            if (teamName.contains("|")) {
                                return@executes sender.sendMessage(text("문자 \"|\"는 팀 이름에 포함될 수 없습니다."))
                            }
                            if (CustomSMPPlugin.teamsName.values.contains(teamName)) {
                                return@executes sender.sendMessage(text("해당 팀 이름은 이미 사용중인 이름입니다."))
                            }
                            val teamUuid = UUID.randomUUID()
                            DataManager.addToTeamUuids(teamUuid)
                            DataManager.setTeamNameWithUuid(teamUuid, teamName)
                            sender.sendMessage(text("팀 [${teamName}]이 생성되었습니다. '/smp team addPlayer \"$teamName\" nickname' 명령어를 이용해서 팀원을 추가하세요."))
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
                            if (!(CustomSMPPlugin.teamsMember[teamUuid] == null) || CustomSMPPlugin.teamsMember[teamUuid]!!.isNotEmpty()) {
                                CustomSMPPlugin.teamsMember[teamUuid]!!.forEach { member ->
                                    DataManager.setIsInTeamWithUuid(
                                        member,
                                        false
                                    )
                                }
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
                    executes {
                        var teams = listOf<HashMap<String, String>>()
                        CustomSMPPlugin.teamsUuid.forEach { team ->
                            run {
                                val tempMap = HashMap<String, String>()
                                val tempTeamMembers: String
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
                                            val playerName: String = if (plugin.server.getPlayer(player) == null) {
                                                plugin.server.getOfflinePlayer(player).name!!
                                            } else {
                                                plugin.server.getPlayer(player)!!.name
                                            }
                                            playerList = playerList.plus(playerName)
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
            then("region") {
                requires { isOp || isConsole }
                then("list") {
                    executes {
                        if (CustomSMPPlugin.teamsName.size == 0) {
                            return@executes sender.sendMessage("존재하는 영역이 없습니다.")
                        }
                        var regionsTextComponent = text("")
                        CustomSMPPlugin.regionsName.forEach { regionName ->
                            run {
                                regionsTextComponent = regionsTextComponent.append(
                                    text("영역 이름: ").append(
                                        text("${regionName}\n").decorate(TextDecoration.BOLD)
                                    )
                                )
                                var inRegionTeamNames = ""
                                CustomSMPPlugin.teamsUuid.forEach TeamsUuidForEach@{ team ->
                                    run {
                                        if (CustomSMPPlugin.teamsRegion[team] == null) {
                                            return@TeamsUuidForEach
                                        }
                                        if (CustomSMPPlugin.teamsRegion[team]!!.isEmpty()) {
                                            inRegionTeamNames = "없음"
                                        } else {
                                            var teamsList: List<String> = emptyList()
                                            teamsList = teamsList.plus(CustomSMPPlugin.teamsName[team]!!)
                                            inRegionTeamNames = teamsList.joinToString(", ")
                                        }
                                    }
                                }
                                regionsTextComponent = regionsTextComponent.append(
                                    text("등록된 팀 목록: ").append(
                                        text("${inRegionTeamNames}\n").decorate(TextDecoration.BOLD)
                                    )
                                )
                                // posData[0] = Start Pos's X
                                // posData[1] = Start Pos's Z
                                // posData[2] = End Pos's X
                                // posData[3] = End Pos's Z
                                val posData = CustomSMPPlugin.regionsPos[regionName]!!
                                regionsTextComponent = regionsTextComponent.append(
                                    text(
                                        "시작 구역 X: ${
                                            posData[0].toInt()
                                        }\n시작 구역 Z: ${
                                            posData[1].toInt()
                                        }\n종료 구역 X: ${
                                            posData[2].toInt()
                                        }\n종료 구역 Z: ${
                                            posData[3].toInt()
                                        }\n\n"
                                    ).decorate(TextDecoration.BOLD)
                                )
                            }
                        }
                        regionsTextComponent = regionsTextComponent.append(
                            text("총 ").append(text("${CustomSMPPlugin.regionsName.size}개")).append(text("의 영역이 있습니다."))
                        )
                        sender.sendMessage(regionsTextComponent)
                    }
                }
                then("create") {
                    then("regionName" to string(StringType.QUOTABLE_PHRASE)) {
                        then("pos1" to position2D()) {
                            then("pos2" to position2D()) {
                                executes {
                                    val regionName: String by it
                                    val pos1: Position2D by it
                                    val pos2: Position2D by it
                                    if (regionName.length < 2 || regionName.length > 16) {
                                        return@executes sender.sendMessage(text("영역 이름은 최소 2글자 최대 16글자 입니다."))
                                    }
                                    if (regionName.contains("|")) {
                                        return@executes sender.sendMessage(text("문자 \"|\"는 팀 이름에 포함될 수 없습니다."))
                                    }
                                    if (CustomSMPPlugin.regionsName.contains(regionName)) {
                                        return@executes sender.sendMessage(text("해당 영역 이름은 이미 사용중인 이름입니다."))
                                    }
                                    DataManager.addToRegionNames(regionName)
                                    DataManager.setRegionPosDataWithName(
                                        regionName,
                                        listOf(pos1.x, pos1.z, pos2.x, pos2.z)
                                    )
                                    sender.sendMessage(
                                        text(
                                            "시작 좌표를 X: ${
                                                pos1.x.toInt()
                                            } Z: ${
                                                pos1.z.toInt()
                                            }로 하고 종료 좌표를 X: ${
                                                pos2.x.toInt()
                                            } Z: ${
                                                pos2.z.toInt()
                                            }로 하는 영역 \"${regionName}\"을(를) 생성하였습니다."
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                then("delete") {
                    then("regionName" to string(StringType.QUOTABLE_PHRASE)) {
                        executes {
                            val regionName: String by it
                            if (!CustomSMPPlugin.regionsName.contains(regionName)) {
                                return@executes sender.sendMessage(text("일치하는 영역 이름이 없습니다."))
                            }
                            val teamsUuid =
                                CustomSMPPlugin.teamsRegion.filterValues { teamRegionName ->
                                    teamRegionName.contains(
                                        regionName
                                    )
                                }.keys.toList()
                            teamsUuid.forEach { team ->
                                DataManager.setTeamRegionsNameWithUuid(
                                    team,
                                    CustomSMPPlugin.teamsRegion[team]!!.minus(regionName)
                                )
                            }
                            DataManager.removeRegionWithName(regionName)
                            sender.sendMessage(text("\"${regionName}\" 영역이 제거되었습니다."))
                        }
                    }
                }
                then("addTeam") {
                    then("regionName" to string(StringType.QUOTABLE_PHRASE)) {
                        then("teamName" to string(StringType.QUOTABLE_PHRASE)) {
                            executes {
                                val regionName: String by it
                                val teamName: String by it
                                if (!CustomSMPPlugin.regionsName.contains(regionName)) {
                                    return@executes sender.sendMessage(text("일치하는 이름의 영역이 없습니다."))
                                }
                                if (!CustomSMPPlugin.teamsName.values.contains(teamName)) {
                                    return@executes sender.sendMessage(text("일치하는 이름의 팀이 없습니다."))
                                } else {
                                    val teamUuid =
                                        CustomSMPPlugin.teamsName.filterValues { filteredTeamName -> filteredTeamName == teamName }.keys.first()
                                    if (CustomSMPPlugin.teamsRegion[teamUuid] == null || CustomSMPPlugin.teamsRegion[teamUuid]!!.isEmpty()) {
                                        DataManager.setTeamRegionsNameWithUuid(teamUuid, listOf(regionName))
                                    } else {
                                        if (CustomSMPPlugin.teamsRegion[teamUuid]!!.contains(regionName)) {
                                            return@executes sender.sendMessage("[${teamName}] 팀은 이미 \"${regionName}\" 영역에 등록되어 있습니다.")
                                        }
                                        DataManager.setTeamRegionsNameWithUuid(
                                            teamUuid,
                                            CustomSMPPlugin.teamsRegion[teamUuid]!!.plus(regionName)
                                        )
                                    }
                                    sender.sendMessage(text("[${teamName}] 팀을 \"${regionName}\" 영역에 등록하였습니다."))
                                }
                            }
                        }
                    }
                }
                then("removeTeam") {
                    then("regionName" to string(StringType.QUOTABLE_PHRASE)) {
                        then("teamName" to string(StringType.QUOTABLE_PHRASE)) {
                            executes {
                                val regionName: String by it
                                val teamName: String by it
                                if (!CustomSMPPlugin.regionsName.contains(regionName)) {
                                    return@executes sender.sendMessage(text("일치하는 이름의 영역이 없습니다."))
                                }
                                if (!CustomSMPPlugin.teamsName.values.contains(teamName)) {
                                    return@executes sender.sendMessage(text("일치하는 이름의 팀이 없습니다."))
                                } else {
                                    val teamUuid =
                                        CustomSMPPlugin.teamsName.filterValues { filteredTeamName -> filteredTeamName == teamName }.keys.first()
                                    if (CustomSMPPlugin.teamsRegion[teamUuid] == null || CustomSMPPlugin.teamsRegion[teamUuid]!!.isEmpty()) {
                                        return@executes sender.sendMessage("\"${regionName}\" 영역에 아무런 팀도 등록되어 있지 있습니다.")
                                    } else {
                                        if (!CustomSMPPlugin.teamsRegion[teamUuid]!!.contains(regionName)) {
                                            return@executes sender.sendMessage("[${teamName}] 팀은 이미 \"${regionName}\" 영역에 등록되어 있지 있습니다.")
                                        }
                                        DataManager.setTeamRegionsNameWithUuid(
                                            teamUuid,
                                            CustomSMPPlugin.teamsRegion[teamUuid]!!.minus(regionName)
                                        )
                                    }
                                    sender.sendMessage(text("[${teamName}] 팀을 \"${regionName}\" 영역에서 제거하였습니다."))
                                }
                            }
                        }
                    }
                }
            }
            then("war") {
                then("request") {
                    requires { isPlayer }
                    then("targetTeamName" to string(StringType.QUOTABLE_PHRASE)) {
                        executes {
                            val targetTeamName: String by it
                            val targetTeam =
                                CustomSMPPlugin.teamsName.filterValues { name -> name == targetTeamName }
                            val requestTeam =
                                CustomSMPPlugin.teamsMember.filterValues { members -> members.contains(player.uniqueId) }
                            var ops = 0
                            plugin.server.onlinePlayers.forEach { targetPlayer ->
                                run {
                                    if (targetPlayer.isOp) {
                                        ops++
                                    }
                                }
                            }
                            if (player.gameMode != GameMode.SURVIVAL) {
                                if (player.gameMode == GameMode.SPECTATOR) {
                                    return@executes sender.sendMessage("관전자는 해당 명령어를 사용할 수 없습니다.")
                                }
                                return@executes sender.sendMessage(text("이 명령어는 게임 참가자만 사용할 수 있습니다."))
                            }
                            if (CustomSMPPlugin.playerTeam[player.uniqueId] == null) {
                                return@executes sender.sendMessage(text("이 명령어를 사용하려면 먼저 팀에 등록이 되어 있어야 합니다."))
                            }
                            if (targetTeam.isEmpty()) {
                                return@executes sender.sendMessage("해당 팀을 찾을 수 없습니다.")
                            }
                            if (CustomSMPPlugin.teamsMember[targetTeam.keys.first()] == null || CustomSMPPlugin.teamsMember[targetTeam.keys.first()]!!.isEmpty()) {
                                return@executes sender.sendMessage("팀 멤버가 없는 팀에 전쟁을 신청할 수 없습니다.")
                            }
                            if (targetTeam.keys.first() == requestTeam.keys.first()) {
                                return@executes sender.sendMessage("나 자신은 영원한 경쟁 상대 입니다.")
                            }
                            if (ops < 1) {
                                return@executes sender.sendMessage("현재 서버에 관리자가 존재하지 않아서 전쟁을 신청할 수 없습니다.")
                            }
                            val checkPendingWarExist = CustomSMPPlugin.isWarRequestPending[requestTeam.keys.first()]
                            if (checkPendingWarExist == null || !CustomSMPPlugin.isWarRequestPending[requestTeam.keys.first()]!!) {
                                CustomSMPPlugin.isWarRequestPending[requestTeam.keys.first()] = true
                                CustomSMPPlugin.teamsMember[requestTeam.keys.first()]?.forEach { u ->
                                    Bukkit.getPlayer(u)?.bedSpawnLocation = player.location
                                }

                                plugin.server.onlinePlayers.forEach { targetPlayer ->
                                    run {
                                        if (targetPlayer.isOp) {
                                            targetPlayer.playSound(
                                                sound(
                                                    Key.key("block.note_block.pling"),
                                                    Sound.Source.AMBIENT,
                                                    10.0f,
                                                    2.0f
                                                )
                                            )
                                            targetPlayer.sendMessage(
                                                text("\n플레이어 ${player.name}님이 속한 팀 [${CustomSMPPlugin.teamsName[requestTeam.keys.first()]!!}]이 팀[${targetTeamName}]에게 전쟁을 신청했습니다. 관리자들은 상의를 한 뒤 '/smp war start \"${CustomSMPPlugin.teamsName[requestTeam.keys.first()]!!}\" \"${targetTeamName}\"' 명령어를 한번만 입력해주세요.\n")
                                                    .color(TextColor.color(0xFFA500)).decorate(TextDecoration.BOLD)
                                            )
                                        }
                                    }
                                }
                                sender.sendMessage("전쟁 신청이 완료되었습니다.")
                            } else {
                                return@executes sender.sendMessage("이미 전쟁을 신청하였습니다. 승인 또는 거절이 될때까지 기다려주세요.")
                            }
                        }
                    }
                }
                then("cancel") {
                    requires { isPlayer }
                    then("targetTeamName" to string(StringType.QUOTABLE_PHRASE)) {
                        executes {
                            val targetTeamName: String by it
                            val targetTeam =
                                CustomSMPPlugin.teamsName.filterValues { name -> name == targetTeamName }
                            val requestTeam =
                                CustomSMPPlugin.teamsMember.filterValues { members -> members.contains(player.uniqueId) }
                            var ops = 0
                            plugin.server.onlinePlayers.forEach { targetPlayer ->
                                run {
                                    if (targetPlayer.isOp) {
                                        ops++
                                    }
                                }
                            }
                            if (player.gameMode != GameMode.SURVIVAL) {
                                if (player.gameMode == GameMode.SPECTATOR) {
                                    return@executes sender.sendMessage("관전자는 해당 명령어를 사용할 수 없습니다.")
                                }
                                return@executes sender.sendMessage(text("이 명령어는 게임 참가자만 사용할 수 있습니다."))
                            }
                            if (CustomSMPPlugin.playerTeam[player.uniqueId] == null) {
                                return@executes sender.sendMessage(text("이 명령어를 사용하려면 먼저 팀에 등록이 되어 있어야 합니다."))
                            }
                            if (targetTeam.isEmpty()) {
                                return@executes sender.sendMessage("해당 팀을 찾을 수 없습니다.")
                            }
                            if (CustomSMPPlugin.teamsMember[targetTeam.keys.first()] == null || CustomSMPPlugin.teamsMember[targetTeam.keys.first()]!!.isEmpty()) {
                                return@executes sender.sendMessage("팀 멤버가 없는 팀에 전쟁을 신청할 수 없습니다.")
                            }
                            if (targetTeam.keys.first() == requestTeam.keys.first()) {
                                return@executes sender.sendMessage("나 자신은 영원한 경쟁 상대 입니다.")
                            }
                            if (ops < 1) {
                                return@executes sender.sendMessage("현재 서버에 관리자가 존재하지 않아서 전쟁 신청을 취소할 수 없습니다.")
                            }
                            val checkPendingWarExist = CustomSMPPlugin.isWarRequestPending[requestTeam.keys.first()]
                            if (checkPendingWarExist == null || !CustomSMPPlugin.isWarRequestPending[requestTeam.keys.first()]!!) {
                                return@executes sender.sendMessage("신청된 전쟁이 없습니다.")
                            } else {
                                CustomSMPPlugin.isWarRequestPending[requestTeam.keys.first()] = false
                                plugin.server.onlinePlayers.forEach { targetPlayer ->
                                    run {
                                        if (targetPlayer.isOp) {
                                            targetPlayer.playSound(
                                                sound(
                                                    Key.key("block.note_block.pling"),
                                                    Sound.Source.AMBIENT,
                                                    10.0f,
                                                    2.0f
                                                )
                                            )
                                            targetPlayer.sendMessage(
                                                text("\n플레이어 ${player.name}님이 속한 팀 [${CustomSMPPlugin.teamsName[requestTeam.keys.first()]!!}]이 팀[${targetTeamName}]에게 신청한 전쟁을 취소했습니다.")
                                                    .color(TextColor.color(0xFFA500)).decorate(TextDecoration.BOLD)
                                            )
                                        }
                                    }
                                }
                                sender.sendMessage("전쟁 신청이 취소되었습니다.")
                            }
                        }
                    }
                }
                then("start") {
                    requires { isOp }
                    then("attackTeamName" to string(StringType.QUOTABLE_PHRASE)) {
                        then("defenseTeamName" to string(StringType.QUOTABLE_PHRASE)) {
                            executes {
                                val attackTeamName: String by it
                                val defenseTeamName: String by it
                                val attackTeam =
                                    CustomSMPPlugin.teamsName.filterValues { name -> name == attackTeamName }
                                val defenseTeam =
                                    CustomSMPPlugin.teamsName.filterValues { name -> name == defenseTeamName }
                                if (CustomSMPPlugin.warTeams.size > 2) {
                                    return@executes sender.sendMessage(text("한꺼번에 진행할 수 있는 전쟁의 수를 초과합니다. 한 전쟁이 종료 된 후에 다시 시도해주세요."))
                                }
                                if (attackTeam.isEmpty()) {
                                    return@executes sender.sendMessage("일치하는 공격 팀 이름을 찾을 수 없습니다.")
                                }
                                if (defenseTeam.isEmpty()) {
                                    return@executes sender.sendMessage("일치하는 방어 팀 이름을 찾을 수 없습니다.")
                                }
                                if (attackTeam.keys.first() == defenseTeam.keys.first()) {
                                    return@executes sender.sendMessage("공격팀과 방어팀은 같을 수 없습니다.")
                                }
                                var isAttackTeamInWar = false
                                var isDefenseTeamInWar = false
                                CustomSMPPlugin.teamsMember[attackTeam.keys.first()]!!.forEach { member ->
                                    run {
                                        if (CustomSMPPlugin.isInWar[member] != null) {
                                            if (CustomSMPPlugin.isInWar[member]!!) {
                                                isAttackTeamInWar = true
                                            }
                                        }
                                    }
                                }
                                CustomSMPPlugin.teamsMember[defenseTeam.keys.first()]!!.forEach { member ->
                                    run {
                                        if (CustomSMPPlugin.isInWar[member] != null) {
                                            if (CustomSMPPlugin.isInWar[member]!!) {
                                                isDefenseTeamInWar = true
                                            }
                                        }
                                    }
                                }
                                if (isAttackTeamInWar || isDefenseTeamInWar) {
                                    return@executes sender.sendMessage(text("두 팀중 한 팀 이상이 전쟁을 진행중입니다."))
                                }
                                CustomSMPPlugin.teamsMember[attackTeam.keys.first()]!!.forEach { member ->
                                    run {
                                        plugin.server.getPlayer(member)?.sendMessage(text("10분 뒤 [${defenseTeamName}] 팀과 전쟁이 시작됩니다."))
                                    }
                                }
                                CustomSMPPlugin.teamsMember[defenseTeam.keys.first()]!!.forEach { member ->
                                    run {
                                        plugin.server.getPlayer(member)?.sendMessage(text("10분 뒤 [${attackTeamName}] 팀과 전쟁이 시작됩니다."))
                                    }
                                }
                                val pendingWarTask = plugin.server.scheduler.runTaskLater(plugin, Runnable {
                                    CustomSMPPlugin.warTeams = CustomSMPPlugin.warTeams.plus(
                                        Pair(
                                            attackTeam.keys.first(),
                                            defenseTeam.keys.first()
                                        )
                                    )
                                    CustomSMPPlugin.teamsMember[attackTeam.keys.first()]!!.forEach { member ->
                                        run {
                                            DataManager.setWarLifeWithUuid(member, 5)
                                            DataManager.setIsInWarWithUuid(member, true)
                                        }
                                    }
                                    CustomSMPPlugin.teamsMember[defenseTeam.keys.first()]!!.forEach { member ->
                                        run {
                                            DataManager.setWarLifeWithUuid(member, 5)
                                            DataManager.setIsInWarWithUuid(member, true)
                                        }
                                    }
                                    CustomSMPPlugin.teamsMember[attackTeam.keys.first()]!!.forEach { member ->
                                        run {
                                            plugin.server.getPlayer(member)?.sendMessage(text("전쟁이 시작되었습니다."))

                                        }
                                    }
                                    CustomSMPPlugin.teamsMember[defenseTeam.keys.first()]!!.forEach { member ->
                                        run {
                                            plugin.server.getPlayer(member)?.sendMessage(text("전쟁이 시작되었습니다."))
                                        }
                                    }
                                    CustomSMPPlugin.warTaskList = CustomSMPPlugin.warTaskList.minus(CustomSMPPlugin.warTaskList[0])
                                }, 126000L) // delay = 126000L로 할것
                                CustomSMPPlugin.warTaskList = CustomSMPPlugin.warTaskList.plus(pendingWarTask.taskId)
                                sender.sendMessage(text("전쟁이 예약되었습니다. 각 팀은 10분의 준비 시간을 가집니다."))
                                sender.sendMessage(text("전쟁을 중지하려면 '/smp war stop ${pendingWarTask.taskId}' 명령어를 입력하면 됩니다."))
                            }
                        }
                    }
                }
                then("stop") {
                    requires { isOp }
                    then("taskId" to int()) {
                        executes {
                            val taskId: Int by it
                            if (!CustomSMPPlugin.warTaskList.contains(taskId)) {
                                return@executes sender.sendMessage(text("일치하는 대기중인 전쟁 작업이 없습니다."))
                            }
                            plugin.server.scheduler.cancelTask(taskId)
                            CustomSMPPlugin.warTaskList = CustomSMPPlugin.warTaskList.minus(taskId)
                            sender.sendMessage(text("전쟁이 관리자에 의하여 취소되었습니다."))
                        }
                    }
                }
            }
            then("setInvincible") {
                requires { isOp || isConsole }
                    then("add") {
                        then("targetTeamName" to string(StringType.QUOTABLE_PHRASE)) {
                            executes {
                                val targetTeamName: String by it
                                val targetTeam = CustomSMPPlugin.teamsName.filterValues { teamName -> teamName == targetTeamName }
                                if (targetTeam.isEmpty()) {
                                    return@executes sender.sendMessage(text("해당 팀을 찾을 수 없습니다."))
                                }
                                DataManager.addToInvincibleTeamUuids(targetTeam.keys.first())
                                sender.sendMessage(text("해당 팀을 무적으로 설정하였습니다."))
                            }
                        }
                    }
                then("remove") {
                    then("targetTeamName" to string(StringType.QUOTABLE_PHRASE)) {
                        executes {
                            val targetTeamName: String by it
                            val targetTeam = CustomSMPPlugin.teamsName.filterValues { teamName -> teamName == targetTeamName }
                            if (targetTeam.isEmpty()) {
                                return@executes sender.sendMessage(text("해당 팀을 찾을 수 없습니다."))
                            }
                            DataManager.removeFromInvincibleTeamUuids(targetTeam.keys.first())
                            sender.sendMessage(text("해당 팀을 무적에서 제거하였습니다."))
                        }
                    }
                }

            }
        }
    }
}
