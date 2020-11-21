package com.crawler.ecommerce.zero.pushpull;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZeroPullClient4 {
    public static void main(String[] args) throws Exception
    {
        try (ZContext context = new ZContext()) {
            //  Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.PULL);
            socket.connect( "tcp://localhost:5555");

            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Data: " + socket.recvStr());
                Thread.sleep(1000); //  Do some 'work'
            }
        }
    }
}
