package hclient;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class hclient {
   public static void main(String[] args) throws IOException {
       Socket socket = null;
       PrintWriter printWriter = null;
       BufferedReader bufferedReader = null;
       DataInputStream dataInputStream = null;
       File file = null;
       int n = 0;
       
       Scanner scanner = new Scanner(System.in);
       System.out.println("Enter your hostname: ");
	   String hostName = scanner.nextLine();
	   InetAddress inetAddress = InetAddress.getByName(hostName);
	   System.out.println("Enter the port number on which server is listening");
	   int port = Integer.parseInt(scanner.nextLine());
       
       BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(System.in));
       System.out.print("Please input file name: ");
       String fileName = bufferedReader2.readLine();
       
       if (fileName != null) {
    	   
    	   try {
                   socket = new Socket(inetAddress, port);
                   printWriter = new PrintWriter(socket.getOutputStream(), true);
                   bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
               }
               catch (UnknownHostException unknownhost1) {
                   System.err.println("Unknown Host Exception for " + hostName);
                   System.exit(1);
               }
               catch (IOException ioe4) {
                   System.err.println("Not able to get Input/Output to = " + hostName);
                   System.exit(1);
               }
               fileName = "/" + fileName; //making it /test.txt
               System.out.println("\nSending incorrect format of request");
               System.out.print("SET " + fileName + " HTTP/1.0\r\n\r\n");
               
               printWriter.print("SET " + fileName + " HTTP/1.0\r\n\r\n");
               printWriter.flush();
               
               System.out.println("Received response from the server:");
               
               String server_response = null;
               server_response = bufferedReader.readLine();
               
               if (server_response == null) {
                   System.err.println("Unable to read the response of the server");
               } else if (server_response.equalsIgnoreCase("HTTP/1.0 400 Bad Request")) {
                   System.out.println(server_response);
               } else {
                   System.out.println("Wrong response from the server:\n");
                   System.out.println(server_response);
               }
               System.out.println("\n");
               socket.close();
               printWriter.close();
               bufferedReader.close();
               
               try {
                   socket = new Socket(inetAddress, port);
                   printWriter = new PrintWriter(socket.getOutputStream(), true);
                   dataInputStream = new DataInputStream(socket.getInputStream());
               }
               catch (UnknownHostException unknownhost2) {
                   System.err.println("Unknown Host Exception for: " + hostName);
                   System.exit(1);
               }
               catch (IOException ioe5) {
            	   //when you enter the wrong name of the host
                   System.err.println("Not able to get Input/Output to = " + hostName);
                   System.exit(1);
               }
               System.out.println("Sending correct format of the request");
               System.out.println("GET " + fileName + " HTTP/1.0");
               
               printWriter.print("GET " + fileName + " HTTP/1.0\r\n");
               printWriter.flush();
               System.out.println("\nReceived response from the server:\n");
               
               server_response = dataInputStream.readLine();
               //reading the response of the server into the same string
              
               if (server_response == null) {
                   //System.err.println("Couldn't read response from server.");
            	     System.err.println("There is no response from the server");
               } else {
            	   if (server_response.equals("HTTP/1.0 200 OK"))
            	   {
            		   FileOutputStream fileoutputstream;
            		   System.out.println(server_response);
            		   
            		   while (!server_response.isEmpty()) {
                           server_response = dataInputStream.readLine();
                            System.out.println(server_response);
                       }
                       
                       try {
                           file = new File("./" + fileName);
                           //using back slash to create file with name as the given
                           fileoutputstream = new FileOutputStream(file);
                           
                           byte[] bytes = new byte[1024];
                           int count=0;
                           while ((count = dataInputStream.read(bytes)) > 0) {
                        	   fileoutputstream.write(bytes, 0, count);
                        	   }
                           fileoutputstream.close();
                           }
                       catch (IOException ioe6) {
                           System.err.println("Couldn't open and write to the output file: " + ioe6);
                           }
                   }
                   
                   if (server_response.equalsIgnoreCase("HTTP/1.0 404 Not Found") || server_response.equalsIgnoreCase("HTTP/1.1 404 Not Found")) 
                   {
                       System.out.println(server_response);
                       System.out.println("The given file is not found on the server.\n");
                   } 
               }
               
               try{
            	   BufferedReader br = new BufferedReader(new FileReader(file));
            	   String readline = null;
            	   System.out.println("The file contents are :");
            	   while ((readline = br.readLine()) != null) {
            		   System.out.println(readline);
            	   }
               	}
               
               catch (NullPointerException e)
               { System.err.println("Cannot print the content of the file as file is not found"); }  
       
               System.out.println("\nClosing the I/O Connection");
               printWriter.close();
               dataInputStream.close();
               bufferedReader2.close();
               socket.close();
         
       }
   }
}
