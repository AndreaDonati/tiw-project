package it.polimi.tiw.beans;

public class User {

	private int matricola;
	private String email;
	private String nome;
	private String cognome;
	private String ruolo;
	private String cdl;
	private String image;

	public int getMatricola() {
		return matricola;
	}

	public void setMatricola(int matricola) {
		this.matricola = matricola;
	}

	public String getMail() {
		return email;
	}

	public void setMail(String username) {
		this.email = username;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	
	public void setRuolo(String ruolo) {
		this.ruolo = ruolo;
	}
	
	public String getRuolo() {
		return ruolo;
	}
	
	public void setCdl(String cdl) {
		this.cdl = cdl;
	}
	
	public String getCdl() {
		return cdl;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = "data:image/jpeg;base64,"+image;
	}

}
