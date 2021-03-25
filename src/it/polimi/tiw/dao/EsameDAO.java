package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EsameDAO {
	
	private Connection connection;
	
	public EsameDAO(Connection connection) {
		this.connection = connection;
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
	public List<String> getEsamiFromCorso(int idCorso){
		//TODO: implement
		return null;
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
	public String getRisultatiEsameStudente(int matricola, int idEsame){
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
	public List<String> getRisultatiEsameProfessore(int matricola, int idEsame){
		//TODO: implement
		return null;
	}
	
}
