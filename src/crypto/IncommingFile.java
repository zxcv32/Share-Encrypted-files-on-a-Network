package crypto;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class IncommingFile extends JFrame {

	static JLabel lblPleaseSaveThe;
	static byte keyValue[];
	public IncommingFile(String ip, String tempFileEnc) {

		JPanel contentPane;
		JButton btnSaveAs;
		JTextField textField;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 310);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblIp = new JLabel(ip);
		lblIp.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblIp.setHorizontalAlignment(SwingConstants.CENTER);
		lblIp.setBounds(12, 13, 408, 80);
		contentPane.add(lblIp);
		
		JLabel lblSentYouA = new JLabel("Sent You a file");
		lblSentYouA.setHorizontalAlignment(SwingConstants.CENTER);
		lblSentYouA.setBounds(22, 106, 398, 16);
		contentPane.add(lblSentYouA);
		
		btnSaveAs = new JButton("Save As:");
		btnSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					save("enc",tempFileEnc);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		btnSaveAs.setBounds(201, 131, 97, 25);
		contentPane.add(btnSaveAs);
		
		JLabel lblSaveEncryptedFile = new JLabel("Save Encrypted File");
		lblSaveEncryptedFile.setHorizontalAlignment(SwingConstants.LEFT);
		lblSaveEncryptedFile.setBounds(12, 135, 177, 16);
		contentPane.add(lblSaveEncryptedFile);
		
		textField = new JTextField();
		textField.setBounds(201, 198, 219, 22);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnSaveAs_1 = new JButton("Save As:");
		btnSaveAs_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					keyValue=(textField.getText()).getBytes(StandardCharsets.UTF_8);
					if(keyValue.length>0)
						save("dec",tempFileEnc);
					else
						JOptionPane.showMessageDialog(null,"Please enter decryption key first!");
						
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnSaveAs_1.setBounds(201, 160, 97, 25);
		contentPane.add(btnSaveAs_1);
		
		JLabel lblDecryptAndSave = new JLabel("Decrypt and Save ");
		lblDecryptAndSave.setBounds(12, 164, 186, 16);
		contentPane.add(lblDecryptAndSave);
		
		JLabel lblEnterDecryptionKey = new JLabel("Enter Decryption Key:");
		lblEnterDecryptionKey.setBounds(12, 201, 177, 16);
		contentPane.add(lblEnterDecryptionKey);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnClose.setBounds(201, 226, 97, 25);
		contentPane.add(btnClose);
		
		lblPleaseSaveThe = new JLabel("Please Save the file");
		lblPleaseSaveThe.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseSaveThe.setBounds(22, 230, 167, 16);
		contentPane.add(lblPleaseSaveThe);
	}

	private static void save(String mode, String tempFileEnc) {
		JFileChooser fileChooser = new JFileChooser();
		File destfile;
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
		  destfile = fileChooser.getSelectedFile();
		  if(mode=="enc") {
			  try {
					copyFile(new File(tempFileEnc), destfile);
			 	} catch (IOException e) {
			  		e.printStackTrace();
			  	}
			}
		  	else {
		  		 decrypt(tempFileEnc, destfile.getAbsolutePath());
			}
		  lblPleaseSaveThe.setText("Saved!");
		  }
	}
	private static void copyFile(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	
    private static void decrypt(String srcPath, String destPath) {
    	 SecretKey secretKey = null;
    	 KeyGenerator keyGenerator = null;
    	 Cipher cipher = null;
    	 try {
    		 try {
   				//keyGenerator = KeyGenerator.getInstance("AES");
   				secretKey = new SecretKeySpec(keyValue, "AES");    	 
   				cipher = Cipher.getInstance("AES");
    		 } catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        File encryptedFile = new File(srcPath);
        File decryptedFile = new File(destPath);
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            /**
             * Initialize the cipher for decryption
             */
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            /**
             * Initialize input and output streams
             */
            inStream = new FileInputStream(encryptedFile);
            outStream = new FileOutputStream(decryptedFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) > 0) {
                outStream.write(cipher.update(buffer, 0, len));
                outStream.flush();
            }
            outStream.write(cipher.doFinal());
            inStream.close();
            outStream.close();
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex);
        } catch (BadPaddingException ex) {
            System.out.println(ex);
        } catch (InvalidKeyException ex) {
            System.out.println(ex);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    private static Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, "AES");
    }
}
