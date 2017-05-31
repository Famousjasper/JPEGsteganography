package com.company;

import sun.misc.IOUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

/**
 * Created by jesper on 2017-05-31.
 */
public class GUI extends JFrame{
    private JPanel Steganography;
    private JTextField pictureTextField;
    private JEditorPane theMessageEditorPane;
    private JButton browseButton;
    private JProgressBar progressBar1;
    private JButton proceed;
    private JFileChooser fileChooser;
    private File pictureFile;

    public GUI() {
        this.setContentPane(Steganography);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        browseButton.addActionListener(al);
        proceed.addActionListener(al);
        this.pack();
        this.setVisible(true);
    }
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == browseButton){
                fileChooser = new JFileChooser();
                int returnVal = fileChooser.showOpenDialog(browseButton);
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    pictureFile = fileChooser.getSelectedFile();
                    pictureTextField.setText(pictureFile.getAbsolutePath());
                }
            }else if(e.getSource() == proceed){
                try {
                    if (pictureFile.getAbsolutePath().endsWith(".jpg")) {
                        if (JOptionPane.showConfirmDialog(theMessageEditorPane, "This will inject your message into the JPEG, proceed?") ==
                                JOptionPane.YES_OPTION) {

                            FileInputStream fis = new FileInputStream(pictureFile);
                            byte[] buffer = new byte[(int) pictureFile.length()];
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            //increase progress
                            progressBar1.setValue(10);
                            progressBar1.setString("10%");
                            int readBytes;
                            while ((readBytes = fis.read(buffer)) != -1) {
                                os.write(buffer, 0, readBytes);

                            }
                            //all bytes are in jpg
                            byte[] jpg = os.toByteArray();
                            //increase progress
                            progressBar1.setValue(50);
                            progressBar1.setString("50%");

                            Scanner sc = new Scanner(theMessageEditorPane.getText());
                            int jpgStartIndex = 0;
                            while(true){
                                if((jpg[jpgStartIndex] == (byte)0xFF && jpg[jpgStartIndex+1] == (byte)0xDA)){
                                    jpgStartIndex+=2; // Skip 0xFFDA
                                    break;
                                }
                                jpgStartIndex++;
                            }
                            //increase progress
                            progressBar1.setValue(60);
                            progressBar1.setString("60%");

                            int jpgIndex = jpgStartIndex;

                            while(sc.hasNextByte()){
                                byte b = sc.nextByte();
                                for(int i = 7; i > 0; i--){
                                    jpg[jpgIndex] = (byte)((int)jpg[jpgIndex] | ((b >>> i) & 0x01));
                                    jpgIndex++;
                                }
                            }
                            //increase progress
                            progressBar1.setValue(90);
                            progressBar1.setString("90%");

                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(jpg);
                            fos.close();

                            //increase progress
                            progressBar1.setValue(100);
                            progressBar1.setString("Done");

                        }
                    } else {
                        JOptionPane.showMessageDialog(proceed, "This isn't a JPEG :(");
                    }
                }catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(theMessageEditorPane, "Couldn't find the file :(");
                }catch (IOException ex) {
                    JOptionPane.showMessageDialog(theMessageEditorPane, "Couldn't read the file :(");
                }catch (NullPointerException ex) {
                    JOptionPane.showMessageDialog(theMessageEditorPane, "Please choose a file first");
                }
            }

        }
    };
}

