package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.Esame;
import it.polimi.tiw.beans.Esaminazione;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Verbale;

public class VerbaleDAO {
	private Connection con;

	public VerbaleDAO(Connection connection) {
		this.con = connection;
	}

	/**
	 * Ritorna tutti i dati relativi ad un verbale
	 * @param idVerbale
	 * @return Verbale
	 */
	public Verbale getVerbale(int idVerbale) throws SQLException {
		Verbale verbale = new Verbale();
		verbale.setId(idVerbale);
		
		String query = "SELECT  verbale.dataVerbale, esaminazione.id, esaminazione.idEsame, esaminazione.idStudente, esaminazione.voto, "
				+ "		utente.matricola, utente.nome, utente.cognome, utente.email, utente.cdl, utente.image"
				+ "		FROM verbale, esaminazione, esame, utente "
				+ "		WHERE verbale.id = ? "
				+ "		AND esaminazione.idVerbale = verbale.id AND esame.id = esaminazione.idEsame"
				+ "		AND utente.matricola = idStudente"
				+ "		ORDER BY esaminazione.voto DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idVerbale);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (!result.isLast()) {
					result.next();
					// data del verbale
					verbale.setDataVerbale(result.getString("verbale.dataVerbale"));
					
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
					risultato.setStato("verbalizzato");
					// idVerbale
					risultato.setIdVerbale(idVerbale);
					
					verbale.addRisultato(risultato);					
				}
			}
		}

		return verbale;
	}
}
