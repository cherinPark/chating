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
            // 클라이언트 측 소켓 정보 초기화
            // Socket(host, port), host: 접속 서버 IP 주소, port: 서버 포트 번호
            socket = new Socket("127.0.0.1", 2022);
            System.out.println("서버 : " + socket);

            // ========== Server와 Stream 연결 ===========
           in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // PrintWriter 스트림의 autoFlush 기능 활성화
           out = new PrintWriter(socket.getOutputStream(), true);

        } catch (Exception e) {
            System.out.println(" * 접속 오류 발생 * " + e);
        }

        // Thread 객체 생성, Runnable 인터페이스를 구현하기 때문에 this 작성
        Thread client = new Thread(this);

        // 클라이언트 스레드 실행 → run() 호출
        client.start();
    }

    // Runnable 인터페이스 run() 메소드 오버라이딩
    public void run() {
        // 더 이상 입력을 받을 수 없을 때까지 JTextArea(채팅창)에 출력
        try {
            while ((str = in.readLine()) != null) {
                textArea.append(str + "\n"); // 상대방이 보낸 문자를 채팅창에 세로로 출력
            }
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }

    // ActionListener 메소드 오버라이딩, 입력란에서 enter입력시 실행할 코드
    public void actionPerformed(ActionEvent e) {
        // 내가 쓴 메세지를 str 변수에 저장
        str = textField.getText();
        if(e.getSource()==button) { //전송버튼 눌렀을 경우
        	//메세지 입력없이 전송버튼
        	if(textField.getText().equals("")) {
        		return;
        	} 
        }
        // 변수에 저장 후 텍스트필드 초기화
        textField.setText("");
        textArea.append(textField.getText()+"\n");

        // 내가 쓴 메세지 출력 -> 상대방은 br.readLine()으로 읽어들임
        out.println(str);
        out.flush();
    }

    public static void main(String[] args) {

        // 클라이언트 객체 생성, 생성자 호출
        new ClientGUI();

    }
}
