package com.zhangbo.codestore.thread.multicalcutedata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 多线程计算数据
 * <p>
 * 使用实现多任务统计
 * countDownLatch这个类使一个线程等待其他线程各自执行完毕后再执行。
 * countDownLatch通过一个计数器来实现的，计数器的初始值是线程的数量，
 * 每当一个线程执行完毕后，计数器的值就-1，当计数器的值为0时，表示所有线程都执行完毕，然后在闭锁上等待的线程就可以恢复工作了。
 *
 * @author zhangbo
 */
public abstract class AbstractMultiCalcuteData<R, S> {

    /**
     * 执行任务的线程队列
     */
    private final BlockingQueue<Future<R>> futureQueue = new LinkedBlockingDeque<>();

    /**
     * 任务线程池
     */
    private final ExecutorService executorService;

    /**
     * 多线程任务启动门，所有任务就绪执行countDown()，子线程开始执行
     */
    private final CountDownLatch startLatch = new CountDownLatch(1);

    /**
     * 多线程任务结束门，子任务结束执行countDown()，主线程开始执行
     */
    private final CountDownLatch endLatch;

    /**
     * 待处理数据
     */
    private final List<S> data;

    public AbstractMultiCalcuteData(List<S> data) {
        if (data != null && !data.isEmpty()) {
            this.executorService = Executors.newFixedThreadPool(Integer.MAX_VALUE);
            this.endLatch = new CountDownLatch(data.size());
            this.data = data;
        } else {
            this.executorService = null;
            this.endLatch = null;
            this.data = null;
        }
    }

    /**
     * 获取所有线程执行结果
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public List<R> getResult() throws InterruptedException, ExecutionException {
        List<R> resultList = new ArrayList<>();
        for (S d : data) {
            Future<R> future = executorService.submit(new Task(d));
            futureQueue.add(future);
        }
        startLatch.countDown();//所有任务已就绪，子任务开始执行
        endLatch.await();//等待子任务执行完成
        //获取子任务结果
        System.out.println("子任务执行完成,开始汇总数据");
        for (Future<R> future : futureQueue) {
            resultList.add(future.get());
        }
        executorService.shutdown();
        return resultList;
    }

    /**
     * 任务执行过程
     *
     * @param data
     * @return
     */
    public abstract R process(S data);

    /**
     * 线程类
     */
    private class Task implements Callable<R> {
        private S data;

        public Task(S data) {
            this.data = data;
        }

        @Override
        public R call() throws Exception {
            startLatch.await();//初始化任务，等待所有任务就绪
            String threadName = Thread.currentThread().getName();
            R result = null;
            try {
                result = process(data);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println(threadName + "子任务执行完成");
                //任务执行结束
                endLatch.countDown();
            }
            return result;
        }
    }

}
