package org.academiadecodigo.paparasciis;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private List<ServerWorker> workers;
    public static final int PORT = 55556;


    public Server() {

        try {

            System.out.println("Binding to port " + PORT);
            serverSocket = new ServerSocket(PORT);

            System.out.println("Server started: " + serverSocket);

            System.out.println("Waiting for a client connection");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {

        ExecutorService pool = Executors.newFixedThreadPool(10);

        while(true) {

            workers = Collections.synchronizedList(new LinkedList<>());

            try {

                ServerWorker worker = new ServerWorker(serverSocket.accept());

                System.out.println("Client accepted");

                workers.add(worker);

                pool.submit(worker);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }



}
