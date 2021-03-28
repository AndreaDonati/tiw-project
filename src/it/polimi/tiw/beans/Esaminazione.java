package it.polimi.tiw.beans;

public class Esaminazione {
	private int id;
	private User studente;
	private Esame esame;
	private int idVerbale;
	private String voto;
	private int lode; // ?
	private String stato;
	
	public Esaminazione() {
		// Default constructor
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setStudente(User studente) {
		this.studente = studente;
	}
	
	public User getStudente() {
		return studente;
	}
	
	public void setEsame(Esame esame) {
		this.esame = esame;
	}
	
	public Esame getEsame() {
		return esame;
	}
	
	public void setIdVerbale(int idVerbale) {
		this.idVerbale = idVerbale;
	}
	
	public int getIdVerbale() {
		return idVerbale;
	}
	
	public void setVoto(String voto) {
		this.voto = voto;
	}
	
	public String getVoto() {
		return voto;
	}
	
	public void setStato(String stato) {
		this.stato = stato;
	}
	
	public String getStato() {
		return stato;
	}
}
