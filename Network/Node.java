package Network;

import sun.nio.cs.ext.MacThai;

import java.util.Collection;
import java.util.HashMap;

public class Node{
    private HashMap<String, Node> nodes;
    private String name;
    private int x;
    private int y;
    private double fx;
    private double fy;
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

    public double getDistance(Node n){
        return Math.sqrt(Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2));
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

    public void updateState(boolean Death){
        if (this.state == State.Infected) {
            this.timeCounter--;
            if (this.timeCounter <= 0) {
                if (Death){
                    this.state = State.Dead;
                }else{
                    this.state = State.Susceptible;
                }
                this.timeCounter = 0;
            }
        }
        if (this.future != null){
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

    public void forceMove(Collection<Node> nodes, double push, double pull){

        this.fx = 0;
        this.fy = 0;
        for (Node n:nodes){
            if (!n.equals(this)) {
                if (!(Math.abs(n.x - this.x) == 0)){
                    this.fx -= (push / ((Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2))) * ((n.x - this.x)/ Math.sqrt(Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2)))); // / nodes.size()
                }
                if (!(Math.abs(n.y - this.y) == 0)){
                    this.fy -= (push / ( (Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2))) * ( (n.y - this.y) / Math.sqrt(Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2))));// nodes.size();
                }
//                if (!(Math.abs(n.x - this.x) == 0)) {
//                    System.out.print("Push (X: " + this.x + " " + n.x + " " + (-push / ((Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2))) * ((n.x - this.x)/ Math.sqrt(Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2)))));
//                }else{
//                    System.out.print("Push (X: Zeroed");
//                }
//                if (!(Math.abs(n.y - this.y) == 0)) {
//                    System.out.println( " , Y: "+ this.y + " " + n.y + " " + (-push / ( (Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2))) * ( (n.y - this.y) / Math.sqrt(Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2)))) + ")");
//                }else{
//                    System.out.println(" , Y: Zeroed)");
//                }
//                System.out.println("Push " + this.fx + " " + this.fx);
            }
        }
        for (Node n:this.nodes.values()){
            if (n != this) {
                if (!(Math.abs(n.x - this.x) == 0)){
                    this.fx += pull * (Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2)) * ((n.x - this.x) / Math.sqrt((Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2))));// this.nodes.size();
                }
                if (!(Math.abs(n.y - this.y) == 0)){
                    this.fy += pull * (Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2)) * ( (n.y - this.y) / Math.sqrt((Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2)))) ;// this.nodes.size();
                }
//                if (!(Math.abs(n.x - this.x) == 0)) {
//                    System.out.print("Pull (X: " + this.x + " " + n.x + " " + pull * (Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2)) * ((n.x - this.x) / Math.sqrt((Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2)))));
//                }else{
//                    System.out.print("Pull (X: Zeroed");
//                }
//                if (!(Math.abs(n.y - this.y) == 0)) {
//                    System.out.println( " , Y: "+ this.y + " " + n.y + " " + pull * (Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2)) * ( (n.y - this.y) / Math.sqrt((Math.pow((n.x - this.x), 2) + Math.pow((n.y - this.y), 2))))+ ")");
//                }else{
//                    System.out.println( " , Y: Zeroed)");
//                }
            }
        }
    }
    public void scaledUpdateLocation(int xMax, int yMax, int delta,  int wiggle){
//        System.out.println((int) (delta * this.fx / (double)xMax) + " " + (int) (delta * this.fx / (double)xMax + Math.random() * 2 * wiggle - wiggle));
//        System.out.println((int) (delta * this.fy / (double)yMax) + " " + (int) (delta * this.fx / (double)xMax + Math.random() * 2 * wiggle - wiggle));
        this.x += (int) (delta * this.fx / (double)xMax + Math.random() * 2 * wiggle - wiggle);
        this.y += (int) (delta * this.fy / (double)yMax + Math.random() * 2 * wiggle - wiggle);
    }

    public double getFxdiff(){
        return Math.abs(this.fx);
    }

    public double getFydiff(){
        return Math.abs(this.fy);
    }

    public void updateLocation(){
        this.x += (int)this.fx;
        this.y += (int)this.fy;
    }

    public double getFx() {
        return fx;
    }

    public double getFy() {
        return fy;
    }
}



