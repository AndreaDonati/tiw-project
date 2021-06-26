package it.polimi.tiw.beans;

public class Esame {

	private int id;
	private Corso corso;
	private String dataAppello;
	
	public Esame() {}
	
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
	
	public String getDataAppello() {
		return dataAppello;
	}
	
	public void setDataAppello(String dataAppello) {
		this.dataAppello = dataAppello;
	}
	
}
