package me.devwckd.mcd_service.util

import org.koin.core.context.GlobalContext

inline fun <reified T : Any> globalInject() = GlobalContext.get().inject<T>()