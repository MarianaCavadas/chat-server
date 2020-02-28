package org.academiadecodigo.paparasciis;

import javax.xml.bind.annotation.XmlType;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private BufferedReader serverIn;
    private BufferedWriter serverOut;
    private Scanner scanner;
    private Socket socket;
    private String alias;


    public static void main(String[] args) {


        Client client = new Client();

        Thread thread = new Thread(client.new ClientRunnable());

        thread.start();

        client.start();

    }

    public Client() {

        System.out.println("Trying to establish a connection, please wait...");

        try {

            socket = new Socket(InetAddress.getByName("127.0.0.1"), Server.PORT);
            System.out.println("Connected to: " + socket);

            streamsInit();

            alias = getAlias();

            System.out.println("You may start a conversation.\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void start() {

        while (!socket.isClosed()) {

            try {

                String messageReceived = serverIn.readLine();

                if (messageReceived == null) {
                    close();
                    break;
                }

                System.out.println(messageReceived);

            } catch (IOException e) {
                close();
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

    private void close() {

        try {
            System.out.println("Connection closed");
            System.exit(1);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            closeStream(serverIn);
            closeStream(serverOut);
            closeStream(scanner);
            closeStream(socket);
        }
    }

    private String getAlias() {
        System.out.println("Alias? ");
        return scanner.nextLine();
    }

    private void closeStream(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class ClientRunnable implements Runnable {

        @Override
        public void run() {

            while (!socket.isClosed()) {

                String message = scanner.nextLine();

                if (message.split(" ")[0].equals("/quit")) {
                    close();
                    return;
                }

                if (message.split(" ") [0].equals("/alias")) {
                    alias = getAlias();
                    return;
                }

                try {

                    serverOut.write(alias + ": " + message);
                    serverOut.newLine();
                    serverOut.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
