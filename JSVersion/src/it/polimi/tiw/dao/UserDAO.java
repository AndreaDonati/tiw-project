package it.polimi.tiw.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import it.polimi.tiw.beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	/**
	 * Controlla le credenziali inserite e ritorna l'utente con quelle credenziali
	 * se l'utente esiste, lancia una SQLException altrimenti.
	 * @param usrn
	 * @param pwd
	 * @return
	 * @throws SQLException
	 */
	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  matricola, email, nome, cognome, role, image FROM utente  WHERE matricola = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setMatricola(result.getInt("matricola"));
					user.setMail(result.getString("email"));
					user.setNome(result.getString("nome"));
					user.setCognome(result.getString("cognome"));
					user.setRuolo(result.getString("role"));
					// prendo l'immagine dal db come blob, prendo il suo encoding in base64 e lo
					// setto come immagine dell'utente
					Blob imageBlob = result.getBlob("image");
					byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
					String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
					user.setImage(encodedImage);
					return user;
				}
			}
		}
	}
	
	/**
	 * Controlla se l'utente specificato è il docente dell'esame relativo all'idEsame
	 * @param idEsame
	 * @param matricola
	 * @return
	 * @throws SQLException
	 */
	public boolean controllaDocente(int idEsame, int matricola) throws SQLException {
		String query = "SELECT  matricola"
				+ "		FROM utente JOIN corso ON utente.matricola = corso.matricolaProfessore JOIN esame ON corso.id = esame.idCorso "
				+ "		WHERE matricola = ? "
				+ "		AND esame.id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, matricola);
			pstatement.setInt(2, idEsame);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return false;
				else {
					return true;
				}
			}
		}
	}
	
	/**
	 * Controlla se lo studente specificato è iscritto all'esame relativo ad idEsame
	 * @param idEsame
	 * @param matricola
	 * @return
	 * @throws SQLException
	 */
	public boolean controllaStudente(int idEsame, int matricola) throws SQLException {
		String query = "SELECT idStudente"
				+ "		FROM esaminazione "
				+ "		WHERE idStudente = ? "
				+ "		AND idEsame = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, matricola);
			pstatement.setInt(2, idEsame);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return false;
				else {
					return true;
				}
			}
		}
	}
}
