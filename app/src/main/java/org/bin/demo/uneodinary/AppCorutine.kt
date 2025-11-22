package org.bin.demo.uneodinary

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.bin.demo.debug
import kotlin.coroutines.CoroutineContext

object AppCoroutineExceptionHandler : CoroutineExceptionHandler {
    override val key: CoroutineContext.Key<*> = CoroutineExceptionHandler

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        debug("===== Caught exception in global CoroutineContext =====")
        debug("Context: $context")
        debug("Exception message: ${exception.message}")
        exception.printStackTrace()
    }
}

object AppCoroutineScope {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + AppCoroutineExceptionHandler)

    fun cancel() {
        scope.cancel()
        debug("AppCoroutineScope cancelled.")
    }
}
