package JustCleint;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
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
public class NetworkServer {

    /**
     * Turn on if standard output debug messages are desired.
     */
    private static final boolean DEBUG = false;

    private ServerSocket server;
    /**
     * Print method that does something only if DEBUG is true
     */
    private static void dPrint( Object logMsg ) {
        if ( NetworkServer.DEBUG ) {
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
     * @param port     the port of the server socket on which the server is
     *                 listening
     * @param model    the local object holding the state of the network that
     *                 must be updated upon receiving server message
     */
    public NetworkServer(int port, Network model){
        try {
            this.server = new ServerSocket(port);
            System.out.println("Waiting For Controller to Connect");
            this.sock = server.accept();
            System.out.println("Controller Connected");
            this.networkIn = new Scanner( sock.getInputStream() );
            this.networkOut = new PrintStream( sock.getOutputStream() );
            this.network = model;
            this.go = true;

            // Block waiting for the CONNECT message from the server.
            String request = this.networkIn.next();
            String arguments = this.networkIn.nextLine();
            assert request.equals( Protocol.INITIAL_CONNECT ) :
                    "CONNECT not 1st";
            NetworkServer.dPrint( "Connected to server " + this.sock );
            this.initialConnect( arguments );

            // Run rest of client in separate thread.
            // This threads stops on its own at the end of the network and
            // does not need to rendez-vous with other software components.
            Thread netThread = new Thread( () -> this.run() );
            netThread.start();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void initialConnect(String arguments ){

        String fields[] = arguments.trim().split( " " );
        int rows = Integer.parseInt( fields[ 0 ] );
        int cols = Integer.parseInt( fields[ 1 ] );

        this.network.setDim( rows, cols );
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
        System.out.println(":{");
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
        NetworkServer.dPrint( '!' + Protocol.ERROR + ',' + arguments );
        dPrint( "Fatal error: " + arguments );
        this.network.error();
        this.stop();
    }

    public void zoom( String arguments ) {
        this.network.zoom(Double.parseDouble(arguments.split(" ")[0]));
    }

    public void connectionsOnOff( ) {
        this.network.setConnections(!this.network.isConnections());
    }
    public void textOnOff() {
        this.network.setText(!this.network.isText());
    }

    /**
     * This method should be called at the end of the network to
     * close the client connection.
     */
    public void close() {
        try {
            this.sock.close();
        }
        catch( IOException ioe ) {
            // squash
        }
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



    /**
     * Run the main client loop. Intended to be started as a separate
     * thread internally. This method is made private so that no one
     * outside will call it or try to start a thread on it.
     */
    private void run() {

        while ( this.goodToGo() ) {
            try {
                String request = this.networkIn.next();
                String arguments = this.networkIn.nextLine().trim();
                NetworkServer.dPrint( "Net message in = \"" + request + '"' );
                System.out.println(request);
                switch ( request ) {
                    case Protocol.INITIAL_CONNECT:
                        // This should not happen because NetworkServer
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
                this.error( e.getMessage() + '?' );
                this.stop();
            }
        }
        this.close();
    }

}
