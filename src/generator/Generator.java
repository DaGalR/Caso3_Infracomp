package generator;

import java.util.Scanner;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {
	
	private static LoadGenerator generator;
	
	public static void main(String[] args) throws Exception {

		Scanner sc = new Scanner(System.in);
		System.out.println("Por favor escriba el número de tareas a ejecturar");
		int numTasks = sc.nextInt();
		System.out.println("Por favor escriba el tiempo entre tareas (ms)");
		int tiempoEntreTareas = sc.nextInt();
		System.out.println("Por favor indica que tipo de cliente quieres correr (1 seguridad, 0 sin seguridad)");
		int tipoCliente = sc.nextInt();
		Task tarea = null;

		if(tipoCliente == 1) {
			tarea = new ClientServerTask();

		}
		else if(tipoCliente == 0) {
			tarea = new ClientServerNSTask(); 
		}
		else {
			throw new Exception("No seleccionaste algo valido");
		}
		generator = new LoadGenerator("Client-Server load test", numTasks, tarea, tiempoEntreTareas);
		generator.generate();
		sc.close();
	}
	
}
