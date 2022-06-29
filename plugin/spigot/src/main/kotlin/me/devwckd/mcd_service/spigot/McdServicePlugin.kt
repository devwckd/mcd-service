package me.devwckd.mcd_service.spigot

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.scope
import de.halfbit.comachine.startInScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.lastOrNull
import me.devwckd.mcd_service.spigot.listener.GeneralListener
import me.devwckd.mcd_service.spigot.state_machine.Disconnect
import me.devwckd.mcd_service.spigot.state_machine.McdState
import me.devwckd.mcd_service.spigot.state_machine.Shutdown
import me.devwckd.mcd_service.spigot.state_machine.newMcdStateMachine

class McdServicePlugin : SuspendingJavaPlugin(), CoroutineScope {

    override val coroutineContext = Job()
    val stateMachine = newMcdStateMachine()
    lateinit var currentState: McdState

    override suspend fun onEnableAsync() {
        registerListeners()
        launch { stateMachine.state.collect {currentState = it } }
        stateMachine.startInScope(this)
    }

    override suspend fun onDisableAsync() {
        stateMachine.send(Shutdown(true))
        coroutineContext.complete()
    }

    private fun registerListeners() {
        server.pluginManager.registerEvents(GeneralListener(this), this)
    }
}