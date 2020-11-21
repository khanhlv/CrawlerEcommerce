package com.crawler.ecommerce.zero.pubsub;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZeroPubServer {

    public static void main(String[] args) throws Exception
    {
        try (ZContext context = new ZContext()) {
            //  Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.PUB);
            socket.bind("tcp://*:5555");

            int i = 1;
            while (!Thread.currentThread().isInterrupted()) {
                String data = "abc" + i++;
                socket.sendMore("A");
                socket.send(data.getBytes(ZMQ.CHARSET));
                System.out.println("Send: " + data);
                Thread.sleep(1000); //  Do some 'work'
            }
        }
    }
}
