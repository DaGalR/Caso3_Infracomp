package generator;

import java.util.Scanner;

import uniandes.gload.core.LoadGenerator;

public class Generator {
	
	private final static LoadGenerator generator;
	
	public void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Por favor escriba el número de tareas a ejecturar");
		int numTasks = sc.nextInt();
		sc.close();
	}
}
