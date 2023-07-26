 package sensor_application_client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sensor_Application_Client {

    public static void main(String[] args) throws IOException {



        // port number of the personal server
        int portNum = 2016;
        String host = "localhost";
        DataOutputStream sendToServer;
        
       /* InetAddress address = InetAddress.getByName("x.x.x.x");
        String hostname=address.getHostName();
        Socket s = new Socket(hostname, portNum);*/
        

        //create one client socket and give it the values of the port number and the local IP address        
        Socket SocketOfClient = new Socket(host, portNum);

        //convert stream to data, so clinet is able to read them 
        sendToServer = new DataOutputStream(SocketOfClient.getOutputStream()); // convert data into stream  

        Random random = new Random();

        Runnable helloRunnable;
        helloRunnable = new Runnable() {
            @Override
            public void run() {

                SimpleDateFormat dateFormat = new SimpleDateFormat("'at date :'dd MMM dd', time 'HH:mm:ss");
                String date = dateFormat.format(new Date());

                // Will generate random numbers from 36 to 40  
                double temperature = ThreadLocalRandom.current().nextDouble(36.0, 41.0);
                temperature = Math.round(temperature * 10.0) / 10.0;

                // Will generate random numbers of heart rates between 60 to 120 
                double heartRate = random.nextInt(61) + 60;

                // Will generate random numbers of Oxygen level between 60 to 100 
                double oxygen = random.nextInt(41) + 60;

                System.out.println(date + ",Temperature is  " + temperature);

                
                try {
                    sendToServer.writeUTF(String.valueOf(temperature)); // send the input to server   
                } catch (IOException ex) {
                    Logger.getLogger(Sensor_Application_Client.class.getName()).log(Level.SEVERE, null, ex);
                  }
          

                System.out.println(date + ",Heart rate is " + heartRate);
                try {
                    sendToServer.writeUTF(String.valueOf(heartRate)); // send the input to server   
                } catch (IOException ex) {
                    Logger.getLogger(Sensor_Application_Client.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    System.out.println(date + ",Oxygen saturation is " + oxygen);
                    sendToServer.writeUTF(String.valueOf(oxygen)); // send the input to server   
                } catch (IOException ex) {
                    Logger.getLogger(Sensor_Application_Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("");

            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 5, TimeUnit.SECONDS);

        
       
try {
      TimeUnit.MILLISECONDS.sleep(59998);
      sendToServer.writeUTF("Close");
      SocketOfClient.close(); 
      
      
      System.exit(0);
    } catch (InterruptedException e) {
       // SocketOfClient.close(); 
        
    }




      
      



    }

}

    