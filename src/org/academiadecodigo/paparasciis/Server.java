package org.academiadecodigo.paparasciis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ServerWorker> workers;
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

            workers = Collections.synchronizedMap(new HashMap<String, ServerWorker>());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {

        ExecutorService pool = Executors.newFixedThreadPool(10);

        while (true) {

            try {

                ServerWorker worker = new ServerWorker(serverSocket.accept());

                System.out.println("Client accepted");

                workers.put(worker.aliasClient, worker);

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
        private String aliasClient;


        private ServerWorker(Socket socket) {

            this.socket = socket;

            initStreams();

            try {
                aliasClient = clientIn.readLine();
                System.out.println(aliasClient);

            } catch (IOException e) {
                e.printStackTrace();
            }
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

        private void send(String message) {

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

        private void closeUp() {

            try {
                clientOut.close();
                clientIn.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
