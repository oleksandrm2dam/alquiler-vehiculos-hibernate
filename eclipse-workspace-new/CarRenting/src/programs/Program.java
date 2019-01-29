package programs;

import java.util.*;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;

import tables.Car;
import tables.Client;
import tables.Reservation;

public class Program {

	protected SessionFactory factory;

	public Program() {
		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	// Method to add an client record in the database
	public Integer addClient(Client new_client) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer clientID = null;

		try {
			tx = session.beginTransaction();

			// check if dni exist
			if (findClient(new_client.getDni()) != null) {
				clientID = -1;
			} else {
				clientID = (Integer) session.save(new_client);
				tx.commit();
			}

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return clientID;
	}

	// Method to add an car record in the database
	public Integer addCar(Car new_car) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer carID = null;

		try {
			tx = session.beginTransaction();

			// check if plate exist
			if (findCar(new_car.getPlateNumber()) != null) {
				carID = -1;
			} else {
				carID = (Integer) session.save(new_car);
				tx.commit();
			}

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return carID;
	}

	// Method to know if exist an client in database by DNI
	protected Client findClient(String dni) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			List<Client> clients = session.createQuery("from Client").list();
			for (Iterator iterator = clients.iterator(); iterator.hasNext();) {
				Client client = (Client) iterator.next();
				if (client.getDni().equals(dni)) {
					tx.commit();
					return client;
				}
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}

	// Method to know if exist an car in database by plate number
	protected Car findCar(String plateNumber) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			List<Car> cars = session.createQuery("from Car").list();
			for (Iterator iterator = cars.iterator(); iterator.hasNext();) {
				Car car = (Car) iterator.next();
				if (car.getPlateNumber().equals(plateNumber)) {
					tx.commit();
					return car;
				}
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}
	
	// Method that returns a reservation with the specified Id, if it exists, null if not
	protected Reservation findReservation(Integer reservationId) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			List<Reservation> reservations = session.createQuery("from Reservation").list();
			for (Iterator<Reservation> iterator = reservations.iterator(); iterator.hasNext();) {
				Reservation reservation = (Reservation) iterator.next();
				if (reservation.getIdreservation().equals(reservationId)) {
					tx.commit();
					return reservation;
				}
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) tx.rollback();	
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}
	
	protected Integer addReservation(Reservation reservation) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer reservationId = null;
		
		try {
			tx = session.beginTransaction();
			reservationId = (Integer) session.save(reservation);
			tx.commit();
		} catch (HibernateException e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return reservationId;
	}
	
	protected boolean isCarReservedOnDate(Car car, Date startDate, Date endDate) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			List<Reservation> reservations = session.createQuery("FROM Reservation").list();
			for(Reservation reservation : reservations) {
				for(Car currentCar : (Set<Car>) reservation.getCars()) {
					if(currentCar.getPlateNumber().equals(car.getPlateNumber())) {
						if(startDate.after(reservation.getStartDate()) && startDate.before(reservation.getEndDate())) {
							tx.commit();
							return true;
						}
						if(endDate.after(reservation.getStartDate()) && endDate.before(reservation.getEndDate())) {
							tx.commit();
							return true;
						}
					}
				}
			}
			tx.commit();
		} catch (HibernateException e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return false;
	}

}
