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
	
    public void addThread(ServerThread serverthread) {
        vector.add(serverthread);
    }
    
    public void removeThread(ServerThread serverthread) {
        vector.remove(serverthread);
    }

    public void broadCast(String str) {
        for (int i = 0; i < vector.size(); i++) {
            ServerThread serverthread = (ServerThread) vector.elementAt(i);
            serverthread.send(str);
        }
    }

    public static void main(String[] args) {
    
        new ServerGUI();

    }

}

	
	class ServerThread extends Thread {

	    Socket socket;
	    ServerGUI serverGui;
	    BufferedReader in;
	    PrintWriter out;
	    String str;
	    String name;
	
	    
	    public ServerThread(ServerGUI serverGui, Socket socket) {
	        this.serverGui = serverGui;
	        this.socket = socket;
	
	        
	        try {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            	out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println(" * 에러 발생 * " + e);
        }
    }

    
    public void send(String str) {
        out.println(str);
        out.flush();
    }

    public void run() {
        try {
            out.println("아이디를 입력해주세요.");
            name = in.readLine();

            serverGui.broadCast("[" + name + "]" + "님이 로그인 했습니다.");

            while ((str = in.readLine()) != null) {
            	serverGui.broadCast("[" + name + "]: " + str);
            }
        } catch (Exception e) {
        	serverGui.removeThread(this);
        	serverGui.broadCast("[" + name + "]" + "님이 로그아웃 했습니다.");
		
            System.out.println(socket.getInetAddress() + "의 연결이 종료");
        }
    }
	
}

