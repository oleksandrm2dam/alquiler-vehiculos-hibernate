package programs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import tables.Car;
import tables.Client;
import tables.Reservation;

public class Menu {

	private Program program;
	private Scanner scanner;
	private SimpleDateFormat dateFormat;

	public Menu() {
		program = new Program();
		scanner = new Scanner(System.in);
		dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	}

	
	//1 Add new Client
	public Integer addNewClient() {
		Integer id = null;
		
		System.out.println("");
		
		return id;
	}
	
	
	//2 Add new Car
	
	// 3 Add a reservation
	public void addNewReservation() {
		Reservation reservation;
		Client client;
		Date startDate;
		Date endDate;
		Set cars = new HashSet(0);
		
		System.out.println("Type the client's DNI: ");
		String dni = scanner.nextLine();
		client = program.findClient(dni);
		if(client == null) {
			System.out.println("ERROR: Specifiend client does not exist.");
			return;
		}
		
		System.out.println("Type the start date (dd-mm-yyyy): ");
		String startDateString = scanner.nextLine();
		try {
			startDate = dateFormat.parse(startDateString);
		} catch (ParseException e) {
			System.out.println("ERROR: Wrong format.");
			return;
		}
		
		System.out.println("Type the end date (dd-mm-yyyy): ");
		String endDateString = scanner.nextLine();
		try {
			endDate = dateFormat.parse(endDateString);
		} catch (ParseException e) {
			System.out.println("ERROR: Wrong format.");
			return;
		}
		
		String answ;
		do {
			System.out.println("Type the plate number of a car you want to rent: ");
			String plateNumber = scanner.nextLine();
			Car newCar = program.findCar(plateNumber);
			if(newCar == null) {
				System.out.println("ERROR: Car with specified plate number does not exist.");
			} else {
				if(program.isCarReservedOnDate(newCar, startDate, endDate)) {
					System.out.println("ERROR: This car is already reserved in the specified dates.");
				} else {
					cars.add(newCar);
				}
			}
			System.out.println("Add another car? (Y/n): ");
			answ = scanner.nextLine();
		} while (!answ.toLowerCase().equals("n"));
		
		if(cars.isEmpty()) {
			System.out.println("ERROR: No cars selected.");
			return;
		}
		
		reservation = new Reservation(client, startDate, endDate, cars);
		program.addReservation(reservation);
		System.out.println("Reservation added successfully!");
	}
	
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

		 // System.out.println(menu.program.clientExists("X5116697W"));

	}

}
