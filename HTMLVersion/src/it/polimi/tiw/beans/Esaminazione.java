package it.polimi.tiw.beans;

public class Esaminazione {
	private int id;
	private User studente;
	private Esame esame;
	private Corso corso;
	private int idVerbale;
	private String voto;
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

	public Corso getCorso() {
		return corso;
	}

	public void setCorso(Corso corso) {
		this.corso = corso;
	}
	
	public boolean isRifiutabile() {
		return voto.equals("30 e Lode") || 
				( voto != null 
					&& !voto.equals("rimandato") 
					&& !voto.equals("riprovato") 
					&& !voto.equals("assente")
					&& !stato.equals("verbalizzato")
					&& !stato.equals("rifiutato")
				);
	}
}
