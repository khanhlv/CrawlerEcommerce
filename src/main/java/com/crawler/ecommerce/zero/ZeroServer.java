package com.crawler.ecommerce.zero;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZeroServer {
    public static void main(String[] args) throws Exception
    {
        try (ZContext context = new ZContext()) {
            //  Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv();
                System.out.println(
                        "Received " + ": [" + new String(reply, ZMQ.CHARSET) + "]"
                );
                String response = "world";
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
                Thread.sleep(1000); //  Do some 'work'
            }
        }
    }
}
