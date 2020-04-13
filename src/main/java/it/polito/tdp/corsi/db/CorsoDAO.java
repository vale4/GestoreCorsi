package it.polito.tdp.corsi.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.corsi.model.Corso;
import it.polito.tdp.corsi.model.Studente;

public class CorsoDAO {
	
	public boolean esisteCorso(String codins) {
		String sql="SELECT * FROM corso WHERE codins=? ";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st=conn.prepareStatement(sql);
			st.setString(1, codins);
			ResultSet rs=st.executeQuery();
			
			if(rs.next()) {
				conn.close();
				return true;
			}else
				conn.close();
				return false;
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Corso>getCorsiByPeriodo(Integer pd){
		String sql="SELECT * FROM corso WHERE pd= ?";
		List<Corso>result=new ArrayList<Corso>();
		
		try {
			Connection conn=ConnectDB.getConnection();
			PreparedStatement st=conn.prepareStatement(sql);
			st.setInt(1, pd);
			ResultSet rs=st.executeQuery();
			
			while(rs.next()) {
				Corso c = new Corso(rs.getString("codins"), rs.getInt("crediti"), rs.getString("nome"), rs.getInt("pd"));
				result.add(c);
			}
			
			conn.close();
			
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
				
	}
	
	public Map<Corso, Integer>getIscrittiByPeriodo(Integer pd){
		String sql="SELECT c.codins, c.nome, c.crediti, c.pd, COUNT(*) AS tot " + 
				"FROM corso AS c, iscrizione AS i " + 
				"WHERE c.codins=i.codins AND pd=? " + 
				"GROUP BY c.codins, c.nome, c.crediti, c.pd ";
		Map<Corso, Integer>result=new HashMap<Corso, Integer>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st=conn.prepareStatement(sql);
			st.setInt(1, pd);
			ResultSet rs=st.executeQuery();
			
			while(rs.next()) {
				Corso c=new Corso(rs.getString("codins"), rs.getInt("crediti"), rs.getString("nome"), rs.getInt("pd"));
				Integer n=rs.getInt("tot");
				
				result.put(c, n);
			}
			conn.close();
			
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	
	public List<Studente>getStudentiByCorso(Corso corso){
		String sql="SELECT s.matricola, s.nome, s.cognome, s.CDS " + 
				"FROM studente AS s, iscrizione AS i " + 
				"WHERE s.matricola=i.matricola AND i.codins=? ";
		List<Studente>studenti=new LinkedList<Studente>();
		
		try {
			Connection conn=ConnectDB.getConnection();
			PreparedStatement st=conn.prepareStatement(sql);
			st.setString(1, corso.getCodins());
			ResultSet rs=st.executeQuery();
			
			while(rs.next()) {
				Studente s=new Studente(rs.getInt("matricola"), rs.getString("nome"), rs.getString("cognome"), rs.getString("CDS"));
				studenti.add(s);
			}
			conn.close();
			
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
		
		return studenti;
		
	}
	
	public Map<String, Integer>getDivisioneCDS(Corso c){
		String sql="SELECT s.CDS, COUNT(*) AS tot " + 
				"FROM studente AS s, iscrizione AS i " + 
				"WHERE s.matricola=i.matricola AND s.CDS<>\"\" AND i.codins=? " + 
				"GROUP BY s.CDS ";
		Map<String, Integer>statistiche=new HashMap<String, Integer>();
		try {
			Connection conn=ConnectDB.getConnection();
			PreparedStatement st=conn.prepareStatement(sql);
			st.setString(1, c.getCodins());
			ResultSet rs=st.executeQuery();
			
			while(rs.next()) {
				statistiche.put(rs.getString("CDS"), rs.getInt("tot"));
			}
			conn.close();
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
		return statistiche;
	}

}
