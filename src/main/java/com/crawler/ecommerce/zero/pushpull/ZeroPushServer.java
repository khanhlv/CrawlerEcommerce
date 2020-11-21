package com.crawler.ecommerce.zero.pushpull;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZeroPushServer {

    public static void main(String[] args) throws Exception
    {
        try (ZContext context = new ZContext()) {
            //  Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.PUSH);
            socket.bind("tcp://*:5555");

            int i = 1;
            while (!Thread.currentThread().isInterrupted()) {
                String data = "abc" + i++;
                socket.send(data.getBytes(ZMQ.CHARSET));
                System.out.println("Send: " + data);
                Thread.sleep(200); //  Do some 'work'
            }
        }
    }
}
