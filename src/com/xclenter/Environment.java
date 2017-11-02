package com.xclenter;

import java.util.ArrayList;
import java.util.List;

public class Environment {
    private Thread envThread;
    private Object locker;
    private boolean isInterrupted ;
    private List<Item> itemList;
    Object listLocker;

    public Environment(){
        envThread = null;
        isInterrupted = false;
        locker = new Object();
        listLocker = new Object();
        itemList = new ArrayList<>();
    }

    public void addItem(Item item){
        if(item != null){
            synchronized (listLocker){
                itemList.add(item);
            }
        }
    }

    class EnvironmentThread implements Runnable{
        @Override
        public void run() {
            while(true){
                synchronized (locker){
                    if(isInterrupted){
                        break;
                    }
                }
                tick();
                try {
                    //设置时间间隔
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void start(){
        envThread = null;
        isInterrupted = false;
        envThread = new Thread(new EnvironmentThread());
        envThread.start();
        System.out.println("环境启动");
    }

    private void tick(){
        //System.out.println(System.currentTimeMillis());
        synchronized (listLocker){
            for(Item item : itemList){
                item.tick();
            }
        }
    }

    public void end(){
        if(envThread != null && envThread.isAlive()){
            synchronized (locker){
                isInterrupted = true;
            }
            try {
                envThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("环境终止");
        }
    }
}
