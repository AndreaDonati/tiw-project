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
	public List<String> getEsamiFromStudenteCorso(int matricola, int idCorso){
		//TODO: implement
		return null;
	}

	/**
	 * Ritorna i risultati di uno specifico esame (idEsame) di uno specifico studente (matricola)
	 * @param matricola: matricola dello STUDENTE
	 * @param idEsame
	 * @return
	 */
	public List<Esaminazione> getRisultatiEsameStudente(int matricola, int idEsame){
		//TODO: implement
		return null;
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
		
		String query = "SELECT  esaminazione.id, utente.matricola, utente.nome, utente.cognome, utente.email, utente.cdl, "
				+ "		esaminazione.idEsame, esame.dataAppello, esaminazione.idVerbale, esaminazione.voto, esaminazione.stato"
				+ "		FROM esaminazione, utente, esame "
				+ "		WHERE esame.id = ? " // così o con questa dopo le altre condizioni
				+ "		AND esaminazione.idEsame = esame.id AND esaminazione.idStudente = utente.matricola "
				+ "		ORDER BY dataAppello DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idEsame);
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
					risultato.setStudente(studente);
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
	
}
