
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Scanner;

import org.json.simple.JSONObject;



public class Main implements Runnable{

    // Some Important Variables
    private static Socket clientSocket = null;
    private static ObjectOutputStream os = null;
    private static ObjectInputStream is = null;
    private static BufferedReader inputLine = null;
    private static BufferedInputStream bis = null;
    private static boolean closed = false;
    public static Scanner useAction = new Scanner(System.in);
    public static String userName;
    public static int act;
    public static String action;
    public static void main(String[] args) {

        // The default port.
        int portNumber = 12345;
        // The default host.
        String host = "localhost";


        /*
         * Open a socket on a given host and port. Open input and output streams.
         */
        try {
            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new ObjectOutputStream(clientSocket.getOutputStream());
            is = new ObjectInputStream(clientSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Unknown " + host);
        } catch (IOException e) {
            System.err.println("No Server found. Please ensure that the Server program is running and try again.");
        }


        /*
         * If everything has been initialized then we want to write some data to the
         * socket we have opened a connection to on the port portNumber.
         */
        if (clientSocket != null && os != null && is != null) {
            try {

                /* Create a thread to read from the server. */
                new Thread(new Main()).start();
                //System.out.println("Enter username");
                userName = (String) inputLine.readLine().trim();
                os.writeObject(userName);
                os.flush();
                while (!closed) {

                    action = useAction.nextLine();

                    act = Integer.parseInt(action);



                    /* Check the input for private messages or files */
//                    do {
//
//
//
//                        if(act > 5 && act < 0){
//                            System.out.println("Wrong choice!\nPlease Enter Valid Option\n");
//                            chk = true;
//                        }
//                    }while(chk);


                    os.writeObject(action);
                    os.flush();
                    if (act == 1){
                        JSONObject obj = new JSONObject();
                        obj.put("_class", "OpenRequest");
                        obj.put("identity", userName);
                        os.writeObject(obj);
                        os.flush();


                    } else if(act ==2 ){
                        JSONObject obj = new JSONObject();
                        obj.put("_class", "PublishRequest");
                        obj.put("identity", userName);
                        obj.put("message", makeMessage(userName));
                        os.writeObject(obj);
                        os.flush();

                    }else if(act == 3){
                        JSONObject obj = new JSONObject();
                        obj.put("_class", "SubscribeRequest");
                        obj.put("identity", userName);
                        obj.put("channel", getChannel());
                        os.writeObject(obj);
                        os.flush();
                    }else if(act == 4){
                        JSONObject obj = new JSONObject();
                        obj.put("_class", "UnsubscribeRequest");
                        obj.put("identity", userName);
                        obj.put("channel", getChannel());
                        os.writeObject(obj);
                        os.flush();

                    }else if(act == 5){
                        JSONObject obj = new JSONObject();
                        obj.put("_class", "GetRequest");
                        obj.put("identity", userName);
                        obj.put("after", getAfter());
                        os.writeObject(obj);
                        os.flush();
                    }

                    else if(act == 6){
                        /*
                         * Close all the open streams and socket.
                         */
                        os.close();
                        is.close();
                        clientSocket.close();

                    }


                    else{
                        System.out.println("wrong choice");
                    }
                }
                /*
                 * Close all the open streams and socket.
                 */
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e)
            {
                System.err.println("IOException:  " + e);
            }

        }
    }

    public static String getAfter(){
        Scanner after = new Scanner(System.in);
        System.out.println("Please Enter After time ::\n");
        String userAfter = after.nextLine();
        return userAfter;
    }
    public static String getChannel(){
        Scanner channel = new Scanner(System.in);
        System.out.println("Please Enter Channel Name ::\n");
        String userchn = channel.nextLine();
        return userchn;
    }
    public static JSONObject makeMessage(String user){
        // JSON Stuff
        JSONObject obj = new JSONObject();

        obj.put("_class", "Message");
        obj.put("from", user);
        Scanner msg = new Scanner(System.in);
        System.out.println("Please Enter Your Message ::\n");
        String userMsg = msg.nextLine();
        if(userMsg.length() > 1234){
            JSONObject obj1 = new JSONObject();

            obj1.put("_class", "ErrorResponse");
            obj1.put("error", "MESSAGE TOO BIG: 1234 characters");
            System.out.println(obj1);
            System.out.println("Please Enter Your Message ::\n");
            userMsg = msg.nextLine();
        }
        obj.put("body", userMsg);

        return obj;
    }

    /*
     * Create a thread to read from the server.
     */

    @Override
    public void run() {

        /*
         * Keep on reading from the socket till we receive "Bye" from the
         * server. Once we received that then we want to break.
         */
        String responseLine;

        try {

            while ((responseLine = (String) is.readObject()) != null)  {

                if (responseLine.equals("Directory Created"))
                {

                }
                /* Condition for Checking for incoming messages */

                else
                {
                    responseLine = responseLine.replace("\\", "");
                    System.out.println(responseLine);
                }
                /* Condition for quitting application */

                if (responseLine.indexOf("*** Bye") != -1)

                    break;
            }

            closed = true;
            System.exit(0);

        } catch (IOException | ClassNotFoundException e) {

            System.err.println("Server Process Stopped Unexpectedly!!");

        }

    }
}