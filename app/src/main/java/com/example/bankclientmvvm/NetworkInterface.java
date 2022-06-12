package com.example.bankclientmvvm;


public interface NetworkInterface {
    interface Network {

        void setIPAddress(String ipAddress);
        String getIPAddress();
        String getMesFromServer();

        //Lấy biến trạng thái stateConnecting từ model Network
        //        void setStateConnecting(boolean stateConnecting);
        boolean getStateConnecting();

        //Tạo kết nối với server
        void createConnect();

        //Tạo kết nối để gửi/đọc dữ liệu
//        void createControlConnect();

        //Ngắt kết nối với server
        void disconnect();
//        void disconnectControl();

        //Gửi dữ liệu
        void sendDataTCP(String messenge);

        //Nhận dữ liệu
//        void readDataUDP();
        void readDataTCP();

        //Kill threadReadData nếu Server k trả dữ liệu
        void killReadDataTCP(int timeKillThreadReadDataTCP);

        //Luôn gửi đến server "text"/giây để kiểm tra trạng thái kết nối đến server, sử dụng port của hàm createConnect()
        void checkConnect();
    }
}
