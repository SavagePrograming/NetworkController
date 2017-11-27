package ServerCleint;

public class NetworkRunner extends Thread {

    Network net;
    int timeDelay;

    public NetworkRunner(Network net, int timeDelay){
        this.net = net;
        this.timeDelay = timeDelay;
    }
    @Override
    public void run() {
        while (this.net.isOn()){
            try {
                sleep(this.timeDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.net.runOnce();
        }
    }
}
