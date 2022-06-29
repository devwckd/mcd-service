package me.devwckd.mcd_service.bungee

import com.github.shynixn.mccoroutine.bungeecord.SuspendingPlugin
import com.github.shynixn.mccoroutine.bungeecord.scope
import de.halfbit.comachine.startInScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import me.devwckd.mcd_service.bungee.listener.GeneralListener
import me.devwckd.mcd_service.bungee.state_machine.Disconnect
import me.devwckd.mcd_service.bungee.state_machine.McdState
import me.devwckd.mcd_service.bungee.state_machine.Shutdown
import me.devwckd.mcd_service.bungee.state_machine.newMcdStateMachine
import net.md_5.bungee.api.plugin.Plugin
import java.util.logging.Logger

class McdServicePlugin : SuspendingPlugin(), CoroutineScope {

    companion object {
        lateinit var LOGGER: Logger
    }

    override val coroutineContext = Job()
    val stateMachine = newMcdStateMachine()
    lateinit var currentState: McdState

    override suspend fun onLoadAsync() {
        LOGGER = proxy.logger
    }

    override suspend fun onEnableAsync() {
        registerListeners()
        launch { stateMachine.state.collect { currentState = it } }
        stateMachine.startInScope(this)
    }

    override suspend fun onDisableAsync() {
        stateMachine.send(Shutdown(true))
        coroutineContext.complete()
    }

    private fun registerListeners() {
        proxy.pluginManager.registerListener(this, GeneralListener(this))
    }

}