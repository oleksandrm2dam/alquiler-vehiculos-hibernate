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
	
	// Method to know if exist an client in database by DNI
	protected Client findClient(String dni) {
		Session session = factory.openSession();
		Transaction tx = null;
		Client client = null;

		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Client where dni = :dni");
			q.setParameter("dni", dni);
			List<Client> clients = q.getResultList();
			if (clients.size() > 0) {
				client = clients.get(0);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return client;
	}

	// Method to know if exist an car in database by plate number
	protected Car findCar(String plateNumber) {
		Session session = factory.openSession();
		Transaction tx = null;
		Car car = null;

		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Car where plateNumber = :plateNumber");
			q.setParameter("plateNumber", plateNumber);
			List<Car> cars = q.getResultList();
			if (cars.size() > 0) {
				car = cars.get(0);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return car;
	}

	// Method that returns a reservation with the specified Id, if it exists, null
	// if not
	protected Reservation findReservation(Integer idreservation) {
		Session session = factory.openSession();
		Transaction tx = null;
		Reservation reservation = null;

		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Reservation where idreservation = :idreservation");
			q.setParameter("idreservation", idreservation);
			List<Reservation> reservations = q.getResultList();
			if (reservations.size() > 0) {
				reservation = reservations.get(0);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return reservation;
	}

	// Method to show all cars with specific brand
	public List<Car> consultCars(String brand) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("from Car where brand = :brand");
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
			return list;
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
		}
		return null;
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
