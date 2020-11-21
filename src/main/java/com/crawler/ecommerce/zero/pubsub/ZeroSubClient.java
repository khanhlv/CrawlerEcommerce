package com.crawler.ecommerce.zero.pubsub;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZeroSubClient {
    public static void main(String[] args) throws Exception
    {
        try (ZContext context = new ZContext()) {
            //  Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.SUB);
            socket.subscribe("A");
            socket.connect( "tcp://localhost:5555");

            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Data: " + socket.recvStr());
                Thread.sleep(1000); //  Do some 'work'
            }
        }
    }
}
