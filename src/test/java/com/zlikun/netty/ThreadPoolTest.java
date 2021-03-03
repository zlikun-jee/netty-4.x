package com.zlikun.netty;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Java 线程池测试
 */
@Slf4j
public class ThreadPoolTest {

    ThreadFactory factory;

    @BeforeEach
    void setup() {
        factory = new ThreadFactory() {
            final AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "thread-" + counter.incrementAndGet());
            }
        };
    }

    /**
     * 单线程线程池
     *
     * @throws InterruptedException
     */
    @Test
    void testSingleThreadExecutor() throws InterruptedException {
        // 单个线程的线程池
//        ExecutorService exec = Executors.newSingleThreadExecutor();
        ExecutorService exec = Executors.newSingleThreadExecutor(factory);
        // 执行任务，无论执行多少个任务，实际都只由一个线程执行
        exec.execute(() -> log.info("[SingleThreadExecutor] Number:{},Time:{}, Thread:{}", 1, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.execute(() -> log.info("[SingleThreadExecutor] Number:{},Time:{}, Thread:{}", 2, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.execute(() -> log.info("[SingleThreadExecutor] Number:{},Time:{}, Thread:{}", 3, System.currentTimeMillis(), Thread.currentThread().getName()));
        // 关闭线程池（标记），此后线程池除了等待现有任务完成外，不能做其它操作
        exec.shutdown();
        // 检查标记状态
        assertTrue(exec.isShutdown());
        // 但实际此时线程池并未真正关闭，要等任务全部执行完成后才会变更为终止状态
        assertFalse(exec.isTerminated());
        // 等待线程池终止
        while (!exec.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        // 此时线程池为终止状态
        assertTrue(exec.isTerminated());
    }

    /**
     * 固定数量线程线程池
     */
    @Test
    void testFixedThreadPool() throws InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool(3, factory);
        exec.execute(() -> log.info("[FixedThreadPool] Number:{},Time:{}, Thread:{}", 1, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.execute(() -> log.info("[FixedThreadPool] Number:{},Time:{}, Thread:{}", 2, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.execute(() -> log.info("[FixedThreadPool] Number:{},Time:{}, Thread:{}", 3, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.execute(() -> log.info("[FixedThreadPool] Number:{},Time:{}, Thread:{}", 4, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.shutdown();
        assertTrue(exec.isShutdown());
        while (!exec.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        assertTrue(exec.isTerminated());
    }

    @Test
    void testCachedThreadPool() throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool(factory);
        exec.execute(() -> log.info("[CachedThreadPool] Number:{},Time:{}, Thread:{}", 1, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.execute(() -> log.info("[CachedThreadPool] Number:{},Time:{}, Thread:{}", 2, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.execute(() -> log.info("[CachedThreadPool] Number:{},Time:{}, Thread:{}", 3, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.execute(() -> log.info("[CachedThreadPool] Number:{},Time:{}, Thread:{}", 4, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.shutdown();
        assertTrue(exec.isShutdown());
        while (!exec.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        assertTrue(exec.isTerminated());
    }

    /**
     * 上述线程池本质上都是通过 ThreadPoolExecutor 类来实现的，直接通过 ThreadPoolExecutor 类可以实现高度自定义线程池
     *
     * @see ThreadPoolExecutor
     */
    @Test
    void testThreadPoolExecutor() throws InterruptedException {
        // 核心线程数
        int coreThreads = 3;
        // 最大线程数
        int maxThreads = 5;
        // 线程存活时间
        long keepAliveTime = 0;
        // 线程存活时间单位
        TimeUnit keepAliveTimeUnit = TimeUnit.SECONDS;
        // SynchronousQueue 是一种无界无缓冲的阻塞式队列，实际不能存储元素，会直接将元素效给消费者，如果没有等到消费者就会阻塞
        // 目前在 #newCachedThreadPool() 中会用到，特别要注意的是 maximumPoolSize = Integer.MAX_VALUE ，这是为了避免请求被拒绝
        // BlockingQueue<Runnable> workerQueue = new SynchronousQueue<>();
        // 任务存储队列
        BlockingQueue<Runnable> workerQueue = new LinkedBlockingQueue<>();
        // 拒绝策略，默认：AbortPolicy，其它可选：CallerRunsPolicy，DiscardOldestPolicy，DiscardPolicy，以及自定义实现
        // AbortPolicy      直接拒绝，抛出异常，实际就是什么也不做，只是抛出异常
        // DiscardPolicy    直接拒绝，不抛出异常，实际就是什么也不做，直接丢弃任务
        // CallerRunsPolicy     如果线程池未标记为 shutdown ，由调用线程直接运行该任务
        // DiscardOldestPolicy  从任务队列头部取出一个任务丢弃，将该任务加入队列（重复执行，直到任务被加入到队列中）
        RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
        ExecutorService exec = new ThreadPoolExecutor(coreThreads
                , maxThreads
                , keepAliveTime
                , keepAliveTimeUnit
                , workerQueue
                , factory
                , rejectedExecutionHandler);

        exec.execute(() -> log.info("[ThreadPoolExecutor] Number:{},Time:{}, Thread:{}", 1, System.currentTimeMillis(), Thread.currentThread().getName()));
        for (int i = 0; i < 3; i++) {
            exec.execute(() -> {
                sleep(100L);
            });
        }
        exec.execute(() -> log.info("[ThreadPoolExecutor] Number:{},Time:{}, Thread:{}", 2, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.execute(() -> log.info("[ThreadPoolExecutor] Number:{},Time:{}, Thread:{}", 3, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.execute(() -> log.info("[ThreadPoolExecutor] Number:{},Time:{}, Thread:{}", 4, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.execute(() -> log.info("[ThreadPoolExecutor] Number:{},Time:{}, Thread:{}", 5, System.currentTimeMillis(), Thread.currentThread().getName()));
        exec.shutdown();
        assertTrue(exec.isShutdown());
        while (!exec.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        assertTrue(exec.isTerminated());
    }

    @Test
    void testSubmitTask() throws InterruptedException {
        ExecutorService exec = Executors.newSingleThreadExecutor(factory);

        // 提交一个任务
        Future<?> future = exec.submit(() -> {
            log.info("[SubmitTask] begin 1 task.");
            sleep(100L);    // 任务取消时可能刚好在执行休眠方法，会导致抛出 InterruptedException 异常
            log.info("[SubmitTask] end 1 task.");
        });
        // 如果任务尚未完成就取消任务
        sleep(50L);
        if (!future.isDone()) {
            assertTrue(future.cancel(true));
            assertTrue(future.isCancelled());
        }

        // 任务结果
        Future<String> future2 = exec.submit(() -> "Hello");
        // Future#get() 会阻塞直到任务执行完
        try {
            String result = future2.get();
            assertEquals("Hello", result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        @Data
        class SubmitResult {
            String result;
        }

        class SubmitTask implements Runnable {
            SubmitResult value;

            public SubmitTask(SubmitResult value) {
                this.value = value;
            }

            @Override
            public void run() {
                this.value.setResult("Hello");
            }
        }

        SubmitResult value = new SubmitResult();
        Future<SubmitResult> future3 = exec.submit(new SubmitTask(value), value);
        try {
            String result = future3.get().getResult();
            assertEquals("Hello", result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        exec.shutdown();
        assertTrue(exec.isShutdown());
        while (!exec.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        assertTrue(exec.isTerminated());
    }

    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void testSingleThreadScheduledExecutor() throws ExecutionException {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(factory);
        ScheduledFuture<?> future = exec.scheduleAtFixedRate(() -> {
            log.info("-> {}", System.currentTimeMillis());
        }, 0, 100, TimeUnit.MILLISECONDS);
        try {
            future.get(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } catch (TimeoutException e) {
            log.error(e.getMessage());
        }
        // 任务会被终止
        exec.shutdown();
    }

}
