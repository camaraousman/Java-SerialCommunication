package com.teta;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Dashboard extends JFrame{
    private JPanel pnMain;
    private JLabel lblId;
    private JTextField txtId;
    private JLabel lblPort;
    private JComboBox cbPort;
    private JButton btnOpen;
    private JButton btnClose;
    private JPanel pnConnection;
    private JPanel pnHardware;
    private JButton btnOpenGate;
    private JButton btnAskInput;
    private JLabel lblOpenGateRes;
    private JLabel lblAskInputRes;
    private JTextArea textAreaTransmitter;
    private JTextArea textAreaReceiver;
    private JPanel pnTransmitter;
    private JPanel pnReceiver;
    private JPanel pnOthers;
    private JLabel lblConnectionStatus;
    private JProgressBar progressBarStatus;
    private JButton btnPullAllRole;
    private JLabel lblAllRoleRes;
    private JButton btnDropAllRole;
    private JLabel lblDropAllRole;
    private JButton btnAskCard;
    private JButton btnAskBarcode;
    private JLabel lblCardRes;
    private JLabel lblBarcodeRes;

    SerialComm comm;
    Commands6606 ioCard;
    public Dashboard(){
        this.comm = new SerialComm();
        this.ioCard = new Commands6606();

        TitledBorder connectionTitle = BorderFactory.createTitledBorder("Port Connection");
        TitledBorder hardwareTitle = BorderFactory.createTitledBorder("6606 Control");
        TitledBorder others = BorderFactory.createTitledBorder("Other Settings");

        connectionTitle.setTitleColor(new Color(0xffffff));
        hardwareTitle.setTitleColor(new Color(0xffffff));
        others.setTitleColor(new Color(0xffffff));

        pnConnection.setBorder(connectionTitle);
        pnHardware.setBorder(hardwareTitle);
        pnOthers.setBorder(others);

        setTitle("6606 TETA ELEKTRONIK");
        setContentPane(pnMain);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(700,400);
        getContentPane().setBackground(new Color(0x252526));
        setVisible(true);

        //manipulate components
        btnClose.setEnabled(false);

        JFrame.setDefaultLookAndFeelDecorated(true);
        getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(60,63,65));
        getRootPane().putClientProperty("JRootPane.titleBarForeground", Color.white);

        ImageIcon image = new ImageIcon("C:/Users/camar/Documents/IdeaProjects/_6606/src/images/favicon.png");
        setIconImage(image.getImage());

        //get available comports
        for(SerialPort eachPort : comm.getPorts()){
            cbPort.addItem(eachPort.getSystemPortName());
        }

        //establish connection
        btnOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBarStatus.setIndeterminate(true);

                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String selectedPort = (String) cbPort.getSelectedItem();
                        if(comm.openPort(selectedPort, 9600, 8)){
                            lblConnectionStatus.setText("ONLINE");
                            lblConnectionStatus.setForeground(Color.green);
                            btnOpen.setEnabled(false);
                            btnClose.setEnabled(true);

                            progressBarStatus.setIndeterminate(false);
                            progressBarStatus.setValue(100);
                        } else{
                            JOptionPane.showMessageDialog(null, "Could not open port, make sure it's not already opened by another program", "Error", JOptionPane.ERROR_MESSAGE);
                            progressBarStatus.setIndeterminate(false);
                            progressBarStatus.setValue(0);
                        }
                    }
                });
            }
        });

        //dispose connection
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comm.closePort()) {
                    lblConnectionStatus.setText("OFFLINE");
                    lblConnectionStatus.setForeground(new Color(0xFF4626));
                    btnOpen.setEnabled(true);
                    btnClose.setEnabled(false);
                    progressBarStatus.setValue(0);

                    //texts and inputs
                    textAreaReceiver.setText("");
                    textAreaTransmitter.setText("");
                    lblOpenGateRes.setText("");
                    lblAllRoleRes.setText("");
                    lblDropAllRole.setText("");
                }
            }
        });

        //open gate
        btnOpenGate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comm.getSerialPort() == null){
                    JOptionPane.showMessageDialog(null, "Please connect device first", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String sendData =  ioCard.GetOpenGate();

                //send data
                comm.writeData(sendData.getBytes(StandardCharsets.US_ASCII), comm.getSerialPort());
                updateTransmitterText(sendData);

                //get response
               String res = comm.GetResponse(comm.getSerialPort());
                lblOpenGateRes.setText(res);
                updateReceiverText(res);

            }
        });

        //Input Status
        btnAskInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sendData = ioCard.ReadInput();

                //write data
                comm.writeData(sendData.getBytes(StandardCharsets.US_ASCII), comm.getSerialPort());
                updateTransmitterText(sendData);

                //get response
                String res = comm.GetResponse(comm.getSerialPort());
                lblAskInputRes.setText(res);
                updateReceiverText(res);
            }
        });

        //pull all role
        btnPullAllRole.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sendData = ioCard.GetPullRole();

                //write data
                comm.writeData(sendData.getBytes(StandardCharsets.US_ASCII), comm.getSerialPort());
                updateTransmitterText(sendData);

                //get response
                String res = comm.GetResponse(comm.getSerialPort());
                lblAllRoleRes.setText(res);
                updateReceiverText(res);
            }
        });
        btnDropAllRole.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sendData = ioCard.DropPullRole();

                //write data
                comm.writeData(sendData.getBytes(StandardCharsets.US_ASCII), comm.getSerialPort());
                updateTransmitterText(sendData);

                //get response
                String res = comm.GetResponse(comm.getSerialPort());
                lblDropAllRole.setText(res);
                updateReceiverText(res);
            }
        });
    }

    public void updateTransmitterText(String sendData){
        textAreaTransmitter.setText(sendData);
        textAreaTransmitter.append("\n");
        textAreaTransmitter.append(Arrays.toString(sendData.getBytes(StandardCharsets.US_ASCII)));
    }

    public void updateReceiverText(String sendData){
        textAreaReceiver.setText(sendData);
        textAreaReceiver.append("\n");
        textAreaReceiver.append(Arrays.toString(sendData.getBytes(StandardCharsets.US_ASCII)));
    }

}
