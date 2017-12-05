package Network;

import java.util.HashMap;
import java.util.Observable;


public class Network extends Observable{
    private HashMap<String, Node> nodes;
    private int width;
    private int height;

    private boolean death = true;
    private boolean On;
    private boolean Active;
    private boolean Text = true;
    private boolean Connections = true;
    public final int minTime = 5;
    public final int maxTime = 10;
    public final int margin = 20;

    public final double probability = .5;
    private boolean Force;

    public HashMap<String, Node> getNodes() {
        return nodes;
    }

    public Network(){
        nodes = new HashMap<>();
        On = false;
        Active = true;
    }

    public boolean isOn() {
        return On;
    }

    public int getWidth(){ return width; }

    public int getHeight(){ return height; }



    public Network(HashMap<String, Node> nodes){
        this.nodes = nodes;
        On = false;
        Active = true;
    }

    public synchronized void infect(String name){
        if (name.equals(Protocol.ALL)){
            for (Node n:nodes.values()){
                n.setState(State.Infected);
                n.setTimeCounter(((int) (Math.random() * (maxTime - minTime))) + minTime);
            }
        }else if(name.equals(Protocol.RANDOM)){
            Node n = nodes.values().toArray(new Node[0])[(int)(Math.random() * nodes.values().size())];
            n.setState(State.Infected);
            n.setTimeCounter(((int) (Math.random() * (maxTime - minTime))) + minTime);

        }else{
            this.nodes.get(name).setState(State.Infected);
            this.nodes.get(name).setTimeCounter(((int) (Math.random() * (maxTime - minTime))) + minTime);
        }
    }


    public boolean isDeath() {
        return death;
    }

    public void setDeath(boolean death) {
        this.death = death;
    }

    public synchronized void suseptable(String name){
        if (name.equals(Protocol.ALL)){

            for (Node n:nodes.values()){
                n.setState(State.Susceptible);
            }
        }else if(name.equals(Protocol.RANDOM)){
            Node n = nodes.values().toArray(new Node[0])[(int)(Math.random() * nodes.values().size())];
            n.setState(State.Susceptible);

        }else{
            this.nodes.get(name).setState(State.Susceptible);
        }
    }

    public synchronized void resistance(String name){
        if (name.equals(Protocol.ALL)){
            for (Node n:nodes.values()){
                n.setState(State.Resistant);
            }
        }else if(name.equals(Protocol.RANDOM)){
            Node n = nodes.values().toArray(new Node[0])[(int)(Math.random() * nodes.values().size())];
            n.setState(State.Resistant);

        }else{
            this.nodes.get(name).setState(State.Resistant);
        }
    }

    public synchronized void immune(String name){
        if (name.equals(Protocol.ALL)){
            for (Node n:nodes.values()){
                n.setState(State.Immune);
            }
        }else if(name.equals(Protocol.RANDOM)){
            Node n = nodes.values().toArray(new Node[0])[(int)(Math.random() * nodes.values().size())];
            n.setState(State.Immune);

        }else{
            this.nodes.get(name).setState(State.Immune);
        }
    }

    public synchronized void kill(String name){
        if (name.equals(Protocol.ALL)){
            for (Node n:nodes.values()){
                n.setState(State.Dead);
            }
        }else if(name.equals(Protocol.RANDOM)){
            Node n = nodes.values().toArray(new Node[0])[(int)(Math.random() * nodes.values().size())];
            n.setState(State.Dead);

        }else{
            this.nodes.get(name).setState(State.Dead);
        }

    }

    public synchronized void connectAll(){
        for (Node n:nodes.values()){
            for (Node n2: nodes.values()){
                if ( n!=n2){
                    n.connect(n2.getName(), n2);
                }
            }
        }
        super.setChanged();
        super.notifyObservers();
    }

    public synchronized void removeAllConnections(){
        for (Node n:nodes.values()){
            n.clearConnections();
        }
        super.setChanged();
        super.notifyObservers();
    }

    public synchronized void connect(String name1, String name2){
        this.nodes.get(name1).connect(name2, this.nodes.get(name2));
        super.setChanged();
        super.notifyObservers();
    }

    public void move(String name, int x, int y){
        this.nodes.get(name).setXY(x,y);
        super.setChanged();
        super.notifyObservers();
    }
    public void clear(){
        this.nodes = new HashMap<>();
        super.setChanged();
        super.notifyObservers();
    }

    public void setDim(int width, int height) {
        this.width = width;
        this.height = height;

    }

    public void zoom(double scale) {
        if (scale > 0) {

            for (Node n: this.nodes.values()){
                n.setXY((int)(n.getX() * scale), (int)(n.getY() * scale));
            }
            this.width = (int)(this.width * scale);
            this.height = (int)(this.height * scale);
        }else{
            scale = Math.abs(scale);
            for (Node n: this.nodes.values()){
                n.setXY((int)(n.getX() / scale), (int)(n.getY() / scale));
            }
            this.width = (int)(this.width / scale);
            this.height = (int)(this.height / scale);

        }
        super.setChanged();
        super.notifyObservers();
    }

    public void update(){
        for (Node n: this.nodes.values()){
            n.updateState(this.death);
        }
        super.setChanged();
        super.notifyObservers();
    }

    public synchronized void runOnce(){
        for (Node n: this.nodes.values()){
            n.spreadInfection(.5, maxTime, minTime);
        }
        for (Node n: this.nodes.values()){
            n.updateState(this.death);
        }
        super.setChanged();
        super.notifyObservers();
    }

    public synchronized void create(String name){
        this.nodes.put(name, new Node(name));
        super.setChanged();
        super.notifyObservers();
    }

    public synchronized void run(int timeStep){
        this.On = true;
        NetworkRunner runner = new NetworkRunner(this, timeStep);
        runner.start();
    }

    public synchronized void stop(){
        this.On = false;
    }

    public synchronized  void delete(String name){
        this.nodes.remove(name);
        super.setChanged();
        super.notifyObservers();
    }

    public void close(){
        stop();
        this.Active = false;
        super.setChanged();
        super.notifyObservers();
    }

    public void error(){
        stop();
        this.Active = false;
        super.setChanged();
        super.notifyObservers();
    }

    public boolean isText() {
        return Text;
    }

    public void setText(boolean text) {
        Text = text;
        super.setChanged();
        super.notifyObservers();

    }

    public boolean isConnections() {
        return Connections;
    }

    public void setConnections(boolean connections) {
        Connections = connections;
        super.setChanged();
        super.notifyObservers();
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
        super.setChanged();
        super.notifyObservers();
    }

    public synchronized void runForceOnce(double push, double pull, int wiggle){
        for (Node n: nodes.values()){
            n.forceMove( nodes.values(), push, pull);
        }

        scaledUpdate(5, wiggle);
//        for (Node n: nodes.values()){
//            n.updateLocation();
//        }

//        for (Node n: nodes.values()){
//            System.out.println(n);
//        }
        resize();
        super.setChanged();
        super.notifyObservers();
    }

    public synchronized void runForce(int timeStep, double push, double pull, int wiggle){
        this.Force = true;
        ForceRunner runner = new ForceRunner(this, timeStep, push, pull, wiggle);
        runner.start();
    }

    public boolean isForce() {
        return Force;
    }

    public synchronized void stopForce(){
        Force = false;
    }

    public void resize(){
        int xMin = ((Node)this.nodes.values().toArray()[0]).getX();
        int xMax = xMin;
        int yMin = ((Node)this.nodes.values().toArray()[0]).getY();
        int yMax = yMin;

        for (Node n : this.nodes.values()){
            if (xMin > n.getX()){
                xMin = n.getX();
            }else if (xMax < n.getX()) {
                xMax = n.getX();
            }
            if (yMin > n.getY()){
                yMin = n.getY();
            }else if (yMax < n.getY()) {
                yMax = n.getY();
            }
        }
//        System.out.println(xMin + "-" + xMax + " " + yMin + "-" + yMax);

        for (Node n: this.nodes.values()){
            n.setXY(((n.getX() - xMin) * (this.width - 2 * margin) / (xMax - xMin)) + margin, ((n.getY()  - yMin) * (this.height - 2 * margin)/ (yMax - yMin)) + margin);

        }
        super.setChanged();
        super.notifyObservers();
    }

    public void scaledUpdate(int Delta, int wiggle){
        int xMax = 0;
        int yMax = 0;
        for (Node n:this.nodes.values()){
            xMax = (int)Math.max(xMax, n.getFxdiff());
            yMax = (int)Math.max(yMax, n.getFydiff());
        }
        for (Node n :this.nodes.values()){
            n.scaledUpdateLocation(xMax, yMax, Delta, wiggle);
        }
    }
}

