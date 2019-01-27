package programs;

import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import tables.*;
import utils.HibernateUtils;

public class Prueba {

	private static Session session;
	
	public static void main(String[] args) {
		
		session = HibernateUtils.getSessionFactory().openSession();
		Transaction ts = session.beginTransaction();
		
		createCar("123", "brand", "model", "color", null);
		
		ts.commit();
		session.close();
		
		System.exit(0);
	}
	
	public static void createCar(String pl, String brand, String model, String color, Set reservations) {
		Car car = new Car(pl, brand, model, color, reservations);
		session.save(car);
	}

}
