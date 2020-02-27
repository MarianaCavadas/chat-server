package org.academiadecodigo.paparasciis;

import javax.xml.bind.annotation.XmlType;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private BufferedReader serverIn;
    private BufferedWriter serverOut;
    private Scanner scanner;
    private Socket socket;
    public static final int PORT = 55556;

    public static void main(String[] args) {

        Client client = new Client("127.0.0.1");

        Thread thread = new Thread(client.new ClientRunnable());

        thread.start();

        client.start();

    }

    public Client(String address) {

        System.out.println("Trying to establish a connection, please wait...");

        try {
            socket = new Socket(address, PORT);
            System.out.println("Connected to: " + socket);

            streamsInit();

            System.out.println("You may start a conversation.");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void start() {

        while (true) {

            try {
                String messageReceived = serverIn.readLine();

                if(messageReceived == null) {
                    System.out.println("...");
                    break;
                }

                System.out.println(messageReceived);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void streamsInit() {

        try {
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            scanner = new Scanner(System.in);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class ClientRunnable implements Runnable {

        @Override
        public void run() {

            while (true) {

                String message = scanner.nextLine();

                try {
                    serverOut.write(message);
                    serverOut.newLine();
                    serverOut.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
