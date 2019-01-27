package programs;

import java.util.Iterator;
import java.util.Set;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.List;

import tables.Client;

public class Program {

	private static SessionFactory factory;

	public static void main(String[] args) {

		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

		Program program = new Program();

		// operations
		/*program.addClient("Alexander Malyga", "calle Pueblo Nuevo 2, 1D", "X5116697W", "622087123", null);
		program.addClient("Yevgeny Chaynykov", "calle Cardeña 2, 4B", "X6007320L", "631790666", null);
		*/
		program.isClient("X5116697W");
	}

	// Method to add an client record in the database
	public Integer addClient(String name, String adress, String dni, String telephone, Set reservations) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer clientID = null;

		try {
			tx = session.beginTransaction();
			Client client = new Client(name, adress, dni, telephone, reservations);
			clientID = (Integer) session.save(client);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return clientID;
	}

	// Method that return list with clients DNI
	private boolean isClient(String dni) {
		Session session = factory.openSession();
		Transaction tx = null;
		boolean bool = false;
		
		try {
			tx = session.beginTransaction();
			java.util.List clients = session.createQuery("select dni from Client").list();
			for (Iterator iterator1 = clients.iterator(); iterator1.hasNext();){
	            Client client = (Client) iterator1.next();
				
	            if (client.getDni() == dni) {
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
			return bool;
		}
	}

}
