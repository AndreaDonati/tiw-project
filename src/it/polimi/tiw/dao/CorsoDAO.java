package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Corso;
import it.polimi.tiw.beans.User;

public class CorsoDAO {
	private Connection con;
	
	public CorsoDAO(Connection connection) {
		this.con = connection;
	}
	
	/**
	 * Ritorna una lista di corsi corrispondenti ai corsi degli esami a cui lo studente con matricola specificata
	 * è iscritto.
	 * @param matricola
	 * @return
	 */
	public List<Corso> getCorsiFromMatricolaStudente(int matricola) throws SQLException{
		List<Corso> corsi = new ArrayList<Corso>();
		
		String query = "SELECT  idCorso, nomeCorso, annoCorso "
				+ "		FROM corso, frequentazione "
				+ "		WHERE corso.id = frequentazione.idCorso "
				+ "     AND matricolaStudente = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, matricola);
			try (ResultSet result = pstatement.executeQuery();) {
				while (!result.isLast()) {
					result.next();
					Corso corso = new Corso();
					corso.setId(result.getInt("idCorso"));
					corso.setNome(result.getString("nomeCorso"));
					corso.setAnno(result.getInt("annoCorso"));
					corsi.add(corso);
				}
			}
		}
		
		return corsi;
	}
	
	public List<Corso> getCorsiFromMatricolaProfessore(int matricola) throws SQLException {
		List<Corso> corsi = new ArrayList<Corso>();
		
		String query = "SELECT  id, nomeCorso, annoCorso "
				+ "		FROM corso "
				+ "		WHERE matricolaProfessore = ? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1,matricola);
			try (ResultSet result = pstatement.executeQuery();) {
				while (!result.isLast()) {
					result.next();
					Corso corso = new Corso();
					corso.setId(result.getInt("id"));
					corso.setNome(result.getString("nomeCorso"));
					corso.setAnno(result.getInt("annoCorso"));
					corsi.add(corso);
				}
			}
		}
		
		return corsi;
	}
}
