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

	// 1 Add new Client
	public void addNewClient() {
		Client new_client = new Client();

		System.out.println("Input client Name");
		new_client.setName(scanner.next());
		System.out.println("Input client Adress");
		new_client.setAdress(scanner.next());
		System.out.println("Input client DNI");
		new_client.setDni(scanner.next());
		System.out.println("Input client Telephone");
		new_client.setTelephone(scanner.next());

		if (!program.addClient(new_client)) {
			System.out.println("There is already a client with this DNI");
		} else {
			System.out.println("New client sucefully added");
		}
	}

	// 2 Add new Car
	public void addNewCar() {
		Car new_car = new Car();

		System.out.println("Input car Plate number");
		new_car.setPlateNumber(scanner.next());
		System.out.println("Input car Brand");
		new_car.setBrand(scanner.next());
		System.out.println("Input car Model");
		new_car.setModel(scanner.next());
		System.out.println("Input car Color");
		new_car.setColor(scanner.next());

		if (!program.addCar(new_car)) {
			System.out.println("There is already a car with this Plate number");
		} else {
			System.out.println("New car sucefully added");
		}
	}

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
		if (client == null) {
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
			if (newCar == null) {
				System.out.println("ERROR: Car with specified plate number does not exist.");
			} else {
				if (program.isCarReservedOnDate(newCar, startDate, endDate)) {
					System.out.println("ERROR: This car is already reserved in the specified dates.");
				} else {
					cars.add(newCar);
				}
			}
			System.out.println("Add another car? (Y/n): ");
			answ = scanner.nextLine();
		} while (!answ.toLowerCase().equals("n"));

		if (cars.isEmpty()) {
			System.out.println("ERROR: No cars selected.");
			return;
		}

		reservation = new Reservation(client, startDate, endDate, cars);
		program.addReservation(reservation);
		System.out.println("Reservation added successfully!");
	}

	// 6.1 Consult Client data
	public void consultClientByDni() {
		System.out.println("Input client DNI");
		String dni = scanner.next();

		if (program.findClient(dni) != null) {
			program.consultClient(dni);
		} else {
			System.out.println("Client not found");
		}
	}

	// 6.2 Consult Cars by Brand
	public void consultCarsByBrand() {
		System.out.println("Input cars Brand");
		String brand = scanner.next();

		program.consultCars(brand);
	}

	// 6.3 Consult client reservations
	public void consultReservationsByDni() {
		System.out.println("Input client DNI");
		String dni = scanner.next();

		if (program.findClient(dni) != null) {
			program.consultReservationsByClient(dni);
		} else {
			System.out.println("Client not found");
		}
	}

	// 6.4 Consult reservation by date
	public void consultReservationByDate() {
		System.out.println("Type the date (dd-mm-yyyy): ");
		String dateString = scanner.nextLine();
		Date date = null;

		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException e) {
			System.out.println("ERROR: Wrong format.");
			return;
		}

		program.consultReservationsByDate(date);
	}

	public static void main(String[] args) {
		Menu menu = new Menu();

		// operations
		/*
		 * program.addClient("Alexander Malyga", "calle Pueblo Nuevo 2, 1D",
		 * "X5116697W", "622087123", null); program.addClient("Yevgeny Chaynykov",
		 * "calle Cardeña 2, 4B", "X6007320L", "631790666", null);
		 */

		menu.program.consultClient("X6007320L");

	}

}
