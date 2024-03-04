/* Implement this class. */

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDispatcher extends Dispatcher {
    // this variable is used only by RoundRobin algorithm
    public int lastNodeID = 0;
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public synchronized void addTask(Task task) {
        // upon receiving a task, forward it to the appropriate node based on the algorithm used
        if (this.algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            int numberOfHosts = this.hosts.size();
            // forward task to node with ID = (i + 1) % n, where i is last ID used
            this.hosts.get(lastNodeID % numberOfHosts).addTask(task);
            lastNodeID++;
        } else if (this.algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
            Integer sizes[] = new Integer[this.hosts.size()];
            int smallest_index = 0;
            // compute queueSize for each node in order to find the shortest one
            for (int i = 0; i < this.hosts.size(); i++) {
                MyHost myHost = (MyHost)this.hosts.get(i);
                sizes[i] = myHost.getQueueSize();
                // add 1 for current running task on node (if existent)
                if (myHost.getCurrentTask() != null)
                    sizes[i]++;
                if (sizes[i] < sizes[smallest_index])
                    smallest_index = i;
            }
            this.hosts.get(smallest_index).addTask(task);
        } else if (this.algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
            if (task.getType() == TaskType.SHORT)
                this.hosts.get(0).addTask(task);
            else if (task.getType() == TaskType.MEDIUM)
                this.hosts.get(1).addTask(task);
            else if (task.getType() == TaskType.LONG)
                this.hosts.get(2).addTask(task);
        } else if (this.algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
            long sizes[] = new long[this.hosts.size()];
            int smallest_index = 0;
            // compute workload for each node in order to find the least busy one
            for (int i = 0; i < this.hosts.size(); i++) {
                MyHost myHost = (MyHost)this.hosts.get(i);
                // getWorkLeft() method returns the rounded sum of all the tasks
                // in the node's waitingQueue + its currently running task
                sizes[i] = myHost.getWorkLeft();
                if (sizes[i] < sizes[smallest_index])
                    smallest_index = i;
            }
           this.hosts.get(smallest_index).addTask(task);
        }
    }
}
