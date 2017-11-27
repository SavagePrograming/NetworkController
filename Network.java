import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Observable;


public class Network extends Observable{
    private HashMap<String, Node> nodes;
    private int width;
    private int height;
    boolean On;
    boolean Active;

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
        this.nodes.get(name).setState(State.Infected);
    }

    public synchronized void suseptable(String name){
        this.nodes.get(name).setState(State.Susceptible);
    }

    public synchronized void resistance(String name){
        this.nodes.get(name).setState(State.Resistant);
    }

    public synchronized void immune(String name){
        this.nodes.get(name).setState(State.Immune);
    }

    public synchronized void kill(String name){
        this.nodes.get(name).setState(State.Dead);
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

    public void setDim(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public synchronized void runOnce(){
        for (Node n: this.nodes.values()){
            n.spreadInfection(.5, 10, 5);
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
}

