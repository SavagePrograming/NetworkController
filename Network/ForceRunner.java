package Network;


public class ForceRunner extends Thread {

    Network net;
    int timeDelay;
    double push;
    double pull;
    int wiggle;

    public ForceRunner(Network net, int timeDelay, double push, double pull, int wiggle){
        this.net = net;
        this.timeDelay = timeDelay;
        this.pull = pull;
        this.push = push;
        this.wiggle = wiggle;
    }


    @Override
    public void run() {
        while (this.net.isForce()){
            try {
                sleep(this.timeDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.net.runForceOnce(push, pull, wiggle);
        }
    }
}
