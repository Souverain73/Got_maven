package got.utils;

import java.util.function.Consumer;

/**
 * Created by Souverain73 on 23.03.2017.
 */
public class Timers {
    public interface Timer{
        void start(boolean async);
        void stop();
        boolean isRunning();
        boolean isFinished();
    }

    private static abstract class AbstractTimer implements Timer{
        protected int time;
        protected int currentTime;
        protected int step;
        protected boolean running;

        public AbstractTimer(int time, int step) {
            this.time = time;
            this.step = step;
        }

        @Override
        public void start(boolean async) {
            currentTime = time;
            running = true;
            MainLoop ml = new MainLoop();
            if (async){
                new Thread(ml).start();
            }else {
                ml.run();
            }
        }

        @Override
        public void stop() {
            running = false;
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public boolean isFinished() {
            return currentTime > 0;
        }

        protected abstract void onStep();
        protected abstract void onFinish();

        private class MainLoop implements Runnable{
            public void run(){
                while(currentTime > 0 && running){
                    onStep();
                    currentTime -= step;
                    try {
                        Thread.sleep(step);
                    }catch (InterruptedException e){

                    }
                }
                running = false;
                onFinish();
            }
        }
    }

    private static class CallbackTimer extends  AbstractTimer{

        Runnable callback;
        public CallbackTimer(int time, Runnable callback) {
            super(time, time);
            this.callback = callback;
        }

        @Override protected void onStep() {}

        @Override
        protected void onFinish() {
            callback.run();
        }
    }

    private static class Interval extends AbstractTimer{
        Runnable callback;
        public Interval(int step, Runnable callback) {
            super(step+1, step);
            this.callback = callback;
        }

        @Override
        protected void onStep() {
            time =step+1;
            callback.run();
        }

        @Override
        protected void onFinish() {}
    }

    private static class Counter extends AbstractTimer{
        private Consumer<Integer> stepCallback;
        private Runnable finishCallback;

        public Counter(int time, int step, Consumer<Integer> stepCallback, Runnable finishCallback) {
            super(time, step);
            this.stepCallback = stepCallback;
            this.finishCallback = finishCallback;
        }

        @Override
        protected void onStep() {
            if (stepCallback != null){
                stepCallback.accept(currentTime);
            }
        }

        @Override
        protected void onFinish() {
            if (finishCallback != null){
                finishCallback.run();
            }
        }
    }

    public static Timer getTimer(int time, int step, Runnable callback){
        return new CallbackTimer(time, callback);
    }

    public static Timer getTimer(int time, Runnable callback){
        return new CallbackTimer(time, callback);
    }

    public static Timer getInterval(int interval, Runnable callback){
        return new Interval(interval, callback);
    }

    public static Timer getCounter(int time, int step, Consumer<Integer> onStep, Runnable onFinish){
        return new Counter(time, step, onStep, onFinish);
    }

    public static Timer getCounter(int time, int step, Consumer<Integer> onStep){
        return new Counter(time, step, onStep, null);
    }

    public static Timer getCounter(int time, Consumer<Integer> onStep, Runnable onFinish){
        return new Counter(time, 1000, onStep, onFinish);
    }

    public static Timer getCounter(int time, Consumer<Integer> onStep){
        return new Counter(time, 1000, onStep, null);
    }

    public static void wait(int time){
        (new CallbackTimer(time, ()->{})).start(false);
    }
}
