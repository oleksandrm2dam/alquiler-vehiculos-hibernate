
import java.sql.*;

public class Connect {

	public static void main(String[] args) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/car_rent", "root", "");
			
			Statement query = con.createStatement();
			ResultSet rs = query.executeQuery("SELECT * FROM tabla");
			
			while (rs.next()) {
				System.out.println("Connected");
			}
			System.out.println("Connected");
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
