package generator;

import java.io.IOException;

import clienteSeguridad.Cliente;
import uniandes.gload.core.Task;

public class ClientServerTask extends Task
{

	@Override
	public void fail() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void success() {
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
