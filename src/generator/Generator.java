package generator;

import java.util.Scanner;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {
	
	private static LoadGenerator generator;
	
	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		System.out.println("Por favor escriba el número de tareas a ejecturar");
		int numTasks = sc.nextInt();
		System.out.println("Por favor escriba el tiempo entre tareas (ms)");
		int tiempoEntreTareas = sc.nextInt();
		Task tarea = new ClientServerTask();
		generator = new LoadGenerator("Client-Server load test", numTasks, tarea, tiempoEntreTareas);
		generator.generate();
		sc.close();
	}
	
}
