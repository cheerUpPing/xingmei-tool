package com.xingmei.oom;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OOMTest {


    public static void main(String[] args) {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
        final List<User> users = Collections.synchronizedList(new ArrayList<User>());
        final Map<String, String> map  = new HashMap<>();
        for (int i = 0; i < 10; i++){
            // 线程名字
            final int finalI = i;
            Thread thread = new Thread(){
                @Override
                public void run() {
                    for (int j = finalI; ; j++){
                        //users.add(new User(this.getName(), j));
                        map.put("" + j, "" + j);
                    }
                }
            };
            thread.setName("thread_name_" + i);
            // 执行线程
            threadPoolExecutor.submit(thread);
        }
        System.out.println("========================");
    }
}
