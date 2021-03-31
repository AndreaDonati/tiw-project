package it.polimi.tiw.beans;

import java.util.ArrayList;
import java.util.List;

public class Verbale {
	
	private int id;
	private String dataVerbale;
	private List<Esaminazione> risultati = new ArrayList<Esaminazione>();
	
	public String getDataVerbale() {
		return dataVerbale;
	}
	
	public void setDataVerbale(String dataVerbale) {
		this.dataVerbale = dataVerbale;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public List<Esaminazione> getRisultati() {
		return risultati;
	}

	public void setRisultati(List<Esaminazione> risultati) {
		this.risultati = risultati;
	}
	
	public void addRisultato(Esaminazione risultato) {
		this.risultati.add(risultato);
	}
}
