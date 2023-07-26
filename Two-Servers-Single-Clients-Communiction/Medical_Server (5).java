 package medical_server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Medical_Server {

  
    public static void main(String[] args) throws IOException {

        DataInputStream fromClient;
        int port = 5016;

        String msg1, msg2, msg3, msg4, msg5, msg6;

        ///create server socket for with the port number 5000 
        ServerSocket serverSocket = new ServerSocket(port);

        //create a socket  
        Socket socket;// 

        String msgT = null;
        String msgH = null;
        String msgO = null;
        double temperature = 0.0;
        double heartRate = 0;
        double oxygen = 0;

        int num = 0;
        //persistent TCP connection 
        do {

            //Socket object to accept and listen for the connection of the client 
            socket = serverSocket.accept(); // connection established  

            //Connection is established is printed 
            System.out.println("medical server client is connected................................\n");

            InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
            fromClient = new DataInputStream(socket.getInputStream());
            //Data stream to convert stream to data, so server can read it 

            String s = "hi";
            if (s.equalsIgnoreCase("stop")) {
                break; //break from the inner loop 
            }

            
            do {


                // Receiving Temperature data 

                msg1 = fromClient.readUTF(); //Message data
                msg2 = fromClient.readUTF(); // Actual value of temperature


                
                if (msg1 != null) {
                    msgT = msg1;
                    temperature = Double.valueOf(msg2);
                }else{
                    temperature = Double.valueOf(msg2);
                }

                // Receivig Heart Rate data 


                msg3 = fromClient.readUTF(); // Message
                msg4 = fromClient.readUTF(); // actual value 


                
                if (msg3 != null) {
                    msgH = msg3;
                    heartRate = Double.valueOf(msg4);
                }else{
                    heartRate = Double.valueOf(msg4);
                }


                // Receiving Oxygen data


                msg5 = fromClient.readUTF();  // message data
                msg6 = fromClient.readUTF(); // Actual value


                
                if (msg5 != null) {
                    msgO = msg5;
                    oxygen = Double.valueOf(msg6);
                }else{
                    oxygen = Double.valueOf(msg6);
                }
            


  
                // Testing for Action

                if ((temperature > 39.0) && (heartRate > 100.0) && (oxygen < 95.0)) {
                    System.out.println(msgT);
                    System.out.println(msgH);
                    System.out.println(msgO);
                    System.out.println("ACTION: Send an ambulance to the patient!");
                } else if (((temperature >= 38.0) && (temperature <= 38.9)) && ((heartRate >= 95.0) && (heartRate <= 98.0)) && (oxygen < 80.0)) {
                    System.out.println(msgT);
                    System.out.println(msgH);
                    System.out.println(msgO);
                    System.out.println("ACTION: Call the patient’s family!");
                }else{

                    System.out.println(msgT);
                    System.out.println(msgH);
                    System.out.println(msgO);

                    System.out.println("Warning, advise patient to make a checkup appointment!");
                    }
                
                

           
             
                System.out.println("--------------------");

                num++;
                if(num > 5){
                    break;
                }
       
            } while (true);
            
     
        } while (true);

         socket.close(); 
  
    }

}

    