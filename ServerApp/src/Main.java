import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import jdk.jshell.JShell;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.net.ServerSocket;
import java.util.Iterator;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class Main {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;

    public static ArrayList<clientThread> clients = new ArrayList<clientThread>();

    public static List<String> SubList =new ArrayList<String>();
    public static List<Integer> CNo =new ArrayList<Integer>();
    public static int clientNum = 1;
    public static void main(String[] args) {

        // The default port number.
        int portNumber = 12345;

        System.out.println("No port specified by user.\nServer is running using default port number=" + portNumber);
        /*
         * Open a server socket on the portNumber (default 1234).
         */
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println("Server Socket cannot be created");
        }

        /*
         * Create a client socket for each connection and pass it to a new client
         * thread.
         */


        while (true) {
            try {

                clientSocket = serverSocket.accept();
                clientThread curr_client =  new clientThread(clientSocket, clients);
                clients.add(curr_client);
                curr_client.start();
                System.out.println("Client "  + clientNum + " is connected!");
                clientNum++;

            } catch (IOException e) {

                System.out.println("Client could not be connected");
            }


        }

    }
}

/*
 * This client thread class handles individual clients in their respective threads
 * by opening a separate input and output streams.
 */
class clientThread extends Thread {

    public static int when = 40;
    private String clientName = null;
    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;
    private Socket clientSocket = null;
    private final ArrayList<clientThread> clients;

    public static boolean chsub = false;
    public clientThread(Socket clientSocket, ArrayList<clientThread> clients) {

        this.clientSocket = clientSocket;
        this.clients = clients;

    }


    public void run() {

        ArrayList<clientThread> clients = this.clients;

        try {
            /*
             * Create input and output streams for this client.
             */
            is = new ObjectInputStream(clientSocket.getInputStream());
            os = new ObjectOutputStream(clientSocket.getOutputStream());

            String name;
            while (true) {

                synchronized(this)
                {
                    this.os.writeObject("Connection Successfully Initiated with Server......" +
                            "\n\nPlease Enter Your Name As Identity :: ");
                    this.os.flush();
                    name = ((String) this.is.readObject()).trim();

                    if ((name.indexOf('@') == -1) || (name.indexOf('!') == -1)) {
                        break;
                    } else {
                        this.os.writeObject("Username should not contain '@' or '!' characters.");
                        this.os.flush();
                    }
                }
            }

            /* Welcome the new the client. */

            System.out.println("Client Name is " + name);

            this.os.writeObject("***************** Welcome " + name + " to our chat room *****************");
            this.os.flush();


            /* Start the conversation. */

            while (true) {

                this.os.writeObject("Please Select Action to Perform:\n" +
                        "1. Open\n2. Publish\n3. Subscribe" +
                        "\n4. Unsubscribe\n5. Get\n6. Quit");
                this.os.flush();

                String line = (String) is.readObject();



                /*
                An Open request establishes the client's identity and creates a channel by that name if
                it does not already exist. The request also subscribes the client to its own channel.
                The request always succeeds.
                 */


                if(line.startsWith("1")){
                    System.out.println("Task 1 Request Recieved");


                    JSONObject data = (JSONObject) is.readObject();
                    System.out.println(data);


                    String subscriptionName = (String) data.get("identity");
                    System.out.println(subscriptionName);


                    FileWriter filew = null;

                    try {
                        // Constructs a FileWriter given a file name, using the platform's default charset

                        File file =new File(subscriptionName+".json");
                        if (!file.exists()){
                            filew = new FileWriter(file);
                            System.out.println("Data written successfully");
                            filew.flush();
                            filew.close();
                            Main.SubList.add(subscriptionName);
                            chsub = true;
                            Main.CNo.add(Main.clientNum);
                            JSONObject obj = new JSONObject();
                            obj.put("_class", "SuccessResponse");
                            this.os.writeObject(obj.toJSONString());
                            os.flush();


                        }else {
                            this.os.writeObject("\nYou have already created your channel!\n\n");
                            this.os.flush();
                            //String line2 = (String) is.readObject();
                            System.out.println("Next command received");

                            JSONObject obj = new JSONObject();
                            obj.put("_class", "ErrorResponse");
                            obj.put("error", "INVALID REQUEST: [{]");
                            this.os.writeObject(obj.toJSONString());
                            os.flush();

                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }


                /*
                A Publish request publishes a message on the client's channel. The timestamp of the message is ignored;
                 the server will issue its own timestamp
                for the message when storing it. The request fails if the channel does not exist or if the message is too big
                 */


                else if (line.startsWith("2")){
                    System.out.println("Task 2 Request Recieved");


                    JSONObject data = (JSONObject) is.readObject();
                    data.put("when",Integer.toString(when));
                    System.out.println(data);
                    when+=3;

                    String subscriptionName = (String) data.get("identity");

                    System.out.println(subscriptionName);



                    FileWriter filew = null;

                    try {


                        File file =new File(subscriptionName+".json");
                        if (!file.exists()){

                            JSONObject obj = new JSONObject();
                            obj.put("_class", "ErrorResponse");
                            obj.put("error", "NO SUCH CHANNEL "+ subscriptionName);
                            this.os.writeObject(obj.toJSONString());
                            os.flush();

                            //String line2 = (String) is.readObject();
                            System.out.println("Next command received");

                        }else {

                            try {
                                filew = new FileWriter(subscriptionName+".json", true);
                                filew.write(data.toJSONString());
                                filew.close();

                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            JSONObject obj = new JSONObject();
                            obj.put("_class", "SuccessResponse");
                            this.os.writeObject(obj.toJSONString());
                            os.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }


                /*
                A Subscribe request subscribes the client to the named channel.
                The request fails if the channel does not exist.
                 */

                else if (line.startsWith("3")){
                    System.out.println("Task 3 Request Recieved");
                    System.out.println(line);
//                    this.os.writeObject("Wait for Responce of task3.....");
//                    this.os.flush();

                    JSONObject data = (JSONObject) is.readObject();
                    System.out.println(data);

                    String subscriptionName = (String) data.get("channel");
                    System.out.println(subscriptionName);

                    File file =new File(subscriptionName+".json");
                    if (!file.exists()){
                        JSONObject obj = new JSONObject();
                        obj.put("_class", "ErrorResponse");
                        obj.put("error", "NO SUCH CHANNEL "+ subscriptionName);
                        this.os.writeObject(obj.toJSONString());
                        os.flush();
                    }else{


                        Main.SubList.add(subscriptionName);
                        Main.CNo.add(Main.clientNum);
                        chsub = true;
                        JSONObject obj = new JSONObject();
                        obj.put("_class", "SuccessResponse");
                        this.os.writeObject(obj.toJSONString());
                        os.flush();

                    }

                }

                /*
                An Unsubscribe request unsubscribes the client from the named channel.
                 The request fails if the channel does not exist.
                 */

                else if (line.startsWith("4")){
                    System.out.println("Task 4 Request Recieved");
                    System.out.println(line);
//                    this.os.writeObject("Wait for Responce of task3.....");
//                    this.os.flush();

                    JSONObject data = (JSONObject) is.readObject();
                    System.out.println(data);

                    String subscriptionName = (String) data.get("channel");
                    System.out.println(subscriptionName);

                    Main.SubList.remove(subscriptionName);

                    JSONObject obj = new JSONObject();
                    obj.put("_class", "SuccessResponse");
                    this.os.writeObject(obj.toJSONString());
                    os.flush();


                }

                /*
                A Get request retrieves all messages that were published on any channel to
                 which the client is currently subscribed, in the order in which they were published.
                The field after specifies a timestamp, and the server will only retrieve messages that were
                published strictly after that timestamp. If the timestamp is 0 the server will retrieve all
                messages from the subscribed channels. The request never fails, but it may return an empty list
                of messages.

                 */

                else if (line.startsWith("5")){

                    String jsonc = "";
                    System.out.println("Task 5 Request Recieved");
                    System.out.println(line);
//                    this.os.writeObject("Wait for Responce of task3.....");
//                    this.os.flush();

                    JSONObject data = (JSONObject) is.readObject();
                    System.out.println(data);

                    String subscriptionName = (String) data.get("identity");
                    System.out.println(subscriptionName);

                    if(Main.SubList.isEmpty()){
                        JSONObject obj = new JSONObject();
                        obj.put("_class", "ErrorResponse");
                        obj.put("error", "NO SUCH CHANNEL : "+subscriptionName);

                        this.os.writeObject(obj.toJSONString());
                        os.flush();
                    }else{
                        JSONObject obj = new JSONObject();
                        obj.put("_class", "MessageListResponse");
                        for(String msg:Main.SubList) {

                            if (checkSub()) {

                                System.out.println(msg);
                                BufferedReader reader = new BufferedReader(new FileReader(msg + ".json"));

                                try {
                                    StringBuilder sb = new StringBuilder();
                                    String l = reader.readLine();

                                    while (l != null) {
                                        sb.append(l);
                                        l = reader.readLine();
                                    }
                                    jsonc = sb.toString();
                                    obj.put("messages", jsonc);
                                } finally {
                                    reader.close();
                                }
                            System.out.println(jsonc);

                        }else{
                                JSONObject obj1 = new JSONObject();
                                obj.put("_class", "ErrorResponse");
                                obj.put("error", "NO SUCH CHANNEL : "+subscriptionName);

                                this.os.writeObject(obj1.toJSONString());
                                os.flush();
                            }
                        }



                        this.os.writeObject(obj.toJSONString());
                        os.flush();
                    }



                }

                if (line.startsWith("/quit")) {

                    break;
                }

                else
                {
                    System.out.println(line);
                }

            }

            /* Terminate the Session for a particluar user */

            this.os.writeObject("*** Bye " + name + " ***");
            this.os.flush();
            System.out.println(name + " disconnected.");
            clients.remove(this);


            synchronized(this) {

                if (!clients.isEmpty()) {

                    for (clientThread curr_client : clients) {


                        if (curr_client != null && curr_client != this && curr_client.clientName != null) {
                            curr_client.os.writeObject("*** The user " + name + " disconnected ***");
                            curr_client.os.flush();
                        }


                    }
                }
            }


            this.is.close();
            this.os.close();
            clientSocket.close();

        } catch (IOException e) {

            System.out.println("User Session terminated");

        } catch (ClassNotFoundException e) {

            System.out.println("Class Not Found");
        }
    }

    public static boolean checkSub(){
        return chsub;
    }

}