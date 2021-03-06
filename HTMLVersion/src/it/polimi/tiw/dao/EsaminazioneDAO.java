package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EsaminazioneDAO {
	private Connection con;

	public EsaminazioneDAO(Connection connection) {
		this.con = connection;
	}

	/**
	 * Inserisce un voto per l'utente ed esame specificato
	 * @param matricolaStudente
	 * @param idEsame
	 * @param voto
	 * @param matricolaProfessore
	 * @return
	 * @throws SQLException
	 */
	public void insertGrade(int matricolaStudente, int idEsame, String voto, int matricolaProfessore) throws SQLException {
		String query = "UPDATE esaminazione"
				+ "		JOIN esame ON esaminazione.idEsame = esame.id JOIN corso ON esame.idCorso = corso.id"
				+"		SET voto = ?, stato = 'inserito'"
				+"		WHERE idStudente = ?"
				+"		AND idEsame = ?"
				+"		AND (stato = 'non inserito' OR stato = 'inserito') "
				+ "		AND corso.matricolaProfessore = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, voto);
			pstatement.setInt(2, matricolaStudente);
			pstatement.setInt(3, idEsame);
			pstatement.setInt(4, matricolaProfessore);
			pstatement.executeUpdate();
		}
	}
	
	/**
	 * Pubblca i voti precedentemente inseriti relativi all'esame specificato
	 * @param idEsame
	 * @return
	 * @throws SQLException
	 */
	public void publishGrades(int idEsame) throws SQLException {
		String query = "UPDATE esaminazione"
				+"		SET stato = 'pubblicato'"
				+"		WHERE idEsame = ?"
				+"		AND stato = 'inserito'";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idEsame);
			pstatement.executeUpdate();
		}
	}
	
	/**
	 * Crea un nuovo verbale e verbalizza i voti precedentemente pubblicati relativi all'esame specificato
	 * @param idEsame
	 * @return
	 * @throws SQLException
	 */
	public int recordGrades(int idEsame) throws SQLException {
		// prendo l'id massimo dei verbali
		String query = "SELECT  MAX(id), dataVerbale FROM verbale";
		int idVerbale = 0;
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				idVerbale = result.getInt("MAX(id)") + 1;
			}
		}
		
		con.setAutoCommit(false); // No commit per statement
		
		// creo un nuovo verbale
		query = "INSERT INTO verbale (id, dataVerbale)"
				+"		VALUES ( ? , ? )";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = dateFormat.format(today);

			pstatement.setInt(1, idVerbale);
			pstatement.setString(2, date);
			pstatement.execute();
			
			// cambio i voti rifiutati in "rimandato"
			String query2 = "UPDATE esaminazione"
					+"		SET voto = 'rimandato'"
					+"		WHERE idEsame = ?"
					+"		AND stato = 'rifiutato'";
			
			try (PreparedStatement pstatement2 = con.prepareStatement(query2);) {
				pstatement2.setInt(1, idEsame);
				pstatement2.executeUpdate();
				
				// cambio stato di tutti i voti 'inseriti' in 'verbalizzati' e aggiungo la dipendenza al nuovo verbale
				String query3 = "UPDATE esaminazione"
						+"		SET stato = 'verbalizzato', idVerbale = ?"
						+"		WHERE idEsame = ?"
						+"		AND stato = 'pubblicato' OR stato = 'rifiutato'";
				
				try (PreparedStatement pstatement3 = con.prepareStatement(query3);) {
					pstatement3.setInt(1, idVerbale);
					pstatement3.setInt(2, idEsame);
					pstatement3.executeUpdate();
					
					con.commit();
					con.setAutoCommit(true);
				}
			}
		} catch(SQLException e) {
			con.rollback();
			con.setAutoCommit(true);
			throw new SQLException(e);
		}
		
		return idVerbale;
	}
	
	/**
	 * Rifiuta il voto pubblicato relativo all'esame e all'utente specificati
	 * @param idEsame
	 * @param matricola
	 * @return
	 * @throws SQLException
	 */
	public void rejectGrade(int idEsame, int matricola) throws SQLException {
		String query = "UPDATE esaminazione"
				+"		SET stato = 'rifiutato'"
				+"		WHERE idEsame = ? "
				+"		AND idStudente = ? "
				+"		AND stato = 'pubblicato' ";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idEsame);
			pstatement.setInt(2, matricola);
			pstatement.executeUpdate();
		}
	}
}
