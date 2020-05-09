package clienteSeguridad;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.Certificate;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.crypto.BadPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class ProtocoloCliente {

	//-----------------------------------------------------------------------
	//  M�todos
	//-----------------------------------------------------------------------
	/**
	 * 
	 * @param stdIn canal de comunicaic�n con el usuarios. 
	 * @param pIn canal de comunicaci�n de entrada con el servidor. 
	 * @param pOut canal  de comunaci�n de salida con el servidor.
	 * @throws IOException 
	 */
	public static void procesar(BufferedReader stdIn, BufferedReader pIn,PrintWriter pOut) throws IOException{

		//Variables para guardar en texto, la informaci�n dada por el cliente y el servidor
		String[] algs;
		String resServidor, resCliente, stringSerServidor,cifradoA,cifradoB, algHMACelegido,algAsimElegido, algSimElegido;
		resServidor ="";
		stringSerServidor="";
		cifradoA="";
		cifradoB="";
		algAsimElegido="";
		algSimElegido="";
		algHMACelegido="";

		//Variables para amacenar las llaves
		Key k_scPro=null; //Llave sim�trica
		KeyPairGenerator generator;
		KeyPair keyPair;

		//Contador para ubicar al programa en la secci�n del protocolo que se encuntra. 
		int contadorProtocolo = 0;


		try {
			generator = KeyPairGenerator.getInstance("RSA");
			keyPair = generator.generateKeyPair();
			PublicKey kPubServ=null;
			while(true) 
			{

				//Inicia el programa. Contador en 0 corresponde a saludo y contador en 1 al envio de algoritmos
				if(contadorProtocolo==0 || contadorProtocolo==1)
				{
					System.out.println("INSTRUCCI�N: Escriba el mensaje a enviar al servidor: ");
					resCliente="HOLA";


					// Se saluda al servidor
					if(resCliente.equals("HOLA") && contadorProtocolo==0) 
					{
						pOut.println(resCliente);
					}
					//Se reciben y envian los algoritmos
					else if(contadorProtocolo==1)
					{
						resCliente="ALGORITMOS:AES:RSA:HMACSHA1";
						algs = resCliente.split(":");
						if(algs.length == 4) {
							algSimElegido=algs[1];
							algAsimElegido=algs[2];
							algHMACelegido = algs[3];
							//Thread.sleep(500);
							System.out.println("REPORTE: algoritmo sim�trico elegido " + algSimElegido);
							//Thread.sleep(500);
							System.out.println("REPORTE: algoritmo asim�trico elegido " + algAsimElegido);
							//Thread.sleep(500);
							System.out.println("REPORTE: algoritmo Hmac elegido " + algHMACelegido);
							//Thread.sleep(500);

							resCliente.trim();
							pOut.println(resCliente);
						}
						else {
							System.out.println("INSTRUCCI�N: debe enviar una cadena del estilo ALGORITMOS:ALG1:ALG2:ALG3");
							continue;
						}
					}
					else 
					{
						System.out.println("REPORTE: Error detectado en su mensaje. \nINSTRUCCI�N: intente escribir de nuevo siguiendo el protocolo.");
						continue;
					}
				}
				//Recepci�n y envio de certificados. 
				if(contadorProtocolo == 2 && algHMACelegido != null && algHMACelegido != "") 
				{	
					try {

						X509Certificate certificado = gc(keyPair);
						byte[] cerBytes = certificado.getEncoded();
						String cerString = DatatypeConverter.printBase64Binary(cerBytes);
						System.out.println("REPORTE: Generando su certificado de cliente... " + cerString);
						pOut.println(cerString);
						System.out.println("REPORTE: enviando su certificado de cliente");
						//Thread.sleep(500);
						resServidor=pIn.readLine();
						//Thread.sleep(500);
						System.out.println("RESPUESTA SERVIDOR: " + resServidor);
						//Thread.sleep(500);

						//Si todo salio bien con el certificado de cliente, se rescibe el certificado del servidor
						if(resServidor.equals("OK")) 
						{

							contadorProtocolo++;
							System.out.println("REPORTE: Recibiendo certificado del servidor...");
							resServidor=pIn.readLine();
							stringSerServidor = resServidor;
							//Thread.sleep(500);
							System.out.println("REPORTE: Certificado servidor recibido para procesar... " + stringSerServidor);
							//Thread.sleep(500);
							byte[] cerServByte = DatatypeConverter.parseBase64Binary(stringSerServidor);
							CertificateFactory cf = CertificateFactory.getInstance("X.509");
							X509Certificate c = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cerServByte));
							kPubServ = c.getPublicKey();
							System.out.println("REPORTE: llave publica de servidor: " + kPubServ);
							//Thread.sleep(500);

							boolean valido = verificarCertificado(c);

							//Si el certificado es válido se continua con la ejecuaci�n del progama, se reciben los datos cifrados
							if(valido)
							{

								System.out.println("REPORTE: El certificado recibido del servidor es válido. \nINSTRUCCIÓN: escriba 'OK' para continuar"); 
								resCliente="OK";
								pOut.println(resCliente);

								System.out.println("REPORTE: Recibiendo respuestas del servidor...");
								//Thread.sleep(500);
								cifradoA = pIn.readLine();
								cifradoB = pIn.readLine();
								System.out.println("REPORTE: Recibidos dos cifrados, procesando...");
								System.out.println("CIFRADO A " + cifradoA);
								byte[] k_sc = Cifrado.descifrar(keyPair.getPrivate(), "RSA", DatatypeConverter.parseBase64Binary(cifradoA), true);
								k_scPro = new SecretKeySpec(k_sc, 0, k_sc.length, algSimElegido );
								byte[] retoByte = Cifrado.descifrar(k_scPro, algSimElegido, DatatypeConverter.parseBase64Binary(cifradoB),false);
								String retoString = DatatypeConverter.printBase64Binary(retoByte);
								//Thread.sleep(500);
								System.out.println("REPORTE: Reto descifrado "+ retoString);
								//Thread.sleep(500);

								System.out.println("REPORTE: Cifrando reto...");
								//Thread.sleep(500);

								byte[] retoCifrado = Cifrado.cifrar(kPubServ, algAsimElegido, retoString, true);
								String retoCifradoString = DatatypeConverter.printBase64Binary(retoCifrado);

								System.out.println("REPORTE: Enviando reto cifrado de vuelta: "+ retoCifradoString);
								pOut.println(retoCifradoString);
								//Thread.sleep(500);

								resServidor = pIn.readLine();
								System.out.println("RESPUESTA SERVIDOR: "+ resServidor);
								contadorProtocolo++;
								continue;
							}
							else {
								pOut.println("REPORTE: ERROR su certificado no es v�lido");

							}


						}
						else {
							System.out.println("RESPUESTA SERVIDOR: " + resServidor);
							continue;
						}
						continue;

					} 
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				//Etapa de reporte y manejo de actualización
				if(contadorProtocolo==4 && k_scPro!=null)
				{
					System.out.println("INSTRUCCIÓN: Escriba su identificador (número de 4 dígitos)");
					resCliente="9876";
					byte[] idCifrado = Cifrado.cifrar(k_scPro, algSimElegido, resCliente, false);
					String idCifradoStr=DatatypeConverter.printBase64Binary(idCifrado);
					System.out.println("REPORTE: Enviando al servidor el id cifrado: " + idCifradoStr);
					//Thread.sleep(500);
					
					pOut.println(idCifradoStr );

					//Se recibe la hora
					resServidor=pIn.readLine();
					System.out.println("REPORTE: Se recibe la hora cifrada: " + resServidor);

					byte[] horaBytes = Cifrado.descifrar(k_scPro, algSimElegido, DatatypeConverter.parseBase64Binary(resServidor), false);
					
					String horaStr= DatatypeConverter.printBase64Binary(horaBytes);
					System.out.println(horaStr);

					DateFormat formHora = new SimpleDateFormat("HHmm");
					Date hora = formHora.parse(horaStr);
					//Thread.sleep(500);
					System.out.println("REPORTE: La hora recibida es: " + hora.getHours() + ":" + hora.getMinutes());

					System.out.println("INSTRUCCIÓN: Si todo está bien, escriba OK para terminar. De lo contrario escriba ERROR");
					resCliente="OK";
					pOut.println(resCliente);
					if(resCliente.equals("OK"))
					{
						System.out.println("SALIENDO");
						break;
					}
				}

				resServidor = pIn.readLine();
				if(resServidor.equals("ERROR")) {
					System.out.println("Error detectado desde el servidor, intente escribir de nuevo su respuesta.");
					continue;

				}
				else if(resServidor.equals("OK")) {
					System.out.println("RESPUESTA SERVIDOR: "+ resServidor);
					//Thread.sleep(500);
					contadorProtocolo++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo para generar certificado
	 * @param keyPair par de llaves publicas y privadas
	 * @return retorna el certificado
	 * @throws OperatorCreationException
	 * @throws CertificateException
	 */
	public static X509Certificate gc(KeyPair keyPair) throws OperatorCreationException, CertificateException{

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.add(Calendar.YEAR, 10);
		X509v3CertificateBuilder x509v3CertificateBuilder = new X509v3CertificateBuilder(new X500Name("CN=localhost"), BigInteger.valueOf(1), 		Calendar.getInstance().getTime(), endCalendar.getTime(), new X500Name("CN=localhost"), 		SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));
		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").build(keyPair.getPrivate());
		X509CertificateHolder x509CertificateHolder = x509v3CertificateBuilder.build(contentSigner);
		return new JcaX509CertificateConverter().setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()).getCertificate(x509CertificateHolder);
	}

	/**
	 * M�todo que verifica que el certificado dado por paremtro es válido. 
	 * @param certificado a ser verificado.
	 * @return retorna true si el certifado es válido, false de lo contrario. 
	 */
	public static boolean verificarCertificado(X509Certificate certificado) {
		PublicKey llave = certificado.getPublicKey();
		try {
			certificado.verify(llave);
			return true;
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e) {
			System.out.println("Error verificando certificado " + e.getMessage());
			return false;

		}
	}
}
