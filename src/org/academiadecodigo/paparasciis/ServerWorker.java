package org.academiadecodigo.paparasciis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerWorker implements Runnable {

    private BufferedWriter out;
    private BufferedReader in;
    private Socket socket;


    public ServerWorker(Socket socket) {

        this.socket = socket;

        initStreams();
    }

    public void initStreams() {

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {



    }
}
