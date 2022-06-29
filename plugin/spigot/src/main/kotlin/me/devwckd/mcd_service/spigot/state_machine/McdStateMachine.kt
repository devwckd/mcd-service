package me.devwckd.mcd_service.spigot.state_machine

import de.halfbit.comachine.comachine
import kotlinx.coroutines.supervisorScope

fun newMcdStateMachine() = comachine<McdState, McdEvent>(startWith = Connect) {
    onConnect()
    onActive()
    onReconnect()
    onDisconnect()
}