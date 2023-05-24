package com.teta;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class SerialComm {
    private SerialPort serialPort;

    public SerialPort getSerialPort(){
        return this.serialPort;
    }

    public SerialPort[] getPorts(){
        return serialPort.getCommPorts();
    }

    public boolean openPort(String portName, int baudRate, int dataBits){
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baudRate);
        serialPort.setNumDataBits(dataBits);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 900, 0);

        if(serialPort.openPort())
            return true;

        return false;
    }

    public boolean closePort(){
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }

        if(!serialPort.isOpen())
            return true;

        return false;
    }

    public void writeData(byte[] sendData, SerialPort serialPort){
        try{
            serialPort.getOutputStream().write(sendData);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String GetResponse(SerialPort serialPort){
        ArrayList<String> data = new ArrayList<>();
        try {
            for (int i = 0; i <= 7; i++) {
                // Read the response from the device
                byte[] buffer = new byte[1024];
                int bytesRead = serialPort.getInputStream().read(buffer);
                String response = new String(buffer, 0, bytesRead, StandardCharsets.US_ASCII);

                data.add(response);
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        return String.join("", data);
    }

}