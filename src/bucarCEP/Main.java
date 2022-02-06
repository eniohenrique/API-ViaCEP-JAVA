package bucarCEP;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

	public static void main(String[] args) throws Exception {
		
		System.out.print("Informe o CEP que deseja pesquisar: ");
		String cep = new Scanner(System.in).nextLine();
		String webService = "http://viacep.com.br/ws/";
		String url_str =  webService + cep + "/json";
		
		Connection connection = null;
		 
	    try{
	       connection = DriverManager.getConnection( "jdbc:sqlite:data.sqlite" );
	       if ( connection != null ){
	          System.out.println("Banco de dados conectado!");
	       }
	    } catch ( Exception ex ) {
	         System.err.println( ex.getClass().getName() + ": " + ex.getMessage() );
	         System.out.println("Erro na conexão com o banco de dados");
	    }

		try {
		    URL url = new URL(url_str);
		    HttpURLConnection request = (HttpURLConnection) url.openConnection();
		    request.connect();
		    
		    if(request.getResponseCode() == 200) {
		    	JsonParser jp = new JsonParser();
			    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
			    JsonObject jsonobj = root.getAsJsonObject();
			    
			    System.out.println(jsonobj);
			    //System.out.println("BAIRRO: "+jsonobj.get("bairro"));
			    
			    Statement statement = connection.createStatement();
			    /*
			    statement.execute("CREATE TABLE viaCEP ("
			    		+ "     id_via_cep INTEGER PRIMARY KEY AUTOINCREMENT,"
			    		+ "     cep varchar(8) NOT NULL,"
			    		+ "  	logradouro varchar(800) NOT NULL,"
			    		+ "  	complemento varchar(800),"
			    		+ "  	bairro varchar(800) NOT NULL,"
			    		+ "  	localidade varchar(200) NOT NULL,"
			    		+ "  	uf varchar(20) NOT NULL,"
			    		+ "  	ibge varchar(200),"
			    		+ "  	gia varchar(800),"
			    		+ "  	ddd varchar(20) NOT NULL,"
			    		+ "     siafi varchar(200)"
			    		+ ");");
			    */
			    
			    statement.execute("INSERT INTO viaCEP(cep , logradouro , complemento , bairro , localidade , uf , ibge , gia , ddd , siafi)"
			    				+ "VALUES ('"+jsonobj.get("cep")+"' , '"+jsonobj.get("logradouro")
			    				+"' , '"+jsonobj.get("complemento")+"' , '"+jsonobj.get("bairro")
			    				+"' , '"+jsonobj.get("localidade")+"' , '"+jsonobj.get("uf")
			    				+"' , '"+jsonobj.get("ibge")+"' , '"+jsonobj.get("gia")
			    				+"' , '"+jsonobj.get("ddd")+"' , '"+jsonobj.get("siafi")+"' )");
			    
			    PreparedStatement query = connection.prepareStatement("SELECT * FROM viaCEP");
	            ResultSet resultQuery = query.executeQuery();
	            
	            while (resultQuery.next()) {
	                Integer id = resultQuery.getInt("id_via_cep");
	                String logradouro = resultQuery.getString("logradouro");
	                String bairro = resultQuery.getString("bairro");
	                String localidade = resultQuery.getString("localidade");

	                System.out.println("ID: "+ id + " - Rua: " + logradouro + " - Bairro: " + bairro + " - Localidade: " + localidade);
	            }
		    }
        } catch (Exception e) {
            throw new Exception("ERRO: " + e);
        }
	}
}
