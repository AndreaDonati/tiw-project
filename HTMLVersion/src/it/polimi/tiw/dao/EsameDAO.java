package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Esame;
import it.polimi.tiw.beans.Esaminazione;
import it.polimi.tiw.beans.User;

public class EsameDAO {
	
	private Connection con;
	private Map<String,String> escaping;
	
	public EsameDAO(Connection connection) {
		this.con = connection;
		this.escaping = new HashMap<String, String>();
		this.escaping.put("matricola", "utente.matricola");
		this.escaping.put("cognome", "utente.cognome");
		this.escaping.put("nome", "utente.nome");
		this.escaping.put("email", "utente.email");
		this.escaping.put("cdl", "utente.cdl");
		this.escaping.put("voto", "esaminazione.voto");
		this.escaping.put("stato", "esaminazione.stato");
	}
	
	/**
	 * Ritorna una lista di esami (compreso idEsame - necessario per le seguenti interazioni)
	 * corrispondenti ad un corso.
	 * @param idCorso
	 * @return
	 */
	public List<Esame> getEsamiFromCorso(int idCorso) throws SQLException {
		List<Esame> esami = new ArrayList<Esame>();
		
		String query = "SELECT  id, dataAppello  "
				+ "		FROM esame "
				+ "		WHERE idCorso = ? "
				+ "		ORDER BY dataAppello DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idCorso);
			try (ResultSet result = pstatement.executeQuery();) {
				while (!result.isLast()) {
					result.next();
					Esame esame = new Esame();
					esame.setId(result.getInt("id"));
					esame.setDataAppello(result.getString("dataAppello"));
					esami.add(esame);
				}
			}
		}

		return esami;
	}
	
	/**
	 * Ritorna una lista di esami (compreso idEsame - necessario per le seguenti interazioni)
	 * di un corso, fatti da uno studente (matricola)
	 * @param matricola: matricola dello STUDENTE
	 * @param idCorso
	 * @return
	 */
	public List<Esame> getEsamiFromStudenteCorso(int matricola, int idCorso) throws SQLException {
		List<Esame> esami = new ArrayList<Esame>();
		
		String query = "SELECT  esame.id, esame.dataAppello  "
				+ "		FROM esaminazione, esame "
				+ "		WHERE esaminazione.idEsame = esame.id"
				+ "		AND esame.idCorso = ? "
				+ "		AND esaminazione.idStudente = ?"
				+ "		ORDER BY dataAppello DESC";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idCorso);
			pstatement.setInt(2, matricola);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					// controllo su empty result set 
					Esame esame = new Esame();
					esame.setId(result.getInt("esame.id"));
					esame.setDataAppello(result.getString("esame.dataAppello"));
					esami.add(esame);
				}
			}
		}

		return esami;
	}

	/**
	 * Ritorna i risultati di uno specifico esame (idEsame) di uno specifico studente (matricola)
	 * @param matricola: matricola dello STUDENTE
	 * @param idEsame
	 * @return
	 */
	public List<Esaminazione> getRisultatiEsameStudente(int matricola, int idEsame) throws SQLException {
		List<Esaminazione> risultati = new ArrayList<Esaminazione>();
		
		String query = "SELECT  esaminazione.id, utente.matricola, utente.nome, utente.cognome, utente.email, utente.cdl, utente.image, "
				+ "		esaminazione.idEsame, esame.dataAppello, esaminazione.idVerbale, esaminazione.voto, esaminazione.stato, corso.nomeCorso,"
				+ "     corso.annoCorso, corso.id, prof.nome, prof.cognome, prof.email, prof.matricola, prof.image"
				+ "		FROM esaminazione JOIN utente JOIN esame JOIN corso JOIN utente AS prof "
				+ "		WHERE esame.id = ? " 
				+ "		AND esaminazione.idStudente = ? "
				+ "		AND esaminazione.idEsame = esame.id AND esaminazione.idStudente = utente.matricola AND corso.id = esame.idCorso "
				+ "		AND corso.matricolaProfessore = prof.matricola"
				+ "		ORDER BY dataAppello DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);)  {
			pstatement.setInt(1, idEsame);
			pstatement.setInt(2, matricola);
			try (ResultSet result = pstatement.executeQuery();) {
				while (!result.isLast()) {
					result.next();
					Esaminazione risultato = new Esaminazione();
					// id
					risultato.setId(result.getInt("esaminazione.id"));
					// esame
					Esame esame = new Esame();
					esame.setId(result.getInt("esaminazione.idEsame"));
					esame.setDataAppello(result.getString("esame.dataAppello"));
					risultato.setEsame(esame);
					// studente
					User studente = new User();
					studente.setMatricola(result.getInt("utente.matricola"));
					studente.setNome(result.getString("utente.nome"));
					studente.setCognome(result.getString("utente.cognome"));
					studente.setMail(result.getString("utente.email"));
					studente.setRuolo("student");
					studente.setCdl(result.getString("utente.cdl"));
					studente.setImage(result.getString("utente.image"));
					risultato.setStudente(studente);
					// docente
					User docente = new User();
					docente.setMatricola(result.getInt("prof.matricola"));
					docente.setNome(result.getString("prof.nome"));
					docente.setCognome(result.getString("prof.cognome"));
					docente.setMail(result.getString("prof.email"));
					docente.setRuolo("teacher");
					docente.setImage(result.getString("prof.image"));
					docente.setCdl("");
					//corso
					Corso corso = new Corso();
					corso.setId(result.getInt("corso.id"));
					corso.setNome(result.getString("corso.nomeCorso"));
					corso.setAnno(result.getInt("corso.annoCorso"));
					corso.setProfessore(docente);
					risultato.setCorso(corso);
					// voto
					risultato.setVoto(result.getString("esaminazione.voto"));
					// stato
					risultato.setStato(result.getString("esaminazione.stato"));
					// idVerbale
					risultato.setIdVerbale(result.getInt("esaminazione.idverbale"));
					
					risultati.add(risultato);					
				}
			}
		}
		return risultati;
	}
	
	/**
	 * Ritorna tutti i risultati di uno specifico esame (idEsame) di un corso insegnato da uno
	 * specifico professore (matricola)
	 * @param matricola: matricola del PROFESSORE
	 * @param idEsame
	 * @return
	 */
	public List<Esaminazione> getRisultatiEsameProfessore(int idEsame, String ordine, String campo) throws SQLException {
		List<Esaminazione> risultati = new ArrayList<Esaminazione>();
		
		campo = escaping.get(campo);
		if(campo == null) {
			campo = "utente.matricola";
			ordine = "ASC";
		}
		
		if(!(ordine.equals("ASC") || ordine.equals("DESC")))
			ordine = "ASC";
		
		String query = "SELECT  esaminazione.id, utente.matricola, utente.nome, utente.cognome, utente.email, utente.cdl, utente.image, "
				+ "		esaminazione.idEsame, esame.dataAppello, esaminazione.idVerbale, esaminazione.voto, esaminazione.stato, "
				+ "		corso.nomeCorso, corso.annoCorso, corso.id"
				+ "		FROM esaminazione, utente, esame, corso "
				+ "		WHERE esame.id = ? "
				+ "		AND esaminazione.idEsame = esame.id AND esaminazione.idStudente = utente.matricola "
				+ "		AND esame.idCorso = corso.id"
				+ "		ORDER BY "+campo+" "+ordine;
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idEsame);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Esaminazione risultato = new Esaminazione();
					// id
					risultato.setId(result.getInt("esaminazione.id"));
					// esame
					Esame esame = new Esame();
					esame.setId(result.getInt("esaminazione.idEsame"));
					esame.setDataAppello(result.getString("esame.dataAppello"));
					risultato.setEsame(esame);
					// studente
					User studente = new User();
					studente.setMatricola(result.getInt("utente.matricola"));
					studente.setNome(result.getString("utente.nome"));
					studente.setCognome(result.getString("utente.cognome"));
					studente.setMail(result.getString("utente.email"));
					studente.setRuolo("student");
					studente.setCdl(result.getString("utente.cdl"));
					studente.setImage(result.getString("utente.image"));
					risultato.setStudente(studente);
					// voto
					risultato.setVoto(result.getString("esaminazione.voto"));
					// stato
					risultato.setStato(result.getString("esaminazione.stato"));
					// idVerbale
					risultato.setIdVerbale(result.getInt("esaminazione.idverbale"));
					// corso
					Corso corso = new Corso();
					corso.setId(result.getInt("corso.id"));
					corso.setNome(result.getString("corso.nomeCorso"));
					corso.setAnno(result.getInt("corso.annoCorso"));
					risultato.setCorso(corso);
					
					risultati.add(risultato);					
				}
			}
		}
		// se il campo su cui ordinare � "voto", allora devo usare un ordinamento personalizzato
		// ORDINE ASC : <vuoto>, assente, rimandato, riprovato, 18, 19, ... 30 e Lode
		// ORDINE DESC: 30 e Lode, ..., 19, 18, riprovato, rimandato, assente, <vuoto>
		// ORDINE ATTUALE ASC: <vuoto>, 18, 19, ... , assente, rimandato, riprovato 
		// ORDINE ATTUALE DESC: riprovato, rimandato, assente, ..., 19, 18, <vuoto>
		if(campo.equals("esaminazione.voto")) {
			// ASC -> scorro la lista dall'inizio e metto in coda i numeri
			if(ordine.equals("ASC")) {
				List<Esaminazione> risultatiOrdinati = new ArrayList<Esaminazione>();
				for (Esaminazione esaminazione : risultati) {
					if(esaminazione.getVoto() != null && esaminazione.getVoto().matches(".*\\d.*")) {
						risultatiOrdinati.add(esaminazione);
					}
				}
				risultati.removeAll(risultatiOrdinati);
				risultati.addAll(risultatiOrdinati);	
			} else {
				// DESC -> scorro la lista dalla fine e metto in testa i numeri
				// prendo l'iteratore per scorrerla al contrario
				
				ListIterator li = risultati.listIterator(risultati.size());
				List<Esaminazione> risultatiOrdinati = new ArrayList<Esaminazione>();
				// Scorro la lista al contrario
				while(li.hasPrevious()) {
					Esaminazione esaminazione = (Esaminazione) li.previous();
					if(esaminazione.getVoto() != null && esaminazione.getVoto().matches(".*\\d.*")) {
						risultatiOrdinati.add(esaminazione);
					}
				}
				risultati.removeAll(risultatiOrdinati);
				Collections.reverse(risultatiOrdinati);
				risultatiOrdinati.addAll(risultati);
				risultati = risultatiOrdinati;	
			}
		}
		
		// se non esistono risultati per l'esame ritorno un'esaminazione fittizia con i dati dell'esame
		if(risultati.isEmpty()) {
			query = "SELECT nomeCorso, annoCorso"
					+ "			 FROM esame, corso"
					+ "			 WHERE esame.idCorso = corso.id"
					+ "			 AND esame.id = ?";

			try (PreparedStatement pstatement = con.prepareStatement(query);) {
				pstatement.setInt(1, idEsame);
				try (ResultSet result = pstatement.executeQuery();) {
					result.next();
					Esaminazione risultato = new Esaminazione();
					risultato.setId(-1);
					Corso corso = new Corso();
					corso.setNome(result.getString("corso.nomeCorso"));
					corso.setAnno(result.getInt("corso.annoCorso"));
					risultato.setCorso(corso);
					risultati.add(risultato);
				}
			}
		}
		return risultati;
	}
	
	/**
	 * Ritorna tutti i risultati di uno specifico esame (idEsame) di un corso insegnato da uno
	 * specifico professore (matricola)
	 * @param matricola: matricola del PROFESSORE
	 * @param idEsame
	 * @return
	 */
	public List<Esaminazione> getRisultatiEsameProfessore(int idEsame) throws SQLException {
		List<Esaminazione> risultati = new ArrayList<Esaminazione>();
		
		String query = "SELECT  esaminazione.id, utente.matricola, utente.nome, utente.cognome, utente.email, utente.cdl, utente.image, "
				+ "		esaminazione.idEsame, esame.dataAppello, esaminazione.idVerbale, esaminazione.voto, esaminazione.stato, "
				+ "		corso.nomeCorso, corso.annoCorso, corso.id"
				+ "		FROM esaminazione, utente, esame, corso "
				+ "		WHERE esame.id = ? "
				+ "		AND esaminazione.idEsame = esame.id AND esaminazione.idStudente = utente.matricola "
				+ "		AND esame.idCorso = corso.id";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idEsame);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Esaminazione risultato = new Esaminazione();
					// id
					risultato.setId(result.getInt("esaminazione.id"));
					// esame
					Esame esame = new Esame();
					esame.setId(result.getInt("esaminazione.idEsame"));
					esame.setDataAppello(result.getString("esame.dataAppello"));
					risultato.setEsame(esame);
					// studente
					User studente = new User();
					studente.setMatricola(result.getInt("utente.matricola"));
					studente.setNome(result.getString("utente.nome"));
					studente.setCognome(result.getString("utente.cognome"));
					studente.setMail(result.getString("utente.email"));
					studente.setRuolo("student");
					studente.setCdl(result.getString("utente.cdl"));
					studente.setImage(result.getString("utente.image"));
					risultato.setStudente(studente);
					// voto
					risultato.setVoto(result.getString("esaminazione.voto"));
					// stato
					risultato.setStato(result.getString("esaminazione.stato"));
					// idVerbale
					risultato.setIdVerbale(result.getInt("esaminazione.idverbale"));
					// corso
					Corso corso = new Corso();
					corso.setId(result.getInt("corso.id"));
					corso.setNome(result.getString("corso.nomeCorso"));
					corso.setAnno(result.getInt("corso.annoCorso"));
					risultato.setCorso(corso);
					
					risultati.add(risultato);					
				}
			}
		}
		return risultati;
	}
	
}
