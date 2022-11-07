package chating2;

import java.io.*;
import java.net.*;
import java.util.Vector; //동적 배열, 접속 클라이언트 정보 실시간 저장


public class ServerGUI {
	ServerSocket server;
	Socket socket;
	Vector vector;
	ServerThread serverthread;
	
	public ServerGUI() {
		vector = new Vector();
		try {
			//클라 접속 대기
			server = new ServerSocket(2022);
			System.out.println(" << 클라이언트 접속 대기 중 >> ");
			
			while(true) {
				socket = server.accept();
				System.out.println(" * 클라이언트가 접속에 성공했습니다 *");
				serverthread = new ServerThread(this, socket);
				this.addThread(serverthread);
				serverthread.start();
			}
		}catch (Exception e) {
			System.out.println(" * 서버 접속 실패 * " + e);
		}
	}
	 // 벡터 v에 접속 클라이언트의 스레드 저장
    public void addThread(ServerThread serverthread) {
        vector.add(serverthread);
    }

    // 퇴장한 클라이언트 스레드 제거
    public void removeThread(ServerThread serverthread) {
        vector.remove(serverthread);
    }

    // 각 클라이언트에게 메세지를 출력하는 메소드, send() 호출
    public void broadCast(String str) {
        for (int i = 0; i < vector.size(); i++) {
            // 각각의 클라이언트를 ServerThread 객체로 형 변환 
            ServerThread serverthread = (ServerThread) vector.elementAt(i);

            // 각 스레드 객체에 str 문자열을 전송
            serverthread.send(str);
        }
    }

    public static void main(String[] args) {

        // 익명 객체 생성
        new ServerGUI();

    }

}

	// ServerThread 클래스 생성 → 서버에서 각 클라이언트의 요청을 처리할 스레드
	class ServerThread extends Thread {

	    // 클라이언트 소켓 저장
	    Socket socket;
	
	    // ChatGUIServer 클래스의 객체를 멤버 변수로 선언, has-a 관계를 위함
	    ServerGUI serverGui;
	
	    // 입출력
	    BufferedReader in;
	    PrintWriter out;
	
	    // 전달할 문자열
	    String str;
	
	    // 대화명(ID)
	    String name;
	
	    // 생성자
	    public ServerThread(ServerGUI serverGui, Socket socket) {
	        /* cg = new ChatGUIServer(); → 작성 불가, 서버가 두 번 가동되기 때문에 충돌이 일어남
	        따라서 매개변수를 이용해서 객체를 얻어온(call by reference) 뒤에 cg와 s값을 초기화해야 함
	        */
	        this.serverGui = serverGui;
	
	        // 접속한 클라이언트 정보 저장
	        this.socket = socket;
	
	        // 데이터 전송을 위한 입출력 스트림 생성
	        try {
            // =========== 입력 ===========
            // s.getInputStream() => 접속 클라이언트(소켓 객체)의 InputStream을 얻어 옴
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // =========== 출력 ===========
            /*
            BufferedWriter의 경우 버퍼링 기능을 가지기 때문에 PrintWriter 스트림 사용
            PrintWriter 스트림의 경우 생성자의 두 번째 인자로 autoFlush 기능을 지정할 수 있음
            BufferedWriter를 사용하는 경우 flush() 메소드를 사용해야 함
            */
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println(" * 에러 발생 * " + e);
        }
    }

    // 메세지(입력 문자열) 출력 메소드
    public void send(String str) {
        // 문자열 출력
        out.println(str);

        // 혹시나 버퍼에 남아있는 것을 비워냄
        out.flush();
    }

    // run()_ServerThread -> broadCast(str)_ChatGUIServer -> send(str)_ServerThread
    public void run() {
        try {
            // 대화명 입력 받기
            out.println("아이디를 입력해주세요.");
            name = in.readLine();

            // 서버에서 각 클라이언트에 대화명 출력
            serverGui.broadCast("[" + name + "]" + "님이 로그인 했습니다.");

            // 무한 대기하며 입력한 메세지를 각 클라이언트에 계속 전달
            while ((str = in.readLine()) != null) {
            	serverGui.broadCast("[" + name + "]: " + str);
            }
        } catch (Exception e) {
            // 접속자 퇴장시 v에서 해당 클라이언트 스레드 제거
        	serverGui.removeThread(this); // this: ServerThread 객체, 접속 클라이언트
             // 서버에서 각 클라이언트에 출력
        	serverGui.broadCast("[" + name + "]" + "님이 로그아웃 했습니다.");

            // 콘솔에 퇴장 클라이언트 IP 주소 출력
            System.out.println(socket.getInetAddress() + "의 연결이 종료");
        }
    }
	
}

