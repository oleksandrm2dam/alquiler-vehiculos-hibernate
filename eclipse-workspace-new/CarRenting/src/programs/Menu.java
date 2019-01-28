package programs;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import tables.Client;

public class Menu {

	private Program program;
	private Scanner scanner;

	public Menu() {
		program = new Program();
		scanner = new Scanner(System.in);
	}

	
	//1 Add new Client
	public Integer addNewClient() {
		Integer id = null;
		
		System.out.println("");
		
		return id;
	}
	
	
	//2 Add new Car
	
	
	
	// 6.1 Consult Client data
	public void consultClientByDni(String dni) {
		Session session = program.factory.openSession();
		Transaction tx = null;
		boolean bool = false;

		try {
			tx = session.beginTransaction();
			List<Client> clients = session.createQuery("from Client").list();
			for (Iterator iterator1 = clients.iterator(); iterator1.hasNext();) {
				Client client = (Client) iterator1.next();

				if (client.getDni().equals(dni)) {
					bool = true;
					System.out.println("name: " + client.getName());
					System.out.println("adress: " + client.getAdress());
					System.out.println("telephone: " + client.getTelephone());
				}
			}
			if (!bool) {
				System.out.println("Client not found");
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	
	public static void main(String[] args) {
		Menu menu = new Menu();

		// operations
		/*
		 * program.addClient("Alexander Malyga", "calle Pueblo Nuevo 2, 1D",
		 * "X5116697W", "622087123", null); program.addClient("Yevgeny Chaynykov",
		 * "calle Cardeña 2, 4B", "X6007320L", "631790666", null);
		 */

		 System.out.println(menu.program.clientExists("X5116697W"));

	}

}
