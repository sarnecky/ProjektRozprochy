package Server;

public class MainServer {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		startServer();
		
	}
	private static void startServer()
	{                  
    
            
		Server server = new Server();    
               
                
                Thread thread = new Thread(server);
		thread.start();
		System.out.println("Server podlaczony");
            
	}
}
