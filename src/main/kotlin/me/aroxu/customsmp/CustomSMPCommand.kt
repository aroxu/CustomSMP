package me.aroxu.customsmp

import io.github.monun.kommand.node.LiteralNode

/**
 * @author aroxu
 */

object CustomSMPCommand {
    fun register(builder: LiteralNode) {
        builder.apply {
            then("about") { executes { sender.sendMessage("CustomSMP by aroxu.") } }
        }
    }
}
