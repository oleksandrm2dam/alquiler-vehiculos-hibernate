package programs;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.query.Query;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
	public void addClient(Client new_client) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.save(new_client);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	// Method to add an car record in the database
	public void addCar(Car new_car) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.save(new_car);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	//
	protected Integer addReservation(Reservation reservation) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer reservationId = null;

		try {
			tx = session.beginTransaction();
			reservationId = (Integer) session.save(reservation);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return reservationId;
	}
	
	// Method that returns a client with the specified DNI if it exists
	protected Client findClient(String dni) {
		Session session = factory.openSession();
		Transaction tx = null;
		Client client = null;

		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Client where dni = :dni");
			q.setParameter("dni", dni);
			List<Client> clients = q.getResultList();
			if (clients != null && clients.size() > 0) {
				client = clients.get(0);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return client;
	}

	// Method that returns a car with the specified plate number if it exists
	protected Car findCar(String plateNumber) {
		Session session = factory.openSession();
		Transaction tx = null;
		Car car = null;

		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Car where plateNumber = :plateNumber");
			q.setParameter("plateNumber", plateNumber);
			List<Car> cars = q.getResultList();
			if (cars != null && cars.size() > 0) {
				car = cars.get(0);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return car;
	}

	// Method that returns a reservation with the specified ID if it exists
	protected Reservation findReservation(Integer idreservation) {
		Session session = factory.openSession();
		Transaction tx = null;
		Reservation reservation = null;

		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Reservation where idreservation = :idreservation");
			q.setParameter("idreservation", idreservation);
			List<Reservation> reservations = q.getResultList();
			if (reservations != null && reservations.size() > 0) {
				reservation = reservations.get(0);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return reservation;
	}

	// Method that returns a list of cars of the specified brand
	public List<Car> consultCars(String brand) {
		Session session = factory.openSession();
		Transaction tx = null;
		List<Car> cars = null;
		
		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Car where brand = :brand");
			q.setParameter("brand", brand);
			cars = q.getResultList();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) tx.rollback();
		} finally {
			session.close();
		}
		return cars;
	}

	// Method that returns a list of reservations of the specified client
	public List<Reservation> consultReservationsByClient(String dni) {
		Session session = factory.openSession();
		Transaction tx = null;
		List<Reservation> reservations = null;
		
		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Reservation as r where r.client ="
					+ "(from Client where dni = :dni)");		
			q.setParameter("dni", dni);
			reservations = q.getResultList();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) tx.rollback();
		} finally {
			session.close();
		}
		return reservations;
	}
	
	// Method that returns a list of reservations within the specified date
	public ArrayList<Reservation> consultReservationsByDate(Date date) {
		Session session = factory.openSession();
		Transaction tx = null;
		ArrayList<Reservation> list = new ArrayList<Reservation>();
		
		try {
			tx = session.beginTransaction();
			List<Reservation> reservations = session.createQuery("from Reservation").list();
			for (Reservation reservation : reservations) {
				if (date.after(reservation.getStartDate()) && date.before(reservation.getEndDate())) {
					list.add(reservation);
				}
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) tx.rollback();
		} finally {
			session.close();
		}
		return list;
	}

	// Method that returns true if the specified car is reserved between the specified dates
	protected boolean isCarReservedOnDate(Car car, Date startDate, Date endDate) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			List<Reservation> reservations = session.createQuery("FROM Reservation").list();
			for (Reservation reservation : reservations) {
				for (Car currentCar : (Set<Car>) reservation.getCars()) {
					if (currentCar.getPlateNumber().equals(car.getPlateNumber())) {
						if (startDate.after(reservation.getStartDate()) && startDate.before(reservation.getEndDate())) {
							tx.commit();
							session.close();
							return true;
						}
						if (endDate.after(reservation.getStartDate()) && endDate.before(reservation.getEndDate())) {
							tx.commit();
							session.close();
							return true;
						}
					}
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
		return false;
	}
	
	// Method that deletes the specified client from the database
	protected void deleteClient(Client client) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.delete(client);
			tx.commit();
		} catch (HibernateException e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	// Method that deletes the specified car from the database
	protected void deleteCar(Car car) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.delete(car);
			tx.commit();
		} catch (HibernateException e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	// Method that deletes the specified reservation from the database
	protected void deleteReservation(Reservation reservation) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.delete(reservation);
			tx.commit();
		} catch (HibernateException e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	// Method that updates the specified client
	protected void updateClient(Client client) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.update(client);
			tx.commit();
		} catch (HibernateException e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
}
