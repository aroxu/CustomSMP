package me.aroxu.customsmp

import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.LiteralNode
import me.aroxu.customsmp.database.DataManager
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
                                DataManager.setSurvivalLifeWithUuid(player.uniqueId.toString(), life)
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
                                DataManager.setWarLifeWithUuid(player.uniqueId.toString(), life)
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
                            player.maxHealth = maxHealth.toDouble()
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
                            DataManager.setIsInWarWithUuid(player.uniqueId.toString(), true)
                            sender.sendMessage("플레이어 ${player.name}에게 전쟁 테스트 모드를 활성화 하였습니다.")
                        }
                    }
                }
                then("disable") {
                    then("player" to player()) {
                        executes {
                            val player: Player by it
                            CustomSMPPlugin.isInWar[player.uniqueId] = false
                            DataManager.setIsInWarWithUuid(player.uniqueId.toString(), false)
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
                        sender.sendMessage("플레이어 ${player.name}의 상태는 다음과 같습니다:\n최대 체력: ${player.maxHealth.toInt()}\n남은 생존 목숨: $targetPlayerSurvivalLife\n남은 전쟁 목숨: $targetPlayerWarLife\n전쟁 진행중: $isTargetPlayerInWarStatusText")
                    }
                }
            }
        }
    }
}
