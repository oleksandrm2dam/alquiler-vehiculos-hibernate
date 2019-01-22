package programs;

import org.hibernate.Session;
import org.hibernate.Transaction;

import tables.Car;
import utils.HibernateUtils;

public class Prueba {

	private static Session session;
	
	public static void main(String[] args) {
		
		session = HibernateUtils.getSessionFactory().openSession();
		Transaction ts = session.beginTransaction();
		
		createCar("123", "brand", "model", "color");
		
		ts.commit();
		session.close();
		
		System.exit(0);
	}
	
	public static void createCar(String pl, String brand, String model, String color) {
		Car car = new Car(pl, brand, model, color);
		session.save(car);
	}

}
