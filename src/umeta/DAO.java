package umeta;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import umeta.Person.PersonName;
import umeta.Publication.publicationTypes;

public class DAO {

	
	private static final Map<Publication.publicationTypes, String> pubTypesMnems = new HashMap<Publication.publicationTypes, String>();
	static {
		pubTypesMnems.put(publicationTypes.issueOfaMagazine, "iss");		
		pubTypesMnems.put(publicationTypes.conferenceDigest, "prc");		
		pubTypesMnems.put(publicationTypes.paperAtConference, "apr");		
		pubTypesMnems.put(publicationTypes.fiction, "fict");
		pubTypesMnems.put(publicationTypes.document, "doc");
		pubTypesMnems.put(publicationTypes.paperinMagazine, "amg");		
		pubTypesMnems.put(publicationTypes.magazine, "mag");		
		pubTypesMnems.put(publicationTypes.monograph, "book");
		pubTypesMnems.put(publicationTypes.unknown, "unk");
		pubTypesMnems.put(publicationTypes.boundVolumesOftheMagazines, "vol");
    }
	
	static String getPersonUriFromDataBase(PersonName name)
	{
		
		 Connection con = null;
	     Statement stmt = null;

	     
	     String lastName = name.getLastName().replace("'", "''");
	     try {
	     		con = DBConnectionManager.getConnection();
				
	            stmt = con.createStatement();  
	            
				
	            ResultSet rs = stmt.executeQuery("select person_names.last_name, person_names.middle_name, person_names.first_name, uris.uri" 
                                                + " from person_names, uris "
                                                + " where uris.id = person_names.per_id " +
                                                " and lower(person_names.last_name) = '" + lastName.toLowerCase() +"'" );

	            while (rs.next()){
	            	String last = rs.getString("last_name");
	            	if (last != null) last = last.trim();
	            	String first = rs.getString("first_name");
	            	if (first != null) first = first.trim();
	            	
	            	String middle = rs.getString("middle_name");
	            	if (middle != null) middle = middle.trim();
	            	
	            	PersonName testName = new PersonName(last, first, middle);
	            	
	            	if (testName.equals(name)) return rs.getString("uri");
	            	
	            }           
	           
	            
        } catch (ClassNotFoundException e)
        {

			// TODO Auto-generated catch block
			e.printStackTrace();
        }catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		//	throw new RuntimeException(e);
		}
	        
		return null;
	}
	
	
	static String getPublicationUriFromDataBase(String title, Publication.publicationTypes type)
	{
		 Connection con = null;
	     Statement stmt = null;

	     title = title.replaceAll("'", "''");
	  
	     
	     try {
	     		con = DBConnectionManager.getConnection();
				
	            stmt = con.createStatement();

	            ResultSet rs = stmt.executeQuery(" select uris.uri from " +
	            		" pub_types, publications, pub_titles, uris " +
	            		" where publications.type = pub_types.id " +
	            		" and pub_types.mnem = '" + pubTypesMnems.get(type) + "' " +
	            		" and pub_titles.pub_id = publications.id " + 
                        " and lower(pub_titles.title) = '" + title.toLowerCase() + "' " +
	            		" and uris.id = publications.id ");

	            if (rs.next())
	            	return rs.getString("uri");
	    } catch (ClassNotFoundException e)
	    {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//throw new RuntimeException(e);
		}
	    
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//throw new RuntimeException(e);
		}
	        
		return null;
	}
	
	
	static String getPublicationUriFromDataBaseByParentUri(String title, Publication.publicationTypes type, String parentUri)
	{
		 Connection con = null;
	     Statement stmt = null;

	     try {
	     		con = DBConnectionManager.getConnection();
				
	            stmt = con.createStatement();

	            ResultSet rs = stmt.executeQuery(" select pub_uris.uri from " +
	            		" pub_types, publications, pub_titles, pub_relations, uris pub_uris, uris parent_uris " +
	            		" where publications.type = pub_types.id " +
	            		" and pub_types.mnem = '" + pubTypesMnems.get(type) + "' " +
	            		" and pub_titles.pub_id = publications.id " + 
                        " and lower(pub_titles.title) = '" + title.toLowerCase() + "' " + 
	            		" and pub_uris.id = publications.id " +
	            		" and pub_relations.object_id = publications.id " +
	            		" and pub_relations.subject_id = parent_uris.id " +
	            		" and parent_uris.uri = '" + parentUri + "'" + 
	            		" and pub_relations.type = -6700");

	            if (rs.next())
	            	return rs.getString("uri");
	    } catch (ClassNotFoundException e)
	    {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	    
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//throw new RuntimeException(e);
		} 
		return null;
	}
	
	
	public static void main(String[] args)
	{
		System.out.println(getPersonUriFromDataBase(new PersonName("Konstantinova", "T.", "V.")));
		System.out.println(getPublicationUriFromDataBase("Single nano-hole as a new effective nonlinear element for third-harmonic generation", Publication.publicationTypes.paperinMagazine));
		System.out.println(getPublicationUriFromDataBaseByParentUri("number 5",  Publication.publicationTypes.issueOfaMagazine, "volume671090710:06-10-13/volume42/year2013" ));
	}
}
