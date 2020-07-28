package com.zhangbo.codestore.thread.multicalcutedata;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MultiCalcuteDataTest {

    public static void main(String[] args) {

        List<String> allDataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            allDataList.add(UUID.randomUUID().toString());
        }

        AbstractMultiCalcuteData<String, String> calcuteData = new AbstractMultiCalcuteData<String, String>(allDataList) {
            @Override
            public String process(String data) {
                return data + "-over";
            }
        };
        try {
            List<String> result = calcuteData.getResult();
            result.forEach(System.out::println);
            System.out.println("任务统计完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


}
