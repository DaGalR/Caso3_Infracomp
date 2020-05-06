package clienteSeguridad;
import java.io.BufferedReader;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class Cliente {
	public static final int PUERTO = 8080;
	public static final String SERVIDOR = "localhost";
	
	
	public Cliente()
	{
	}
	
	public void procesar() throws IOException{
		Socket socket = null;
		PrintWriter escritor = null;
		BufferedReader lector = null;
		System.out.println("Cliente...");
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			socket = new Socket(SERVIDOR,PUERTO);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch(IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		ProtocoloCliente.procesar(stdIn,lector,escritor);
		
		stdIn.close();
		escritor.close();
		lector.close();
		socket.close();
	}

	
}
