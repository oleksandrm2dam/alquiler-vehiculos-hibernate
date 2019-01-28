package programs;

import java.util.*;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;

import tables.Car;
import tables.Client;

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
			if (clientExists(new_client.getDni())) {
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
			if (carExists(new_car.getPlateNumber())) {
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
	protected boolean clientExists(String dni) {
		Session session = factory.openSession();
		Transaction tx = null;
		boolean bool = false;

		try {
			tx = session.beginTransaction();
			List<Client> clients = session.createQuery("from Client").list();
			for (Iterator iterator = clients.iterator(); iterator.hasNext();) {
				Client client = (Client) iterator.next();
				if (client.getDni().equals(dni)) {
					bool = true;
				}
			}
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return bool;
	}

	// Method to know if exist an car in database by plate number
	protected boolean carExists(String plateNumber) {
		Session session = factory.openSession();
		Transaction tx = null;
		boolean bool = false;

		try {
			tx = session.beginTransaction();
			List<Car> cars = session.createQuery("from Car").list();
			for (Iterator iterator = cars.iterator(); iterator.hasNext();) {
				Car car = (Car) iterator.next();
				if (car.getPlateNumber().equals(plateNumber)) {
					bool = true;
				}
			}
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return bool;
	}

}
