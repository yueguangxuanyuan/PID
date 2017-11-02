package com.xclenter;

import java.lang.invoke.SwitchPoint;

public class Boiler implements Item{
    float current;
    float target;
    float envTemperature;
    HeatStrategy heatStrategy;
    Heat heatMechanism;
    final float envCoolParam = 0.2f;

    interface Heat{
        public float heat();
    }

    class NormalHeat implements Heat{
        @Override
        public float heat() {
            float bias = target - current;
            return bias > 0 ? 20f:0;
        }
    }

    class PIDHeat implements Heat{
        float lastError;
        float preError;

        final float P_const = 40f;
        final float I_const = 150f;
        final float D_const = 1f;
        public PIDHeat(){
            preError = lastError = 0;
        }
        private float range(float heatTemp){
            if(heatTemp < 0){
                return 0;
            }
            if(heatTemp > 40){
                return 40;
            }
            return  heatTemp;
        }
        @Override
        public float heat() {
            float currentError = target - current;

            System.out.println("PID params "+ currentError + " " + lastError + " " + preError);

            float heatTemp = P_const * (currentError-lastError)
                    + I_const * currentError
                    + D_const * (currentError + preError -2*lastError);
            preError = lastError;
            lastError = currentError;
            heatTemp = range(heatTemp);
            System.out.println("PID out "+ heatTemp);
            return heatTemp;
        }
    }

    public Boiler(float current, float target, float envTemperature) {
        this.current = current;
        this.target = target;
        this.envTemperature = envTemperature;
        {
            heatStrategy = HeatStrategy.Normal;
            heatMechanism = new NormalHeat();
        }
    }

    public Boiler(float current, float target, float envTemperature, HeatStrategy heatStrategy) {
        this.current = current;
        this.target = target;
        this.envTemperature = envTemperature;
        {
            this.heatStrategy = heatStrategy;
            switch (heatStrategy){
                case PID:
                    heatMechanism = new PIDHeat();
                    break;
                case Normal:
                    heatMechanism = new NormalHeat();
                    break;
                default:
                    heatMechanism = new NormalHeat();
                    break;
            }
        }
    }

    private float heat(){
        return heatMechanism.heat();
    }

    private float cool(){
        //用二次函数拟合降温曲线    a * (temperature - envTemperature)^2
        return - envCoolParam*2*(current - envTemperature);
        //return 0;
    }

    final float dt = 0.01f;
    @Override
    public void tick() {
        current = current + (cool()+ heat())*dt;
        System.out.println("boiler tick : " + current);
    }
}
