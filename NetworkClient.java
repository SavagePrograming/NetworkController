import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * The client side network interface to a Reversi network server.
 * Each of the two players in a network gets its own connection to the server.
 * This class represents the controller part of a model-view-controller
 * triumvirate, in that part of its purpose is to forward user actions
 * to the remote server.
 *
 * @author Robert St Jacques @ RIT SE
 * @author Sean Strout @ RIT CS
 * @author James Heliotis @ RIT CS
 */
public class NetworkClient {

    final int HEIGHT = 200;
    final int WIDTH = 200;
    /**
     * Turn on if standard output debug messages are desired.
     */
    private static final boolean DEBUG = false;

    /**
     * Print method that does something only if DEBUG is true
     */
    private static void dPrint( Object logMsg ) {
        if ( NetworkClient.DEBUG ) {
            System.out.println( logMsg );
        }
    }

    /**
     * The {@link Socket} used to communicate with the reversi server.
     */
    private Socket sock;

    /**
     * The {@link Scanner} used to read requests from the reversi server.
     */
    private Scanner networkIn;

    /**
     * The {@link PrintStream} used to write responses to the reversi server.
     */
    private PrintStream networkOut;

    /**
     * The {@link Network} used to keep track of the state of the network.
     */
    private Network network;

    /**
     * Sentinel used to control the main network loop.
     */
    private boolean go;

    /**
     * Accessor that takes multithreaded access into account
     */
    private synchronized boolean goodToGo() {
        return this.go;
    }

    /**
     * Multithread-safe mutator
     */
    private synchronized void stop() {
        this.go = false;
    }

    /**
     * Hook up with a Reversi network server already running and waiting for
     * two players to initialConnect. Because of the nature of the server
     * protocol, this constructor actually blocks waiting for the first
     * message from the server that tells it how big the board will be.
     * Afterwards a thread that listens for server messages and forwards
     * them to the network object is started.
     *
     * @param hostname the name of the host running the server program
     * @param port     the port of the server socket on which the server is
     *                 listening
     *                 must be updated upon receiving server message
     */
    public NetworkClient(String hostname, int port) {
        try {
            this.sock = new Socket(hostname, port);
            this.networkIn = new Scanner(sock.getInputStream());
            this.networkOut = new PrintStream(sock.getOutputStream());
            initalConnect();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public NetworkClient(String hostname, int port, int width, int height) {
        try {
            this.sock = new Socket(hostname, port);
            this.networkIn = new Scanner(sock.getInputStream());
            this.networkOut = new PrintStream(sock.getOutputStream());
            initalConnect(width, height);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initalConnect(){
        this.networkOut.println(Protocol.INITIAL_CONNECT + " " + WIDTH + " " + HEIGHT);
    }

    public void initalConnect(int width, int height){
        this.networkOut.println(Protocol.INITIAL_CONNECT + " " + width + " " + height);
    }

    public void move(String name, int x, int y){
        this.networkOut.println(Protocol.MOVE + " " + name + " " + x + " " + y);
    }

    public void connect(String name1, String name2){
        this.networkOut.println(Protocol.CONNECT + " " + name1  + " " + name2);
    }

    public void create(String name1){
        this.networkOut.println(Protocol.CREATE + " " + name1);
    }

    public void delete(String name1){
        this.networkOut.println(Protocol.DELETE + " " + name1);
    }

    public void error(String message){
        this.networkOut.println(Protocol.ERROR + " " + message);
    }

    public void exit(String message){
        this.networkOut.println(Protocol.EXIT);
    }

    public void change(State s, String name ){
        switch(s) {
            case Susceptible:
                this.networkOut.println(Protocol.CHANGE + " " + Protocol.SUSCEPTIBLE + " " + name);
                break;
            case Infected:
                this.networkOut.println(Protocol.CHANGE + " " + Protocol.INFECTED + " " + name);
                break;
            case Dead:
                this.networkOut.println(Protocol.CHANGE + " " + Protocol.DEAD + " " + name);
                break;
            case Immune:
                this.networkOut.println(Protocol.CHANGE + " " + Protocol.IMMUNE + " " + name);
                break;
            case Resistant:
                this.networkOut.println(Protocol.CHANGE + " " + Protocol.RESISTANCE + " " + name);
                break;
        }
    }

    public void step(){
        this.networkOut.println(Protocol.STEP);
    }

    public void start(int time){
        this.networkOut.println(Protocol.START + " " + time);
    }

    public void pause(){
        this.networkOut.println(Protocol.STOP);
    }

    public void onOff(int time){
        this.networkOut.println(Protocol.ONOFF+ " " + time);
    }
}
