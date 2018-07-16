package crypto;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class GUI extends JFrame {

	private JPanel contentPane;
	private JLabel lblNoFileChoosen;
		
    KeyGenerator keyGenerator = null;
    static SecretKey secretKey = null;
    static Cipher cipher = null;
    static String ALGO="AES";
    String fileToEncrypt;
    String encryptedFile;
    String decryptedFile;
    String directoryPath;
    static File file;
    static String mode="enc";
    static String userKey="";
    private static File f;
    private JLabel lblDestinationIp;
    private static JTextField txtIp;
    private static JTextField textField;
    private static JTextField txtKey;
    private JPanel panel;
    private JButton btnSend;
    private JLabel lblMyListOf;
	private static JTextField textField_1;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

	EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		

		Thread server=new Thread(){
			public void run(){
				try {
					runServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		server.start();
	 	try {
			listUsers();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	private static void listUsers() throws IOException {
		int octet1s,octet2s,octet3s,octet4s;
		int octet1e,octet2e,octet3e,octet4e;
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter start ip of range");
		String startIP=br.readLine();
		System.out.println("Enter end ip of range");
		String endIP=br.readLine();
		String ip;
		StringTokenizer st=new StringTokenizer(startIP, ".");
		octet1s=Integer.parseInt(st.nextToken());
		octet2s=Integer.parseInt(st.nextToken());
		octet3s=Integer.parseInt(st.nextToken());
		octet4s=Integer.parseInt(st.nextToken());
		StringTokenizer st1=new StringTokenizer(startIP, ".");
		
		octet1e=Integer.parseInt(st1.nextToken());
		octet2e=Integer.parseInt(st1.nextToken());
		octet3e=Integer.parseInt(st1.nextToken());
		octet4e=Integer.parseInt(st1.nextToken());
		for(;octet1s<=octet1e;++octet1s) {
			for(;octet2s<=octet2e;++octet2s) {
				for(;octet3s<=octet3e;++octet3s) {
                    try{
                    	ip=Integer.toString(octet1s)+"."+Integer.toString(octet2s)+"."+Integer.toString(octet3s)+"."+Integer.toString(octet4s);
                    	Socket ServerSok = new Socket(ip,6661);
                    	System.out.println(ip);
                    	ServerSok.close();
                    	}
                    catch (Exception e){
                        e.printStackTrace(); 
                    }
				}
			}
		}	
	}
	private static void runServer(){
		try {
			ServerSocket serverSocket = null;		  			
				try {
					serverSocket = new ServerSocket(6661);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,"Can't setup server on specified port number");
					while(true) {
						try {
							Thread.sleep(5000); 
							serverSocket = new ServerSocket(Integer.parseInt(textField_1.getText()));
							break;
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null,"Can't setup server on specified port number");
							e.printStackTrace();
						}
					}	
				}
			while(true) {
				try {
						Socket socket= null;
					    socket = serverSocket.accept();
					    InputStream in = null;
						OutputStream out = null;
					    File directory = new File("downloaded");
					    if (! directory.exists()){
					        directory.mkdir();
					    }
						String tempFilename="downloaded/"+Long.toString(System.currentTimeMillis())+"Encrypted";
						out = new FileOutputStream(tempFilename);
						try {
								in = socket.getInputStream();
							} catch (IOException ex) {
						    System.out.println("Can't get socket input stream. ");
						}
						byte[] bytes = new byte[16*1024];
						int count;
						try {
							while ((count = in.read(bytes)) > 0) {
							    out.write(bytes, 0, count);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							out.close();
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						String clientIP=socket.getRemoteSocketAddress().toString();
						Thread save=new Thread(){
							public void run(){					
								IncommingFile obj = null;
								obj = new IncommingFile(clientIP,tempFilename);
								obj.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
								obj.setVisible(true);
							}
						};
						save.start();
						socket.close();
					} catch (IOException ex) {
					    ex.printStackTrace();
						serverSocket.close();
						serverSocket = null;
						while(true) {
							try {
								Thread.sleep(5000); 
								serverSocket = new ServerSocket(Integer.parseInt(textField_1.getText()));
								break;
							} catch (Exception e) {
								JOptionPane.showMessageDialog(null,"Can't setup server on specified port number");
								e.printStackTrace();
							}
						}
					}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	private static void runClient() {
		try {
				Socket socket = null;
			    OutputStream out = null;
			    InputStream in = null;
		        socket = new Socket(txtIp.getText(), Integer.parseInt(textField.getText()));
		        String tempFile=System.currentTimeMillis()+f.getName();
		        secretKey=new SecretKeySpec(txtKey.getText().getBytes(StandardCharsets.UTF_8),ALGO);
		        encrypt(f.getAbsolutePath(),tempFile);
		      
		        // Get the size of the file
		        byte[] bytes = new byte[16 * 1024];
		        in = new FileInputStream(tempFile);
		        out = socket.getOutputStream();

		        int count;
		        while ((count = in.read(bytes)) > 0) {
		            out.write(bytes, 0, count);
		        }
		        out.close();
		        in.close();
		        socket.close();
		        try {
					Files.delete(Paths.get(tempFile));
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null,"File successfully sent to "+txtIp.getText()+"!");
	}
	/**
	 * Create the frame.
	 */
	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblChooseFileTo = new JLabel("Choose File to Send:");
		lblChooseFileTo.setBounds(12, 94, 128, 16);
		contentPane.add(lblChooseFileTo);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(152, 90, 87, 25);
		contentPane.add(btnBrowse);
		
		lblNoFileChoosen = new JLabel("no file choosen");
		lblNoFileChoosen.setBounds(251, 94, 169, 16);
		contentPane.add(lblNoFileChoosen);
		
		lblDestinationIp = new JLabel("Destination IP:");
		lblDestinationIp.setBounds(12, 123, 128, 16);
		contentPane.add(lblDestinationIp);
		
		txtIp = new JTextField();
		txtIp.setText("127.16.1.1");
		txtIp.setBounds(152, 120, 119, 22);
		contentPane.add(txtIp);
		txtIp.setColumns(10);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(283, 123, 56, 16);
		contentPane.add(lblPort);
		
		textField = new JTextField();
		textField.setText("6661");
		textField.setBounds(351, 120, 69, 22);
		contentPane.add(textField);
		textField.setColumns(10);
		
		panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "AES KEY for encryption (default: MD5 of filename+file extension)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(12, 155, 408, 47);
		contentPane.add(panel);
		panel.setLayout(null);
		
		txtKey = new JTextField();
		txtKey.setBounds(12, 18, 384, 22);
		panel.add(txtKey);
		txtKey.setColumns(10);
		
		btnSend = new JButton("Send >");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				runClient();
			}
		});
		btnSend.setBounds(174, 215, 97, 25);
		contentPane.add(btnSend);
		
		lblMyListOf = new JLabel("My list of IPs:");
		lblMyListOf.setBounds(12, 13, 97, 16);
		contentPane.add(lblMyListOf);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(125, 10, 295, 71);
		contentPane.add(scrollPane);
		
		JTextArea txtrIp = new JTextArea();
		txtrIp.setText("Loading ips please wait...");
		txtrIp.setEditable(false);
		scrollPane.setViewportView(txtrIp);
		
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			    JFileChooser fc=new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());    
			    int i=fc.showOpenDialog(null);    
			    if(i==JFileChooser.APPROVE_OPTION){    
			        f=fc.getSelectedFile();    
			        fileToEncrypt=f.getPath();   
			        directoryPath=f.getParent();
			        lblNoFileChoosen.setText(f.getName());
			        System.out.println();
			        txtKey.setText(md5(f.getName())+f.getName().substring(f.getName().lastIndexOf('.')));
			    }   
			}
		});
		Enumeration<?> e = null;
		try {
			e = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		txtrIp.setText("");
		textField_1 = new JTextField();
		textField_1.setText("6661");
		textField_1.setBounds(12, 59, 97, 22);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		while(e.hasMoreElements()){
		    NetworkInterface n = (NetworkInterface) e.nextElement();
		    Enumeration<?> ee = n.getInetAddresses();
		    while (ee.hasMoreElements()){
		        InetAddress i = (InetAddress) ee.nextElement();
		        txtrIp.append(i.getHostAddress()+"\n");
		    }
		}
	}
    private static void encrypt(String srcPath, String destPath) {
        File rawFile = new File(srcPath);
        File encryptedFile = new File(destPath);
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
        	try {
				cipher = Cipher.getInstance(ALGO);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			}
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            inStream = new FileInputStream(rawFile);
            outStream = new FileOutputStream(encryptedFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) > 0) {
                outStream.write(cipher.update(buffer, 0, len));
                outStream.flush();
            }
            outStream.write(cipher.doFinal());
            inStream.close();
            outStream.close();
        } catch (Exception ex) {
           ex.printStackTrace();
        } 
    }
    private static String md5(String data) {
    	StringBuilder hexString = new StringBuilder();
        try {
       	 byte[] bytesOfMessage = data.getBytes("UTF-8");

       	 MessageDigest md = MessageDigest.getInstance("MD5");
       	 byte[] thedigest = md.digest(bytesOfMessage);

       	    for (int i1 = 0; i1 < thedigest.length; i1++) {
       	        String hex = Integer.toHexString(0xFF & thedigest[i1]);
       	        if (hex.length() == 1) {
       	            hexString.append('0');
       	        }
       	        hexString.append(hex);
       	    }
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}        
        return(hexString.toString().toUpperCase());      
    }
}
