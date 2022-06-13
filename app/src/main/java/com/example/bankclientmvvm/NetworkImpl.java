package com.example.bankclientmvvm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class NetworkImpl implements NetworkInterface.Network {

    private String messenge = "";
    private static String ipAddress;
    private static int portSendData;
    private static boolean stateConnecting = false;

    private static Socket socketTCP;
    private OutputStream outputStream;
    private InputStream inputStream;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    private Thread threadConnect, threadDisconnect, threadSendDataTCP, threadReadDataTCP,
            threadCheckConnect, threadKillReadDataTCP;

    public NetworkImpl() {
        portSendData = 8000;
    }

    private void setMessenge(String messenge){
        this.messenge = messenge;
    }

    @Override
    public void setIPAddress(String ipAddress) {
        NetworkImpl.ipAddress = ipAddress;
    }

    @Override
    public String getIPAddress() {
        return ipAddress;
    }

    @Override
    public boolean getStateConnecting() {
        return stateConnecting;
    }

    @Override
    public String getMesFromServer() {
        return messenge;
    }

    @Override
    public void createConnect() {
        //Create SenderTCP stream
        threadConnect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    socketTCP = new Socket(ipAddress, portSendData);
                    socketTCP = new Socket();
                    socketTCP.connect(new InetSocketAddress(ipAddress, portSendData), 3000);
                } catch(SocketException ex) {
                    System.out.println("I/O error: " + ex.getMessage());
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
        threadConnect.setName("Thread Connect");
        threadConnect.start();
        try {
            threadConnect.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        threadDisconnect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Close socket and data stream
                    String messenge = "end";
//                    dataStreamOut.writeUTF(messenge);
                    sendDataTCP(messenge);
                    Thread.sleep(100);
                    dataOutputStream.close();
                    dataInputStream.close();
                    socketTCP.close();

                    //Ngắt thread disconnect đang chạy
                    Thread.currentThread();
                    if(!Thread.interrupted()) {
                        Thread.currentThread().interrupt();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
//                    setStateConnecting(false);
                }
            }
        });
        threadDisconnect.setName("Thread Disconnect");
        threadDisconnect.start();
        try {
            threadDisconnect.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendDataTCP(String messenge) {
        threadSendDataTCP = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    outputStream = socketTCP.getOutputStream();
                    dataOutputStream = new DataOutputStream(outputStream);
                    dataOutputStream.writeUTF(messenge);
                    System.out.println(messenge);

                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        threadSendDataTCP.setName("Thread SendData TCP");
        threadSendDataTCP.start();
    }

    @Override
    public void readDataTCP() {
        threadReadDataTCP = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String messRecv;
                    inputStream = socketTCP.getInputStream();
                    dataInputStream = new DataInputStream(inputStream);
                    while(true){
                        messRecv = dataInputStream.readUTF();
                        if(messRecv != null){
                            System.out.println(messRecv);
                            setMessenge(messRecv);
                            break;
                        }
                    }
                    Thread.currentThread();
                    if(!Thread.interrupted()) {
                        Thread.currentThread().interrupt();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadReadDataTCP.setName("Thread ReadData TCP");
        threadReadDataTCP.start();
        try {
            threadReadDataTCP.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkConnect() {
        threadCheckConnect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (stateConnecting == true) {
                        Thread.currentThread();
                        Thread.sleep(1000);
                        sendDataTCP("0");
                    }

                } catch (InterruptedException e){
                    e.printStackTrace();
                    stateConnecting = false;
//                    Thread.currentThread().interrupt();
                }
            }
        });
        threadCheckConnect.setName("Thread CheckConnect");
        threadCheckConnect.start();
    }

    @Override
    public void killReadDataTCP(int timeKillThreadReadDataTCP){
        threadKillReadDataTCP = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.currentThread();
                    Thread.sleep(timeKillThreadReadDataTCP);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
//                if(!threadReadDataTCP.isInterrupted()) {
                threadReadDataTCP.interrupt();
//                }
            }
        });
        threadKillReadDataTCP.start();
    }
}
