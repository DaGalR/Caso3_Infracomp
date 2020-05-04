package clienteSeguridad;


import java.security.Key;

import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;

public class Cifrado 
{
	private static final String PADDING_AES = "AES/ECB/PKCS5Padding";

	/**
	 * Cifra simetricamente o asimetricamente
	 * @param llave llave para usar 
	 * @param algoritmo para descifrar
	 * @param texto entrada a descifrar
	 * @param tipo true si es para cifrar asimetrico, false de lo contrario
	 * @return
	 */
	public static byte[] cifrar(Key llave, String algoritmo, String texto, boolean tipo )
	{
		byte [] textoCifrado;
		try 
		{
			if(tipo) {
				
				Cipher cifrador = Cipher.getInstance(algoritmo);
				byte[] textoBytes= DatatypeConverter.parseBase64Binary(texto);

				cifrador.init(Cipher.ENCRYPT_MODE, llave);
				textoCifrado = cifrador.doFinal(textoBytes);
				return textoCifrado;
			}
			else {
				Cipher cifrador = Cipher.getInstance(PADDING_AES);
				byte[] textoClaro = texto.getBytes();
				
				cifrador.init(Cipher.ENCRYPT_MODE, llave);
				textoCifrado = cifrador.doFinal(textoClaro);
				
				return textoCifrado;
			}
		}
		catch(Exception e)
		{
			System.out.println("Excepcion: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Descifra simetricamente o asimetricamente
	 * @param llave llave para usar 
	 * @param algoritmo para descifrar
	 * @param texto entrada a descifrar
	 * @param tipo true si es para descifrar asimetrico, false de lo contrario
	 * @return retorna el mesaje decifrado
	 */
	public static byte[] descifrar(Key llave, String algoritmo, byte[] texto, boolean tipo )
	{
		byte [] textoBytes;

		try
		{
			if(tipo) {
				Cipher cifrador = Cipher.getInstance(algoritmo);
				cifrador.init(Cipher.DECRYPT_MODE, llave);
				textoBytes = cifrador.doFinal(texto);
				return textoBytes;
			}
			else {
				Cipher cifrador = Cipher.getInstance(PADDING_AES);
				cifrador.init(Cipher.DECRYPT_MODE, llave);
				textoBytes = cifrador.doFinal(texto);
				return textoBytes;
			}
		}
		catch(Exception e)
		{
			System.out.println("Excepcion: " + e.getMessage());
			return null;
		}
	}


}
