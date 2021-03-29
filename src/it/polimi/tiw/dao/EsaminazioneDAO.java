package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
	 * @return
	 * @throws SQLException
	 */
	public void insertGrade(int matricolaStudente, int idEsame, String voto) throws SQLException {
		String query = "UPDATE esaminazione"
				+"		SET voto = ?, stato = 'inserito'"
				+"		WHERE idStudente = ?"
				+"		AND idEsame = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, voto);
			pstatement.setInt(2, matricolaStudente);
			pstatement.setInt(3, idEsame);
			pstatement.executeUpdate();
		}
	}
}
