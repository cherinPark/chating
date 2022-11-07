package chating2;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;

public class ClientGUI extends JFrame implements ActionListener, Runnable{
	//클라이언트 화면
	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textArea);
	JTextField textField = new JTextField(20);
	JButton button = new JButton("전송");
	JPanel panel = new JPanel();
	
	Socket socket; //서버통신
	PrintWriter out; //출력
	BufferedReader in; //입력
	String str; //채팅 문자 저장
	
	public ClientGUI() {
		super("채팅방");
		add("Center", scrollPane);
		panel.add(textField);
		panel.add(button);
		add("South", panel);
		textField.addActionListener(this);
		button.addActionListener(this);
		setBounds(200,200,500,350);
		panel.setBackground(Color.pink);
		setVisible(true);
		textField.requestFocus();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
            	    socket = new Socket("127.0.0.1", 2022);
            	    System.out.println("서버 : " + socket);
        
           	    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           	    out = new PrintWriter(socket.getOutputStream(), true);

        	} catch (Exception e) {
            	    System.out.println(" * 접속 오류 발생 * " + e);
       		}

        	Thread client = new Thread(this);
		
        	client.start();
    	}

    public void run() {
        try {
            while ((str = in.readLine()) != null) {
                textArea.append(str + "\n"); // 상대방이 보낸 문자를 채팅창에 세로로 출력
            }
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }

    public void actionPerformed(ActionEvent e) {
        str = textField.getText();
        if(e.getSource()==button) {
        	if(textField.getText().equals("")) {
        		return;
        	} 
        }
        textField.setText("");
        textArea.append(textField.getText()+"\n");
	    
        out.println(str);
        out.flush();
    }

    public static void main(String[] args) {

        new ClientGUI();

    }
}
