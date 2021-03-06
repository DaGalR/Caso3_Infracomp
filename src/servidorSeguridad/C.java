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
		
		ruta= "./medidas.txt";
		File fileMedidas = null;
		fileMedidas = new File(ruta);
		if (!fileMedidas.exists()) {
			try {
				fileMedidas.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		D.init(certSer, keyPairServidor,file,fileMedidas);

		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println("Por favor introduzca el número máximo de threads que quiere en el pool ");
		int nThreads = Integer.parseInt(br.readLine());

		//Crea pool de threads con par�metro recibido de consola
		ExecutorService pool = Executors.newFixedThreadPool(nThreads);

		System.out.println(MAESTRO + "Socket creado.");

		for (int i=0;true;i++) {
			//System.out.println("Contador en for" + contInst);
			try { 
				Socket sc = ss.accept();
				pool.execute(new D(sc, i));
				System.out.println(MAESTRO + "Cliente " + i+ " aceptado.");
				//D d = new D(sc,i);
				//d.start();
			} catch (IOException e) {
				System.out.println(MAESTRO + "Error creando el socket cliente.");
				e.printStackTrace();
			}
			
			synchronized(fileMedidas)
			{
				try {
					FileWriter fw = new FileWriter(fileMedidas,true);
					fw.write("TRANSACCIONES SOLICITADAS:"+  i+1  + "\n");
					fw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		

	}

 
}
