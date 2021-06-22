package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
					user.setImage(result.getString("image"));
					return user;
				}
			}
		}
	}
}
