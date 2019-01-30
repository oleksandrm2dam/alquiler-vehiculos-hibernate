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
	
	public void startMenu() {
		String answ;
		do {
			printMenu();
			answ = scanner.nextLine();
			switch(answ) {
			case "1":
				addNewClient();
				break;
			case "2":
				addNewCar();
				break;
			case "3":
				addNewReservation();
				break;
			case "4":
				deleteClient();
				break;
			case "5":
				deleteCar();
				break;
			case "6":
				deleteReservation();
				break;
			case "7":
				updateClient();
				break;
			case "8":
				break;
			case "9":
				break;
			case "10":
				break;
			case "11":
				break;
			case "12":
				System.out.println("BYE!");
				scanner.close();
				program.factory.close();
				break;
			default:
				System.out.println("ERROR: Option not valid.");
				break;
			}
		} while (!answ.equals("12"));
	}
	
	private void printMenu() {
		String menu = "---MENU---\n";
		menu += "1) Add new client\n";
		menu += "2) Add new car\n";
		menu += "3) Add new reservation\n";
		menu += "4) Delete client\n";
		menu += "5) Delete car\n";
		menu += "6) Delete reservation\n";
		menu += "7) Modify client\n";
		menu += "8) Search for client\n";
		menu += "9) Search for car\n";
		menu += "10) Search for reservations by DNI\n";
		menu += "11) Search for cars reserved at a date\n";
		menu += "12) EXIT.\n";
		System.out.println(menu);
	}

	// 1 Add new Client
	public void addNewClient() {
		Client new_client = new Client();

		System.out.println("Input client Name");
		new_client.setName(scanner.nextLine());
		System.out.println("Input client Adress");
		new_client.setAdress(scanner.nextLine());
		System.out.println("Input client DNI");
		new_client.setDni(scanner.nextLine());
		System.out.println("Input client Telephone");
		new_client.setTelephone(scanner.nextLine());

		if (program.findClient(new_client.getDni()) != null) {
			System.out.println("There is already a client with this DNI");
		} else {
			program.addClient(new_client);
			System.out.println("New client successfully added");
		}
	}

	// 2 Add new Car
	public void addNewCar() {
		Car new_car = new Car();

		System.out.println("Input car Plate number");
		new_car.setPlateNumber(scanner.nextLine());
		System.out.println("Input car Brand");
		new_car.setBrand(scanner.nextLine());
		System.out.println("Input car Model");
		new_car.setModel(scanner.nextLine());
		System.out.println("Input car Color");
		new_car.setColor(scanner.nextLine());

		if (program.findCar(new_car.getPlateNumber()) != null) {
			System.out.println("There is already a car with this Plate number");
		} else {
			program.addCar(new_car);
			System.out.println("New car successfully added");
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
	
	// 4 Delete clients
	public void deleteClient() {
		System.out.println("Type the client's DNI: ");
		String dni = scanner.nextLine();
		Client client = program.findClient(dni);
		if(client == null) {
			System.out.println("ERROR: Client not found.");
			return;
		} else {
			System.out.println(client.toString());
		}
		
		System.out.println("Are you sure you want to delete the client? (y/n): ");
		if(scanner.nextLine().toLowerCase().equals("y")) {
			program.deleteClient(client);
			System.out.println("Client deleted successfully!");
		} else {
			System.out.println("Client not deleted.");
		}
	}
	
	// 4 Delete cars
	public void deleteCar() {
		System.out.println("Type the car's plate number: ");
		String plateNumber = scanner.nextLine();
		Car car = program.findCar(plateNumber);
		if(car == null) {
			System.out.println("ERROR: Car not found.");
			return;
		} else {
			System.out.println(car.toString());
		}
		
		System.out.println("Are you sure you want to delete the car? (y/n): ");
		if(scanner.nextLine().toLowerCase().equals("y")) {
			program.deleteCar(car);
			System.out.println("Car deleted successfully!");
		} else {
			System.out.println("Car not deleted.");
		}
	}
	
	// 4 Delete reservations
	public void deleteReservation() {
		System.out.println("Type the reservation's id: ");
		Integer reservationId = Integer.parseInt(scanner.nextLine());
		Reservation reservation = program.findReservation(reservationId);
		if(reservation == null) {
			System.out.println("ERROR: Reservation not found.");
			return;
		} else {
			System.out.println(reservation.toString());
		}
		
		System.out.println("Are you sure you want to delete the reservation? (y/n): ");
		if(scanner.nextLine().toLowerCase().equals("y")) {
			program.deleteReservation(reservation);
			System.out.println("Reservation deleted successfully!");
		} else {
			System.out.println("Reservation not deleted.");
		}
	}
	
	// 5 Update client
	public void updateClient() {
		System.out.println("Type the client's DNI: ");
		String dni = scanner.nextLine();
		Client client = program.findClient(dni);
		if(client == null) {
			System.out.println("ERROR: Client not found.");
			return;
		} else {
			System.out.println(client.toString());
		}
		
		System.out.println("What do you want to modify?");
		System.out.println("1) Phone number");
		System.out.println("2) Address");
		String answ = scanner.nextLine();
		
		if(answ.equals("1")) {
			System.out.println("Type the new phone number:");
			client.setTelephone(scanner.nextLine());
		} else {
			if(answ.equals("2")) {
				System.out.println("Type the new address:");
				client.setAdress(scanner.nextLine());
			} else {
				System.out.println("ERROR: Option not valid.");
				System.out.println("Client not modified.");
				return;
			}
		}
		program.updateClient(client);
	}

	// 6.1 Consult Client data
	public void consultClientByDni() {
		System.out.println("Input client DNI");
		String dni = scanner.nextLine();

		if (program.findClient(dni) != null) {
			program.consultClient(dni);
		} else {
			System.out.println("Client not found");
		}
	}

	// 6.2 Consult Cars by Brand
	public void consultCarsByBrand() {
		System.out.println("Input cars Brand");
		String brand = scanner.nextLine();

		program.consultCars(brand);
	}

	// 6.3 Consult client reservations
	public void consultReservationsByDni() {
		System.out.println("Input client DNI");
		String dni = scanner.nextLine();

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
		menu.startMenu();
		

		// operations
		/*
		 * program.addClient("Alexander Malyga", "calle Pueblo Nuevo 2, 1D",
		 * "X5116697W", "622087123", null); program.addClient("Yevgeny Chaynykov",
		 * "calle Cardeña 2, 4B", "X6007320L", "631790666", null);
		 */

		// menu.program.consultClient("X6007320L");

	}

}
