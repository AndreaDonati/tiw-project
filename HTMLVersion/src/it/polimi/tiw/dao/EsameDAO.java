package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.Esame;
import it.polimi.tiw.beans.Esaminazione;
import it.polimi.tiw.beans.User;

public class EsameDAO {
	
	private Connection con;
	
	public EsameDAO(Connection connection) {
		this.con = connection;
	}
	
//	-----------------------------------------------------------------------------------
//	NOTA: CAMBIARE I TIPI DI RITORNO DELLE FUNZIONI CON I TIPI CORRISPONDENTI NECESSARI
//	-----------------------------------------------------------------------------------
	
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
		for (Esaminazione esaminazione : risultati) {
			System.out.println(esaminazione.getStudente().getNome()+" "+ esaminazione.getStudente().getCognome()+" - "
					+ esaminazione.getVoto() + " - " + esaminazione.getStato());
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
				+ "		WHERE esame.id = ? " // cos� o con questa dopo le altre condizioni
				+ "		AND esaminazione.idEsame = esame.id AND esaminazione.idStudente = utente.matricola "
				+ "		AND esame.idCorso = corso.id"
				+ "		ORDER BY dataAppello DESC";
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
		for (Esaminazione esaminazione : risultati) {
			System.out.println(esaminazione.getStudente().getNome()+" "+ esaminazione.getStudente().getCognome()+" - "
					+ esaminazione.getVoto() + " - " + esaminazione.getStato());
		}
		return risultati;
	}
	
}
