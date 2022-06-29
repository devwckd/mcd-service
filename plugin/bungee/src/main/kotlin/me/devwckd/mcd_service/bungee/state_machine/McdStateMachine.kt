package me.devwckd.mcd_service.bungee.state_machine

import de.halfbit.comachine.comachine

fun newMcdStateMachine() = comachine<McdState, McdEvent>(startWith = Connect) {
    onConnect()
    onActive()
    onReconnect()
    onDisconnect()
}