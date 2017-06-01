package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Created by Famousjasper on 2017-05-31.
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

                            int jpgStartIndex = 0;
                            while(true){
                                if((jpg[jpgStartIndex] == (byte)0xFF && jpg[jpgStartIndex+1] == (byte)0xDA)){
                                    jpgStartIndex+=14; // Scan starts here
                                    break;
                                }
                                jpgStartIndex++;
                            }
                            //increase progress
                            progressBar1.setValue(60);
                            progressBar1.setString("60%");

                            int jpgIndex = jpgStartIndex;
                            String msg = theMessageEditorPane.getText();
                            try{
                                for(int i = 0; i < msg.length(); i++){
                                    byte b = (byte)msg.charAt(i);
                                    for(int j = 7; j > -1; j--){
                                        if(jpg[jpgIndex] != (byte)0xFF){
                                            jpg[jpgIndex] = (byte)((int)jpg[jpgIndex] | ((b >>> j) & 0x01));
                                            jpgIndex++;
                                        }else{
                                            if(jpg[jpgIndex+1] == (byte)0xD9){
                                                throw new IndexOutOfBoundsException();
                                            }
                                            jpgIndex+=2;
                                            jpg[jpgIndex] = (byte)((int)jpg[jpgIndex] | ((b >>> j) & 0x01));
                                        }

                                    }
                                }
                            }catch(IndexOutOfBoundsException ex){
                                JOptionPane.showMessageDialog(theMessageEditorPane, "Parts of your message have been removed because it was too big");
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

