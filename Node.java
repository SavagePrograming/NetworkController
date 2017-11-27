import java.util.HashMap;
import java.util.Observable;

enum State{
    Susceptible, Infected, Dead, Immune, Resistant
}

public class Node{
    private HashMap<String, Node> nodes;
    private String name;
    private int x;
    private int y;
    private State state;
    private State future;
    private int timeCounter;
    
    public Node(String name){
        this.name = name;
        this.nodes = new HashMap<>();
        this.state = State.Susceptible;
        x = 100;
        y = 100;
    }

    public State getState() {
        return state;
    }

    public Node(String name, HashMap<String, Node> nodes){
        this.name = name;
        this.nodes = nodes;
        this.state = State.Susceptible;
        x = 100;
        y = 100;
    }

    public Node(String name, HashMap<String, Node> nodes, State state){
        this.name = name;
        this.nodes = nodes;
        this.state = state;
        x = 100;
        y = 100;
    }
    
    public void spreadInfection(double probability, int maxTime, int minTime){
        if (this.state == State.Infected) {
            for (Node n : nodes.values()) {
                if (Math.random() < probability && n.state == State.Susceptible) {
                    n.setState(State.Infected);
                    n.setTimeCounter((int)(Math.random() * (maxTime - minTime) + minTime));
                }
            }
        }
    }

    public void updateState(){
        if (this.state == State.Infected) {
            this.timeCounter--;
            if (this.timeCounter <= 0) {
                this.state = State.Dead;
                this.timeCounter = 0;
            }
        }else if (this.future != null){
            this.state = this.future;
            this.future = null;
        }
    }

    public void clearConnections(){
        this.nodes = new HashMap<>();
    }

    
    public void setState(State state){
        this.future = state;
    }

    public void setTimeCounter(int time){
        this.timeCounter = time;
    }

    public void connect(String name, Node node){
        this.nodes.put(name, node);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public HashMap<String, Node> getNodes() {
        return nodes;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        String str = "{"+ this.name + ", Location:(" + this.x +", " + this.y + "), Children:(";
        for (Node n: nodes.values()) str += n.name + " ,";
        str = str.substring(0, str.length() - 2) + "), State:";
        switch (this.state){
            case Susceptible:
                return str + "Susceptible}";
            case Infected:
                return str + "Infected}";
            case Dead:
                return str + "Dead}";
            case Immune:
                return str + "Immune}";
            case Resistant:
                return str + "Resistant}";
        }
        return str + "Unknown}";
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }
}


