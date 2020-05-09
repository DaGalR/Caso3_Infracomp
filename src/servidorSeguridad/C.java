package servidorSeguridad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Monitor.Monitor;
import servidorSeguridad.D;


public class C {
	private static ServerSocket ss;	
	private static final String MAESTRO = "MAESTRO: ";
	private static X509Certificate certSer; /* acceso default */
	private static KeyPair keyPairServidor; /* acceso default */
	public static int contInst;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		
		System.out.println(MAESTRO + "Establezca puerto de conexion:");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		int ip = Integer.parseInt(br.readLine());
		System.out.println(MAESTRO + "Empezando servidor maestro en puerto " + ip);
		// Adiciona la libreria como un proveedor de seguridad.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		

		// Crea el archivo de log
		File file = null;
		keyPairServidor = S.grsa();
		certSer = S.gc(keyPairServidor); 
		String ruta = "./resultados.txt";

		file = new File(ruta);
		if (!file.exists()) {
			file.createNewFile();
		}


		D.init(certSer, keyPairServidor,file);

		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println("Por favor introduzca el número máximo de threads que quiere en el pool ");
		int nThreads = Integer.parseInt(br.readLine());

		//Crea pool de threads con par�metro recibido de consola
		ExecutorService pool = Executors.newFixedThreadPool(nThreads);

		System.out.println(MAESTRO + "Socket creado.");

		for (int contInst=0;true;contInst++) {
			//System.out.println("Contador en for" + contInst);
			try { 
				Socket sc = ss.accept();
				pool.execute(new D(sc, contInst));
				System.out.println(MAESTRO + "Cliente " + contInst + " aceptado.");
				//D d = new D(sc,i);
				//d.start();
			} catch (IOException e) {
				System.out.println(MAESTRO + "Error creando el socket cliente.");
				e.printStackTrace();
			}
		}
		

	}

 
}
