package com.xclenter;

public class Main {
    public static void main(String[] args) {
        Environment environment = new Environment();
        environment.addItem(new Boiler(50,70,25 ,HeatStrategy.PID));

        environment.start();

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        environment.end();
    }
}
