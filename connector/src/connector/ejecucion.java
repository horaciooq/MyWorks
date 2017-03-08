package connector;
import java.util.*;
import java.sql.*;

import javax.swing.JOptionPane;

import com.ca.harvest.jhsdk.hutils.JCaHarvestException;

public class ejecucion {
	private Connection miConexion;	
	public int conteocriticas (int version){
		int total=0;
		int base_id=0;
		Statement st;
		PreparedStatement st1,st2,st3;
		ResultSet rs;
		ResultSet rsissues;
		ResultSet rsscan_issue;
		ResultSet rspromote;
		String query, queryscan_issue, querypromote,queryissue;
		miConexion=conexion.GetConnection();
		try {
			query="select top 1 s.id,s.artifact_id, a.uploadDate, s.projectVersion_id from scan as s"+
				  " INNER JOIN artifact as a ON a.id=s.artifact_id where s.projectVersion_id=" + version +
				  " order by a.uploadDate";
			st=miConexion.createStatement();
			rs=st.executeQuery(query);
			while(rs.next()){
				base_id=rs.getInt("id");
			}
			queryissue="select * from issue where projectVersion_id=?" + 
					" AND friority='Critical' AND scanStatus!='REMOVED'";
			queryscan_issue="select * from scan_issue where scan_id= ? AND friority='Critical'";
			querypromote="select * from issueharvestview where projectVersion_id=?"+ 
					" AND friority='Critical' AND lookupIndex=0";
			st=miConexion.createStatement();
			st1=miConexion.prepareStatement(queryissue);
			st2=miConexion.prepareStatement(queryscan_issue);
			st3=miConexion.prepareStatement(querypromote);
			st1.setInt(1, version);
			st2.setInt(1,base_id);
			st3.setInt(1,version);
			rsissues=st1.executeQuery();
			ArrayList <Integer> newissues= new ArrayList <Integer>();
			while(rsissues.next()){
				int issue;
				int issue_id;
				issue=rsissues.getInt("id");
				rsscan_issue=st2.executeQuery();
				if(rsscan_issue!=null){
				while(rsscan_issue.next()){
					issue_id=rsscan_issue.getInt("issue_id");
					if(!(issue==issue_id)){
						if(!newissues.contains(issue)){
							newissues.add(issue);
						}
					}
					else{
						if(newissues.contains(issue)){
							Integer intObj = new Integer(issue);
							newissues.remove(intObj);
							break;
						}
						else{
							break;
						}
					}
				}
			}
				newissues.add(issue);
			}
				int issue_ida;
				rspromote=st3.executeQuery();
		           while(rspromote.next()){
		        	   issue_ida=rspromote.getInt("issue_id");
		        	   if (newissues.contains(issue_ida)){
		        		   Integer intObj = new Integer(issue_ida);
		        		   newissues.remove(intObj);
		        	   }
		           }
		total=newissues.size();		
		
			
		}catch (SQLException e){
			JOptionPane.showMessageDialog(null, "Error de SQL "+ e.getMessage(), 
		  			   "ERROR", JOptionPane.ERROR_MESSAGE);
	}
		return total; 
	}
public int conteoaltas (int version){
	int total=0;
	int base_id=0;
	Statement st;
	PreparedStatement st1,st2,st3;
	ResultSet rs;
	ResultSet rsissues;
	ResultSet rsscan_issue;
	ResultSet rspromote;
	String query, queryscan_issue, querypromote,queryissue;
	miConexion=conexion.GetConnection();
	try {
		query="select top 1 s.id,s.artifact_id, a.uploadDate, s.projectVersion_id from scan as s"+
			  " INNER JOIN artifact as a ON a.id=s.artifact_id where s.projectVersion_id=" + version +
			  " order by a.uploadDate";
		st=miConexion.createStatement();
		rs=st.executeQuery(query);
		while(rs.next()){
			base_id=rs.getInt("id");
		}
		queryissue="select * from issue where projectVersion_id=?"+
				" AND friority='High' AND scanStatus!='REMOVED'";
		queryscan_issue="select * from scan_issue where scan_id=?" +
				" AND friority='High'";
		querypromote="select * from issueharvestview where projectVersion_id=?" + 
				" AND friority='High' AND lookupIndex=0";
		st=miConexion.createStatement();
		st1=miConexion.prepareStatement(queryissue);
		st2=miConexion.prepareStatement(queryscan_issue);
		st3=miConexion.prepareStatement(querypromote);
		st1.setInt(1, version);
		st2.setInt(1, base_id);
		st3.setInt(1, version);
		rsissues=st1.executeQuery();
		ArrayList <Integer> newissues= new ArrayList <Integer>();
		while(rsissues.next()){
			int issue;
			int issue_id;
			issue=rsissues.getInt("id");
			rsscan_issue=st2.executeQuery();
			if(rsscan_issue!=null){
			while(rsscan_issue.next()){
				issue_id=rsscan_issue.getInt("issue_id");
				if(!(issue==issue_id)){
					if(!newissues.contains(issue)){
						newissues.add(issue);
					}
				}
				else{
					if(newissues.contains(issue)){
						Integer intObj = new Integer(issue);
						newissues.remove(intObj);
						break;
					}
					else{
						break;
					}
				}
			}
		}
			else{
				newissues.add(issue);
			}
		}
			int issue_ida;
			rspromote=st3.executeQuery();
	           while(rspromote.next()){
	        	   issue_ida=rspromote.getInt("issue_id");
	        	   if (newissues.contains(issue_ida)){
	        		   Integer intObj = new Integer(issue_ida);
	        		   newissues.remove(intObj);
	        	   }
	           }
	total=newissues.size();		
	
		
	}catch (SQLException e){
		JOptionPane.showMessageDialog(null, "Error de SQL "+ e.getMessage(), 
	  			   "ERROR", JOptionPane.ERROR_MESSAGE);
}
	return total; 
}

public version_project getvpinfo(String namev, String namep){
	miConexion=conexion.GetConnection();
	version_project vs=new version_project();
	PreparedStatement st;
	ResultSet rs;
	String query;
	String version=null;
	vs.SetVersionName(namev);
	try {
		query="select id from project where name=?";
		st=miConexion.prepareStatement(query);
		st.setString(1, namep);
		rs=st.executeQuery();
		int aux=0;
		while(rs.next()){
			aux=rs.getInt("id");
		}
		query="select id from projectversion where name=? AND project_id=?";
		st=miConexion.prepareStatement(query);
		st.setString(1, namev);
		st.setInt(2, aux);
		rs=st.executeQuery();
		while(rs.next()){
			version=rs.getString("id");
			vs.SetProject_ID(aux);
			vs.SetVersion_ID(Integer.parseInt(version));
			}
		PreparedStatement querySQL;
		querySQL = miConexion.prepareStatement("select top 1 * from artifact where projectVersion_id=? AND artifactType='FPR' order by uploadDate desc");
		querySQL.setString(1, version);
		rs=querySQL.executeQuery();
		while(rs.next()){
			if(rs.getString("status").equals("PROCESS_COMPLETE")){
				vs.SetCriticas(conteocriticas(Integer.parseInt(version)));
				vs.SetAltas(conteoaltas(Integer.parseInt(version)));
				System.out.println("Vulnerabilidades nuevas encontradas:\n");
				System.out.println("Críticas: "+ vs.GetCriticas()+ "\n");
				System.out.println("Altas: "+vs.GetAltas()+"\n");
				if(vs.GetAltas()+vs.GetCriticas()!=0){
					vs.SetBandera(true);
				}else{
					vs.SetBandera(false);
				}
			}else{
				JOptionPane.showMessageDialog(null,rs.getString("messages") , 
			  			   "ERROR", JOptionPane.WARNING_MESSAGE);
			}
		}
			
	}catch(SQLException e){
		
		JOptionPane.showMessageDialog(null, "Error de SQL b "+ e.getMessage(), 
  			   "ERROR", JOptionPane.ERROR_MESSAGE);
	}finally
	{
		try {
	          if (null != miConexion)
	             miConexion.close();
	          } catch (SQLException e) {
	          	JOptionPane.showMessageDialog(null, "Error Cerrando La Conexion", 
	         			   "ERROR", JOptionPane.ERROR_MESSAGE);
	          }
	}
	return vs;
}


public void escribirharvest (version_project vp, String str ) {
	try {
		harvestform f = new harvestform();
		if(vp.GetBandera()){
			f.modificaForm(str, "No Aprobado");	
		 }else{
			f.modificaForm(str, "Aprobado");	
		 }
	}catch (JCaHarvestException he){
		System.out.println("Harvest Fallo");
	}catch (NullPointerException e){
		System.out.println("Error Cargando el archivo FPR");
	}
	
	
}
}