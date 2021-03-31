package it.polimi.tiw.beans;

import java.util.List;

public class Corso {
	private int id;
	private String nome;
	private User professore;
	private int anno;
	
	// test
	private List<Esame> esami;
	
	public Corso() {
		// Default constructor
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public User getProfessore() {
		return professore;
	}
	
	public void setProfessore(User professore) {
		this.professore = professore;
	}
	
	public int getAnno() {
		return anno;
	}
	
	public void setAnno(int anno) {
		this.anno = anno;
	}
	
	public List<Esame> getEsami() {
		return esami;
	}
	
	public void setEsami(List<Esame> esami) {
		this.esami = esami;
	}
}
