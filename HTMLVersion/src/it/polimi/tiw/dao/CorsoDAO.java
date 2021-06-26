package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Corso;

public class CorsoDAO {
	private Connection con;
	
	public CorsoDAO(Connection connection) {
		this.con = connection;
	}
	
	/**
	 * Ritorna una lista di corsi a cui lo studente è iscritto
	 * @param matricola
	 * @return
	 * @throws SQLException
	 */
	public List<Corso> getCorsiFromMatricolaStudente(int matricola) throws SQLException{
		List<Corso> corsi = new ArrayList<Corso>();
		
		String query = "SELECT DISTINCT nomeCorso"
				+ "		FROM corso, frequentazione "
				+ "		WHERE corso.id = frequentazione.idCorso "
				+ "     AND matricolaStudente = ? "
				+ "		ORDER BY nomeCorso DESC, annoCorso DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, matricola);
			try (ResultSet result = pstatement.executeQuery();) {
				while (!result.isLast()) {
					result.next();
					Corso corso = new Corso();
					corso.setNome(result.getString("nomeCorso"));
					corsi.add(corso);
				}
			}
		}
		
		return corsi;
	}
	
	/**
	 * Ritorna una lista di corsi differenziati per anno relativi al corso specificato a cui lo studente è iscritto
	 * @param matricola
	 * @param nomeCorso
	 * @return
	 * @throws SQLException
	 */
	public List<Corso> getCorsiFromMatricolaStudente(int matricola, String nomeCorso) throws SQLException{
		List<Corso> corsi = new ArrayList<Corso>();
		
		String query = "SELECT  idCorso, nomeCorso, annoCorso "
				+ "		FROM corso, frequentazione "
				+ "		WHERE corso.id = frequentazione.idCorso "
				+ "     AND matricolaStudente = ? "
				+ "		AND nomeCorso = ?"
				+ "		ORDER BY nomeCorso DESC, annoCorso DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, matricola);
			pstatement.setString(2,nomeCorso);
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
	
	/**
	 * Ritorna una lista di corsi di cui il professore è docente
	 * @param matricola
	 * @return
	 * @throws SQLException
	 */
	public List<Corso> getCorsiFromMatricolaProfessore(int matricola) throws SQLException {
		List<Corso> corsi = new ArrayList<Corso>();
		
		String query = "SELECT DISTINCT nomeCorso "
				+ "		FROM corso "
				+ "		WHERE matricolaProfessore = ? "
				+ "		ORDER BY nomeCorso DESC, annoCorso DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1,matricola);
			try (ResultSet result = pstatement.executeQuery();) {
				while (!result.isLast()) {
					result.next();
					Corso corso = new Corso();
					corso.setNome(result.getString("nomeCorso"));
					corsi.add(corso);
				}
			}
		}
		
		return corsi;
	}
	
	/**
	 * Ritorna una lista di corsi differenziati per anno relativi al corso specificato di cui il professore è docente
	 * @param matricola
	 * @param nomeCorso
	 * @return
	 * @throws SQLException
	 */
	public List<Corso> getCorsiFromMatricolaProfessore(int matricola, String nomeCorso) throws SQLException {
		List<Corso> corsi = new ArrayList<Corso>();
		String query = "SELECT  id, nomeCorso, annoCorso "
				+ "		FROM corso "
				+ "		WHERE matricolaProfessore = ? "
				+ "		AND nomeCorso = ?"
				+ "		ORDER BY annoCorso DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1,matricola);
			pstatement.setString(2,nomeCorso);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
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
