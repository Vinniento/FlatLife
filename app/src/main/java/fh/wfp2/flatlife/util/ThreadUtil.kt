package fh.wfp2.flatlife.util

import java.util.concurrent.Executors

class ThreadUtil {

    private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

    fun ioThread(f: () -> Unit) {
        IO_EXECUTOR.execute(f)
    }
}