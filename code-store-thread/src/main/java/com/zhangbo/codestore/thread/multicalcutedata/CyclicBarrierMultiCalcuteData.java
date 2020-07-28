package com.zhangbo.codestore.thread.multicalcutedata;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhangbo
 * @description:
 * @date 2020-07-24 17:46
 */
public class CyclicBarrierMultiCalcuteData {
    private static final CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i <10 ; i++) {
            executorService.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    System.out.println("我干完了，等待其他兄弟");
                    cyclicBarrier.await();
                    return Thread.currentThread().getName();
                }
            });
        }

        while (true) {
           if(cyclicBarrier.getNumberWaiting()==0){
               System.out.println("over");
               break;
           }else{
               System.out.println("还没干完");
           }
        }

    }
}
