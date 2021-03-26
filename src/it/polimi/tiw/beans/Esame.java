package it.polimi.tiw.beans;

import java.util.Date;

public class Esame {

	private int id;
	private Corso corso;	// temporaneo, poterbbe non servire
	private Date dataAppello;
	
	public Esame() {
		// Default constructor
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Corso getCorso() {
		return corso;
	}
	
	public void setCorso(Corso corso) {
		this.corso = corso;
	}
	
	public Date getDataAppello() {
		return dataAppello;
	}
	
	public void setDataAppello(Date dataAppello) {
		this.dataAppello = dataAppello;
	}
	
}
