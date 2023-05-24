package com.teta;

import java.nio.charset.StandardCharsets;

public class Commands6606 {
    char STX = ':';
    char ETX = '\r';
    char slaveId = '1';

    public String GetOpenGate(){
            // Define the command
            char command = 'K';
            int checkSum = (slaveId + command) % 256;

            // prepare message to be sent
            return String.format("%c%c%c%02X%c", STX, slaveId, command, checkSum, ETX);
    }

    public String GetPullRole(){
        // Define the command
        char command = 'W';
        char data1 = 'F';
        char data2 = 'F';

        // prepare message to be sent
        int checkSum = (slaveId+command+data1+data2) % 256;

        return String.format("%c%c%c%c%c%02X%c", STX, slaveId, command, data1, data2,checkSum, ETX);
    }

    public String DropPullRole(){
        // Define the command
        char command = 'W';
        char data1 = '0';
        char data2 = '0';

        // prepare message to be sent
        int checkSum = (slaveId+command+data1+data2) % 256;

        return String.format("%c%c%c%c%c%02X%c", STX, slaveId, command, data1, data2,checkSum, ETX);
    }

    public String ReadInput(){
        char command = 'I';
        int checkSum = (slaveId+command) % 256;

        return String.format("%c%c%c%02X%c", STX, slaveId, command, checkSum, ETX);
    }

}
