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
			if (tx != null)
				tx.rollback();
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
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
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

	// Method that returns a reservation with the specified Id, if it exists, null
	// if not
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
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}

	// Method to show specific client by DNI
	public Client consultClient(String dni) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Client where dni = :dni");
			q.setParameter("dni", dni);
			Client client = (Client) q.getSingleResult();
			tx.commit();
			return client;
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
		}
		return null;
	}

	// Method to show all cars with specific brand
	public List<Car> consultCars(String brand) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Car where brand = brand");
			q.setParameter("brand", brand);
			List<Car> cars = q.getResultList();
			tx.commit();
			return cars;
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
		}
		return null;
	}

	// Method to show all reservations of specific client
	public List<Reservation> consultReservationsByClient(String dni) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Reservation as r where r.client ="
					+ "(from Client where dni = :dni)");		
			q.setParameter("dni", dni);
			List<Reservation> reservations = q.getResultList();
			tx.commit();
			return reservations;
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
		}
		return null;
	}
	
	//Method to show car plate number and DNI of client that made reservation on specific date
	public List<Reservation> consultReservationsByDate(Date date) {
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
			return list;
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
		}
		return null;
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
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return reservationId;
	}

	//
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
							return true;
						}
						if (endDate.after(reservation.getStartDate()) && endDate.before(reservation.getEndDate())) {
							tx.commit();
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
