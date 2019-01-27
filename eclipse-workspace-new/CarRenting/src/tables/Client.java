package tables;
// Generated 27-ene-2019 20:12:02 by Hibernate Tools 5.2.11.Final

import java.util.HashSet;
import java.util.Set;

/**
 * Client generated by hbm2java
 */
public class Client implements java.io.Serializable {

	private Integer idclient;
	private String name;
	private String adress;
	private String dni;
	private String telephone;
	private Set reservations = new HashSet(0);

	public Client() {
	}

	public Client(String name, String adress, String dni, String telephone, Set reservations) {
		this.name = name;
		this.adress = adress;
		this.dni = dni;
		this.telephone = telephone;
		this.reservations = reservations;
	}

	public Integer getIdclient() {
		return this.idclient;
	}

	public void setIdclient(Integer idclient) {
		this.idclient = idclient;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdress() {
		return this.adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getDni() {
		return this.dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Set getReservations() {
		return this.reservations;
	}

	public void setReservations(Set reservations) {
		this.reservations = reservations;
	}

}
