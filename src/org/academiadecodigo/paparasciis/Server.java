package org.academiadecodigo.paparasciis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<Integer, ServerWorker> workers;
    static final int PORT = 50;

    public static void main(String[] args) {

        Server server = new Server();

        server.start();
    }


    private Server() {

        try {

            System.out.println("Binding to port " + PORT);
            serverSocket = new ServerSocket(PORT);

            System.out.println("Server started: " + serverSocket);

            System.out.println("Waiting for a client connection");

            workers = Collections.synchronizedMap(new HashMap<>());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {

        AtomicInteger id = new AtomicInteger();

        ExecutorService pool = Executors.newFixedThreadPool(10);

        while (true) {

            try {

                ServerWorker worker = new ServerWorker(serverSocket.accept());

                id.getAndIncrement();

                System.out.println("Client accepted");

                System.out.println("Client " + id);

                workers.put(id.get(), worker);

                pool.submit(worker);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private synchronized void broadcast(String message) {


        for (ServerWorker w : workers.values()) {

            w.send(message);
        }

    }




    private class ServerWorker implements Runnable {

        private BufferedWriter clientOut;
        private BufferedReader clientIn;
        private Socket socket;
        //private String aliasClient;


        private ServerWorker(Socket socket) {

            this.socket = socket;

            initStreams();

        }

        private void initStreams() {

            try {
                clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {

            while (!socket.isClosed()) {

                broadcast(getMessage());
            }
        }

        /*private String getAliasClient() {

            try {
                aliasClient = clientIn.readLine();
                System.out.println(aliasClient);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return aliasClient;
        }*/

        private void send(String message) {

           /* String command = message.split(" ")[0];

            commands(command);

            if (command.charAt(0) == '/') {
                return;
            }*/

            try {

                clientOut.write(message);
                clientOut.newLine();
                clientOut.flush();

            } catch (IOException e) {

                workers.remove(this);
                //e.printStackTrace();
            }
        }

        private String getMessage() {

            String message = "";

            try {
                message = clientIn.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return message;
        }


        private void commands(String command) {

            if (command == null || command.charAt(0) != '/') {
                return;
            }

            switch (command) {

                case "/alias":

                default:
                    System.out.println("Not a known command, please try again.");
                    break;

            }
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

        private void closeUp() {

            try {
                System.out.println("Connection closed");

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                closeStream(clientIn);
                closeStream(clientOut);
                closeStream(socket);
            }

        }
    }

}
