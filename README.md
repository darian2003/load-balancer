# 🧠 Load Balancer Simulator

## Overview

This project implements a **multi-threaded task scheduling system** for a simulated datacenter, built in **Java**. The system distributes incoming tasks across multiple hosts based on different **load balancing policies**.

The core objective is to explore and compare how various scheduling strategies affect workload distribution and execution efficiency in a concurrent environment.

## Key Features

- **Multi-threaded architecture** using Java Threads
- Four built-in scheduling policies:
  - **Round Robin**
  - **Shortest Queue**
  - **Size Interval Task Assignment (SITA)**
  - **Least Work Left**
- Thread-safe task assignment and execution
- Prioritized task queues using `PriorityBlockingQueue`
- Safe concurrency handling via `synchronized` methods and `AtomicBoolean` flags

## Implementation Highlights

### `MyDispatcher.java`

Handles task distribution to hosts using the selected policy. To ensure thread safety when multiple task generators operate in parallel, the `addTask(Task task)` method is synchronized, preventing race conditions during task forwarding.

### `MyHost.java`

Each host processes tasks from a local queue in its own thread. To manage concurrent access to this queue:
- A **`PriorityBlockingQueue`** is used for thread-safe priority-based scheduling.
- A custom comparator (`MyTaskComparator`) ensures tasks are ordered by priority.
- The main execution loop runs tasks in time slices (minimum 200ms), checking for completion or preemption.
- The host gracefully shuts down when signaled, using an `AtomicBoolean` flag to manage its running state.

## Technologies Used

- Java Concurrency (Threads, `synchronized`, `AtomicBoolean`)
- Blocking Queues (`PriorityBlockingQueue`)
- Object-Oriented Design

---

> 📄 For full requirements and context, see the [Load Balancer.pdf](./Load%20Balancer.pdf) file.
