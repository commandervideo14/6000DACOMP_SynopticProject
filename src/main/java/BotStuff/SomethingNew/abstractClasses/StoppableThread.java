package BotStuff.SomethingNew.abstractClasses;

public abstract class StoppableThread extends Thread {

    protected volatile boolean threadStopped;

    public void end() {
        threadStopped = true;
    }
}
