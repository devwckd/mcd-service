package me.devwckd.mcd_service.util

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.command.AsyncDockerCmd
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.suspendCoroutine

suspend fun <C, R, T : AsyncDockerCmd<C, R>> T.suspended(predicate: (R) -> Boolean = { true }) =
    suspendCoroutine<R> { cont ->
        exec(object : ResultCallback.Adapter<R>() {
            private val resumed = AtomicBoolean(false)

            override fun onNext(obj: R) {
                if (!predicate(obj)) return
                if (resumed.get()) return

                cont.resumeWith(Result.success(obj))

                resumed.set(true)
            }

            override fun onError(throwable: Throwable) {
                if (resumed.get()) return

                cont.resumeWith(Result.failure(throwable))

                resumed.set(true)
            }
        })
    }

fun <C, R, T : AsyncDockerCmd<C, R>> T.executeAsChannel() = Channel<R>(Channel.UNLIMITED)
    .apply {
        val callback = object : ResultCallback.Adapter<R>() {
            override fun onNext(obj: R) = runBlocking {
                send(obj)
            }

            override fun onError(throwable: Throwable) {
                cancel(CancellationException("Error", throwable))
            }

            override fun close() {
                this@apply.close()
            }
        }

        exec(callback)
    }
