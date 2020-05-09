package generator;

import java.io.IOException;

import clienteSeguridad.Cliente;
import uniandes.gload.core.Task;


public class ClientServerTask extends Task
{
	private int contExito=0;
	private int contFalla=0;

	@Override
	public void fail() {
		contFalla++;
		System.out.println("Fallo en cliente, van "+contFalla);
		
	}

	@Override
	public void success() {
		contExito++;
		System.out.println("Fallo en cliente, van "+contExito);

		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() 
	{
		Cliente cliente =new Cliente();
		try {
			cliente.procesar();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
