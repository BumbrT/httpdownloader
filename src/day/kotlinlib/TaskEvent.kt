package day.kotlinlib

/**
 * used by disruptor classes for cross thread messaging without blocking
 * DownloadScheduler observes TaskEvent objects, download Thread Creates Them
 */
data class TaskEvent(var stateChanged: IdentifiedTaskState?, var bytesDownloaded: Int?) {
    fun process() {
        if (this.stateChanged != null) {
            val state = this.stateChanged as IdentifiedTaskState
            if (state.isCreated)
                actions.forEach { x -> x.OnCreated(this.stateChanged as IdentifiedTaskState) }
            else
                actions.forEach { x -> x.OnStateChanged(this.stateChanged as IdentifiedTaskState) }
        } else actions.forEach { x -> x.OnDownloadedBytes(this.bytesDownloaded as Int) }
    }

    class EventAction(
            var OnCreated: (state: IdentifiedTaskState) -> Unit,
            var OnStateChanged: (state: IdentifiedTaskState) -> Unit,
            var OnDownloadedBytes: (bytes: Int) -> Unit) {
    }

    companion object EventActionsCollection {
        private val actions = linkedListOf<EventAction>()
        public fun subscribe(action: EventAction) {
            actions.add(action)
        }
    }
}