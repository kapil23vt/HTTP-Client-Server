package hserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class hserver {
    public static void main(String[] args) {
        ServerSocket serverSocket = null; 
        PrintWriter printWriter = null;
        OutputStream outputStream = null;
        DataOutputStream dataOutputStream = null;
        BufferedReader bufferedReader = null;
        Socket socket = null;
        File file = null;
        FileInputStream fileInputStream = null;
        
        //Getting the port number from the user
        Scanner scanner = new Scanner(System.in);
		System.out.println("Enter port number for socket creation: ");
		int portNumber = Integer.parseInt(scanner.nextLine());
        
		//Creating the socket on the taken port number
		try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Socket created on server side, now run Client program");
            }
        catch (IOException ioe1) {
            System.err.println("This port already in use, choose another port" + ioe1);
            System.exit(1);
        }
		
        int number_of_connections = 0; // to keep the count of the connections done
        
        while (true) {
        	try {
                System.out.println("Total number of connections = "+ number_of_connections);
                socket = serverSocket.accept();
                //Accepting the connection on the server socket 
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // To read stream from a socket input stream 
                outputStream = socket.getOutputStream();
                // To write stream on socket
                printWriter = new PrintWriter(outputStream, true);
                dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));
            }
            catch (IOException ioe2) {
                System.err.println("I/O Error occurred when waiting for connection" + ioe2);
                System.exit(1);
            }
            try {
            	//reading the request of the socket
                String string = bufferedReader.readLine();
                //string store the inputstream on the socket
                
                if (string != null) {
                    String[] request = string.split("\\s+", 10);
                    //request = GET/test.txt/HTTP 1.0
                    //splitting the string 
                    if (request.length != 3) {
                        printWriter.print("HTTP/1.0 400 Bad Request\r\n\r\n");
                        printWriter.flush();
                    } else if (!request[0].equals("GET")) {
                    	//if the first word is not GET
                        printWriter.print("HTTP/1.0 400 Bad Request\r\n\r\n");
                        printWriter.flush();
                    } else if (!request[2].equals("HTTP/1.0")) {
                    	//if string is not ended by either of the HTTP protocol
                        printWriter.print("HTTP/1.0 400 Bad Request\r\n\r\n");
                        printWriter.flush();
                    } else if (request[1].charAt(0) != '/') {
                    	//if the name of the file is not starting with /
                        printWriter.print("HTTP/1.0 400 Bad Request\r\n\r\n");
                        printWriter.flush();
                    } 
                    
                    else {// when the right request is received
                        fileInputStream = null; 
                        try {
                            file = new File("." + request[1]);
                            //Accessing the requested file 
                            fileInputStream = new FileInputStream(file);
                        }
                        catch (FileNotFoundException filenotfound) {
                            printWriter.print("HTTP/1.0 404 Not Found\r\n\r\n");
                            printWriter.flush();
                        }
                        if (fileInputStream != null) {
                        	
                        	printWriter.print("HTTP/1.0 200 OK\r\n");
                        	printWriter.flush();
                            printWriter.print("Content-Length of the file: " + file.length() + "\r\n\r\n");
                            printWriter.flush();
                            
                            byte[] bytes = new byte[1024];
                            
                            int count=0;
                            while ((count = fileInputStream.read(bytes)) > 0) {
                            	dataOutputStream.write(bytes, 0, count);
                            	dataOutputStream.flush();
                            }
                            
                        }
                    }
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                    fileInputStream = null;
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                    bufferedReader = null;
                }
                if (printWriter != null) {
                    printWriter.close();
                    printWriter = null;
                }
                if (socket != null) {
                	//System.out.println("Closing the server as the file transfer is done");
                	//System.exit(1);
                    socket.close();
                    socket = null;
                }
                
                ++number_of_connections;
                continue;
            }
            catch (IOException ioe3) {
            	//Closing the server 
                System.err.println("Cannot read the client’s request. Closing the connection " + ioe3);
                System.exit(1);
                continue;
            }
        }
       
    }//main
}//class
