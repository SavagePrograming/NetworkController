package JustCleint;

import Network.Network;
import Network.Protocol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;


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
public class Controller {
    /**
     * The {@link Network} used to keep track of the state of the network.
     */
    private Network network;


    private boolean go;

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

     *                 listening
     * @param model    the local object holding the state of the network that
     *                 must be updated upon receiving server message
     */
    public Controller(int width, int height, Network model, Map<String, String> params){
        this.network = model;
        this.initialConnect(width, height);
    }

    public void initialConnect(int width, int height){
        this.network.setDim( width, height );
    }

    public void initialConnect(String arguments ){

        String fields[] = arguments.trim().split( " " );
        int rows = Integer.parseInt( fields[ 0 ] );
        int cols = Integer.parseInt( fields[ 1 ] );

        this.network.setDim( rows, cols );
    }

    public void force(String arguments){
        String[] args = arguments.split(" ");
        double push = Double.parseDouble(args[0]);
        double pull = Double.parseDouble(args[1]);
        int wiggle = Integer.parseInt(args[2]);
        System.out.println(push + " " + pull);
        this.network.runForceOnce(push, pull, wiggle);
    }

    public void startforce(String arguments){
        String[] args = arguments.split(" ");
        int time = Integer.parseInt(args[0]);
        double push = Double.parseDouble(args[1]);
        double pull = Double.parseDouble(args[2]);
        int wiggle = Integer.parseInt(args[3]);
        this.network.runForce(time, push, pull, wiggle);
    }

    public void stopforce(){
        this.network.stopForce();
    }

    public void connect(String arguments ){
        String fields[] = arguments.trim().split( " " );
        String name1 =  fields[ 0 ];
        if (name1.equals(Protocol.ALL)){
            this.network.connectAll();
        }else if (name1.equals(Protocol.NONE)){
            this.network.removeAllConnections();
        }else {
            String name2 = fields[1];
            this.network.connect(name1, name2);
        }
    }

    public void clear(){
        this.network.clear();
    }

    public void create(String arguments ){
//        System.out.println(":{");
        String fields[] = arguments.trim().split( " " );
        String name1 =  fields[ 0 ];
        this.network.create(name1);
    }

    public void delete(String arguments ){
        String fields[] = arguments.trim().split( " " );
        String name1 =  fields[ 0 ];
        this.network.delete(name1);
    }

    public void change(String arguments) {
        String[] args = arguments.split(" ");
        String request = args[0];
        String name = args[1];
        System.out.println(request);
        switch (request) {
            case Protocol.DEAD:
                this.network.kill(name);
                break;
            case Protocol.IMMUNE:
                this.network.immune(name);
                break;
            case Protocol.INFECTED:
                System.out.println("Infecting " + name);
                this.network.infect(name);
                break;
            case Protocol.RESISTANCE:
                this.network.resistance(name);
                break;
            case Protocol.SUSCEPTIBLE:
                this.network.suseptable(name);
                break;
        }
        this.network.update();
    }

    public void dead(String arguments){
        String name = arguments.split(" ")[0];
        this.network.kill(name);
        this.network.update();
    }

    public void immune(String arguments){
        String name = arguments.split(" ")[0];
        this.network.immune(name);
        this.network.update();
    }

    public void infect(String arguments){
        String name = arguments.split(" ")[0];
        this.network.infect(name);
        this.network.update();
    }

    public void resistance(String arguments){
        String name = arguments.split(" ")[0];
        this.network.resistance(name);
        this.network.update();
    }

    public void suseptable(String arguments){
        String name = arguments.split(" ")[0];
        this.network.suseptable(name);
        this.network.update();
    }

    /**
     * Called when the server sends a message saying that
     * gameplay is damaged. Ends the network.
     *
     * @param arguments The error message sent from the reversi.server.
     */
    public void error( String arguments ) {
        this.network.error();
        this.stop();
    }

    public void zoom( String arguments ) {
        this.network.zoom(Double.parseDouble(arguments.split(" ")[0]));
    }

    public void connectionsOnOff( ) {
        this.network.setConnections(!this.network.isConnections());
    }

    public void deathOnOff( ) {
        this.network.setDeath(!this.network.isDeath());
    }

    public void textOnOff() {
        this.network.setText(!this.network.isText());
    }

    /**
     * This method should be called at the end of the network to
     * close the client connection.
     */
    public void close() {
        this.network.close();
    }


    public void start(String arguments){
        int timeStep = Integer.parseInt(arguments.split(" ")[0]);
        this.network.run(timeStep);
    }
    public void step(){
        this.network.runOnce();
    }

    public void move(String arguments){
        String[] args = arguments.split(" ");
        this.network.move(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    public void pause(){
        this.network.stop();
    }

    boolean validate(String input){
        String command = input.split(" ")[0];
        return command.equals(Protocol.CHANGE ) || command.equals(Protocol.MOVE) || command.equals(Protocol.CLEAR) ||
                command.equals(Protocol.INITIAL_CONNECT) || command.equals(Protocol.CONNECT) ||
                command.equals(Protocol.ERROR ) || command.equals(Protocol.CREATE) || command.equals(Protocol.ZOOM) ||
                command.equals(Protocol.EXIT) || command.equals(Protocol.DELETE) || command.equals(Protocol.STEP)||
                command.equals(Protocol.START) || command.equals(Protocol.STOP) || command.equals(Protocol.ONOFF) ||
                command.equals(Protocol.TEXT) || command.equals(Protocol.CONNECTIONS) ||
                command.equals(Protocol.INFECTED) || command.equals(Protocol.DEAD) || command.equals(Protocol.IMMUNE) ||
                command.equals(Protocol.FORCE) || command.equals(Protocol.FORCESTART) || command.equals(Protocol.FORCESTOP) ||
                command.equals(Protocol.SUSCEPTIBLE) || command.equals(Protocol.RESISTANCE) || command.equals(Protocol.DEATH);
    }

    public boolean command(String input){

        if (validate(input)){
//            System.out.println("1:" + input);
            runCommand(input);
            return true;
        }else if (input.split(" ")[0].equals(Protocol.READ)){
            String path = "";
            boolean start = true;
            for (String i: input.split(" ")){
                if (start) {
                    start = false;
                }else{
                    path += i + " ";
                }

            }
            readFromFile(path.substring(0,path.length() - 1));
            return true;
        }else{
            System.out.println("Invalid Command:'" + input + "'");
            return false;
        }
    }

    public void readFromFile(String fileName){
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            ArrayList<String> commands = new ArrayList<>();
            String line;
            while ((line = in.readLine()) != null){
                if (!(line.contains(Protocol.READ) && line.contains(fileName))) commands.add(line);
            }
            for (String command:commands){
                this.command(command);
            }
        } catch (IOException e) {
            System.out.println("File is in Valid: " + fileName);

        }
    }

    /**
     * Run the main client loop. Intended to be started as a separate
     * thread internally. This method is made private so that no one
     * outside will call it or try to start a thread on it.
     */
    private void runCommand(String command) {

//        System.out.println("2:" + command);
        try {
            String[] args = command.split(" ");
            String request = args[0];
            String arguments = "";
            if (args.length > 1) {
//                System.out.println("3:" + request);
                for (int i = 1; i < args.length; i++) {
                    arguments += args[i] + " ";
                }
                arguments = arguments.substring(0, arguments.length() - 1);
            }

//            System.out.println("4:"+request);
//            System.out.println("5:"+request.equals(Protocol.TEXT));

            switch ( request ) {
                case Protocol.INITIAL_CONNECT:
                    // This should not happen because Controller
                    // waits for the CONNECT message in the constructor.
                    assert false : "CONNECT already happened?";
                    initialConnect( arguments );
                    break;
                case Protocol.CONNECT:
                    connect(arguments);
                    break;
                case Protocol.CREATE:
                    create(arguments);
                    break;
                case Protocol.DELETE:
                    delete(arguments);
                    break;
                case Protocol.ERROR:
                    error( arguments );
                    break;
                case Protocol.MOVE:
                    move(arguments);
                    break;
                case Protocol.DEATH:
                    deathOnOff();
                    break;
                case Protocol.EXIT:
                    close();
                    break;
                case Protocol.CHANGE:
                    change(arguments);
                    break;
                case Protocol.STEP:
                    step();
                    break;
                case Protocol.TEXT:
                    System.out.println("TEXT");
                    textOnOff();
                    break;
                case Protocol.CONNECTIONS:
                    connectionsOnOff();
                    break;
                case Protocol.START:
                    start(arguments);
                    break;
                case Protocol.STOP:
                    pause();
                    break;
                case Protocol.DEAD:
                    this.dead(arguments);
                    break;
                case Protocol.FORCE:
                    this.force(arguments);
                    break;
                case Protocol.FORCESTART:
                    this.startforce(arguments);
                    break;
                case Protocol.FORCESTOP:
                    this.stopforce();
                    break;
                case Protocol.IMMUNE:
                    this.immune(arguments);
                    break;
                case Protocol.INFECTED:
                    this.infect(arguments);
                    break;
                case Protocol.RESISTANCE:
                    this.resistance(arguments);
                    break;
                case Protocol.SUSCEPTIBLE:
                    this.suseptable(arguments);
                    break;
                case Protocol.CLEAR:
                    clear();
                    break;
                case Protocol.ZOOM:
                    zoom(arguments);
                    break;
                case Protocol.ONOFF:
                    if (this.network.isOn()){
                        pause();
                    }else {
                        start(arguments);
                    }
                    break;

                default:
                    System.err
                            .println( "Unrecognized request: " + request );
                    this.stop();
                    break;
            }
        }
        catch( NoSuchElementException nse ) {
            // Looks like the connection shut down.
            this.error( "Lost connection to server." );
            this.stop();
        }
        catch( Exception e ) {
            e.printStackTrace();
            this.error( e.getMessage() + '?' );
            this.stop();
        }
    }
}
