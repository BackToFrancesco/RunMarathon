import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class StopwatchService {

    var time by mutableStateOf(0.milliseconds)
    var timeIntervals by mutableStateOf(mutableListOf<Duration>())

    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var isActive = false

    fun start() {
        if (isActive) return

        coroutineScope.launch {
            val timeStart = System.currentTimeMillis()
            this@StopwatchService.isActive = true
            while (this@StopwatchService.isActive) {
                delay(10L)
                time = System.currentTimeMillis().milliseconds - timeStart.milliseconds
            }
        }
    }

    fun addInterval() {
        if (!isActive) return

        timeIntervals.add(time)
    }

    fun reset() {
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.Main)
        timeIntervals = mutableListOf()
        isActive = false
    }
}