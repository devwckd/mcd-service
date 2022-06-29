package me.devwckd.mcd_service.bungee.util

import de.halfbit.comachine.dsl.LaunchBlock
import de.halfbit.comachine.dsl.WhenInBlock

fun <S : Any, SS : S, E : Any> WhenInBlock<S, SS, E>.launchOnEnter(block: suspend LaunchBlock<S, SS>.() -> Unit) = onEnter {
    launchInState(block)
}