/* Implement this class. */

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Comparator;

public class MyHost extends Host {

    public Task currentTask;
    Comparator<Task> customComparator = new MyTaskComparator();
    BlockingQueue<Task> waitingQueue = new PriorityBlockingQueue<>(10,customComparator);
    public AtomicBoolean running = new AtomicBoolean(true);
    @Override
    public void run() {
        while(this.running.get()) {
            long cycleStartTime = System.currentTimeMillis();
            // check if there is any work to be done
            if (this.currentTask == null) {
                if (this.waitingQueue.size() > 0) {
                    this.currentTask = this.waitingQueue.remove();
                } else {
                    // there is no work to be done
                    continue;
                }
            }
            // check if current task is finished
            if (this.currentTask.getLeft() <= 0) {
                this.currentTask.setLeft(0);
                this.currentTask.finish();
            if (this.waitingQueue.size() > 0) {
                    this.currentTask = this.waitingQueue.remove();
                } else {
                    this.currentTask = null;
                    continue;
                }
            }

            // do some work...
            try {
                // Setting cycle duration to at least 200 ms
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Task taskOnThread = this.currentTask;

            // check preemption
            if (this.currentTask.isPreemptible() && this.waitingQueue.size() > 0) {
                if (this.waitingQueue.element().getPriority() > this.currentTask.getPriority()) {
                    // add old task back to queue
                     this.addTask(this.currentTask);
                    // add new task on the thread
                    this.currentTask = this.waitingQueue.remove();
                }
            }
            // decrement time spent on thread
            long cycleDuration = System.currentTimeMillis() - cycleStartTime;
            taskOnThread.setLeft(taskOnThread.getLeft() - cycleDuration);
        }
    }


    @Override
    public void addTask(Task task) {
      // Add task to the end of the queue
        this.waitingQueue.add(task);

    }


    @Override
    public int getQueueSize() {
        return this.waitingQueue.size();
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public BlockingQueue<Task> getWaitingQueue() {
        return waitingQueue;
    }

    @Override
    public long getWorkLeft() {
        long result = 0;
        for (Task task : this.waitingQueue) {
            result += task.getLeft();
        }
        if (currentTask != null)
            result += currentTask.getLeft();
        return Math.round((float) result / 1000);
    }

    @Override
    public synchronized void shutdown() {
        this.running.set(false);
    }
}

class MyTaskComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        // Compare based on custom priority logic
        int prio1 = o1.getPriority();
        int prio2 = o2.getPriority();
        if (prio1 == prio2)
            return o1.getStart() - o2.getStart();
        if (prio1 < prio2)
            return 1;
        return -1;
    }
}