package ServerCleint;

import java.util.HashMap;
import java.util.Observable;


public class Network extends Observable{
    private HashMap<String, Node> nodes;
    private int width;
    private int height;
    private boolean On;
    private boolean Active;
    private boolean Text = true;
    private boolean Connections = true;
    public final int minTime = 5;
    public final int maxTime = 10;

    public final double probability = .5;

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
            n.updateState();
        }
        super.setChanged();
        super.notifyObservers();
    }

    public synchronized void runOnce(){
        for (Node n: this.nodes.values()){
            n.spreadInfection(.5, maxTime, minTime);
        }
        for (Node n: this.nodes.values()){
            n.updateState();
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
}

