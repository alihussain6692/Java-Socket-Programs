 package the_personal_server;

import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedWriter;


public class The_Personal_Server {

    public static void main(String[] args) throws IOException {


        // Socket for Sensor Application

        int portNum = 2016;
        DataInputStream Fromclient;
        BufferedWriter bufferedWriter = null;
        Writer outputStream=null;


        //port number of the medical server
        int port = 5016;
        DataOutputStream toServer;
        String host = "localhost";

        //create one client socket and give it the values of the port number and the local IP address 
        Socket client_Socket = new Socket(host, port);// 

        //Convert data to stream, so server can read them 


        toServer = new DataOutputStream(client_Socket.getOutputStream());


        // create Server socket
        ServerSocket socketOfServer = new ServerSocket(portNum);

        SimpleDateFormat dateFormat = new SimpleDateFormat("'at date :'dd MMM dd', time 'HH:mm:ss");
        String date = dateFormat.format(new Date());
        Socket socket;
int num = 0;
        do {

            //accept the connection requist from the cleint 
            socket = socketOfServer.accept();
            System.out.println("Client is connected to the server ");

            //convert stream into data   
            Fromclient = new DataInputStream(socket.getInputStream());

            String t;
            String h;
            String o;

            do {
              
                     // get first chunk(Temperature) from the client input  

                t = Fromclient.readUTF();       
                  
             
                if (t.equals("Close")) {
                    socket.close();
                    break;
                }else{
                    double temperature = Double.valueOf(t);
                     if (temperature >= 36 && temperature <= 38) {
                    System.out.println(date + ",Temperature is Normal  " + temperature);
                    toServer.writeUTF(String.valueOf(""));
                    toServer.writeUTF(String.valueOf(0));
                    
                }
                if (temperature > 38) {
                    String msg = (date + ",Temperature is high  " + temperature);
                    toServer.writeUTF(String.valueOf(msg));
                    toServer.writeUTF(String.valueOf(temperature));
                    System.out.println(msg + ". An alert message is sent to the Medical Server.");
                   
                }
                    
                }
                                     
                

               

                // get Secone( Heart Rate ) input from the client input 


                h = Fromclient.readUTF();
                if (h.equals("Close")) {
                    socket.close();
                    break;
                }else{
                    double heartRate = Double.valueOf(h);

                if (heartRate >= 60 && heartRate <= 100) {
                    System.out.println(date + ",Heart rate is normal " + heartRate);
                    toServer.writeUTF(String.valueOf(""));
                    toServer.writeUTF(String.valueOf(0));
                
                }
                if (heartRate > 100) {
                    String msg = (date + ",Heart rate is above normal " + heartRate);
                    toServer.writeUTF(msg);
                    toServer.writeUTF(String.valueOf(heartRate));
                    System.out.println(msg + ". An alert message is sent to the Medical Server.");
                 

                } else if (heartRate < 60) {
                    String msg = (date + ",Heart rate is below normal " + heartRate);
                    toServer.writeUTF(msg);
                    toServer.writeUTF(String.valueOf(heartRate));
                    System.out.println(msg + ". An alert message is sent to the Medical Server.");
                  
                }
                }
             

                // get third ( Oxygen ) input from the client input 


                o = Fromclient.readUTF();
                if (o.equals("Close")) {
                    socket.close();
                    break;
                } 
                else{
                
                    double oxygen = Double.valueOf(o);

                if (oxygen >= 75) {
                    System.out.println(date + ",Oxygen saturation is normal " + oxygen);
                    toServer.writeUTF(String.valueOf(""));
                    toServer.writeUTF(String.valueOf(0));
                 

                }
                if (oxygen < 75) {
                    String msg = (date + ",Oxygen saturation is Low " + oxygen);
                    toServer.writeUTF(String.valueOf(msg));
                    toServer.writeUTF(String.valueOf(oxygen));
                    System.out.println(msg + ". An alert message is sent to the Medical Server.");
               
                }                
                
                }


                 
                     
        

           System.out.println();
           if(num > 5){
            break;
           }
           num++;

            } while(true);
           
            
        } while (true); // the server will wait for another connectin   
         
        
   

    }
    
 
    
    
}

    