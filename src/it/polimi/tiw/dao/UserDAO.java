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
		String query = "SELECT  matricola, username, name, surname, ruolo FROM user  WHERE username = ? AND password =?";
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
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					user.setRuolo(result.getString("ruolo"));
					return user;
				}
			}
		}
	}
	
	/**
	 * Ritorna tutti gli utenti nel db (possibilmente inutile)
	 * @return
	 * @throws SQLException
	 */
	public List<User> getAllUsers() throws SQLException {
		List<User> users = new ArrayList<User>();
		
		String query = "SELECT name, surname, username FROM user";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				while(!result.isLast()) {
					result.next();
					User user = new User();
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					users.add(user);
				}
			}
		}
		return users;
	}
	
    /**
     * Potrebbe anche essere inutile
     * @param matricola
     * @return
     * @throws SQLException
     */
	public User getUserFromMatricola(String matricola) throws SQLException {
		String query = "SELECT  username, name, surname FROM user  WHERE matricola = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, Integer.parseInt(matricola));
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}
	
	// POSSIBILMENTE INUTILE	
	/**
	 * ritorna tutti gli studenti iscritti all'esame con id idEsame
	 * e anche tutte le informazioni sui voti degli studenti iscritti all'esame
	 * @param idEsame
	 * @return
	 */
	public List<User> getStudentiVotiFromIdEsame(String idEsame) throws SQLException{
		List<User> users = new ArrayList<User>();
		// FIXARE QUESTA QUERY SECONDO I NOMI DEGLI ATTRIBUTI SCELTI DA BAGA NEL DB
		// E ANCHE SECONDO A QUALI ATTRIBUTI BISOGNA RITORNARE
		String query = "SELECT  matricola, name, surname, voto, lode, stato "
				+ "		FROM user JOIN esaminazione ON user.matricola = esaminazione.matricolaStudente  "
				+ "		WHERE esaminazione.idEsame = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, Integer.parseInt(idEsame));
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setMatricola(result.getInt("matricola"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					users.add(user);
				}
			}
		}
		return users;
	}
}
