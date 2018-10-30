package org.benfarhat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.benfarhat.beans.Car;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * Hello world!
 *
 */
public class App 
{

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    
    public static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public static final String protocol = "jdbc:derby:";
	public static final String dbName = "derbyDB";
	
    public static void main( String[] args ) throws SQLException
    {
    	App app = new App();
    	try {
    		Class.forName(driver).newInstance();

            System.out.println( "Connection to Derby" );
            app.connectionToDerby();
            System.out.println( "Executing statement" );
            app.demoDB();
            app.close();
            
    	} catch (Exception e0) {
    		System.out.println(e0);
    	}
    	System.out.println("Using Spring JDBC");
    	
    	try {
    		app.WithJdbc();
    	} catch (Exception e4) {
    		System.out.println(e4);
    	}
    }
    
    public void connectionToDerby() throws SQLException {
    	
    	String dbURL = App.protocol + App.dbName + ";create=true";
    	conn = DriverManager.getConnection(dbURL);
    }
    
    public void demoDB() throws SQLException {
    	Statement stmt = conn.createStatement();
    	System.out.println( "Droping table" );
        try {
            stmt.executeUpdate("DROP TABLE CARS");
        	System.out.println( "Done!\n" );
        } catch (SQLException e1) {
        	System.out.println(e1);
            if (!e1.getSQLState().equals("42Y55"))
                throw e1;
        } 
        System.out.println( "Creating TABLE CARS - inserting elements" );
        try {
            stmt.executeUpdate("CREATE TABLE CARS(ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY \r\n" + 
            		"    (START WITH 1, INCREMENT BY 1), NAME VARCHAR(30), PRICE INT)");
            // insert elements
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('Audi', 50000)");
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('Alfa', 40000)");
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('BMW', 60000)");
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('Citroen', 45000)");
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('Fiat', 35000)");
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('Honda', 55000)");
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('Jeep', 66000)");
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('Nissan', 52000)");
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('Porsche', 78000)");
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('Seat', 49000)");
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('Toyota', 53000)");
            stmt.executeUpdate("INSERT INTO CARS(Name, Price) VALUES('VW', 61000)");
        	System.out.println( "Done!\n" );
        } catch (SQLException e2) {
        	System.out.println(e2);
        }
        System.out.println( "Executing Query on TABLE" );
        try {
			ResultSet rs = stmt.executeQuery("SELECT * FROM CARS");
			System.out.println("NAME\t\tPRICE($)");
			while (rs.next()) {
				System.out.printf("%s\t\t%d $\n", rs.getString("NAME"), rs.getInt("PRICE"));
			}

        	System.out.println( "\nDone!" );
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        
    }
    
    private void close() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e3) {
        	System.out.println(e3);
        }
    }
    
    public void WithJdbc() {
        
        //System.setProperty("derby.system.home", "/home/janbodnar/.derby");

        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new org.apache.derby.jdbc.EmbeddedDriver());
        dataSource.setUrl("jdbc:derby:derbyDB");

        String sql = "SELECT * FROM CARS WHERE Id=?";
        Long id = 1L;
                
        JdbcTemplate jtm = new JdbcTemplate(dataSource);
        
        Car car = (Car) jtm.queryForObject(sql, new Object[] {id}, 
                new BeanPropertyRowMapper(Car.class));        
        System.out.println("Getting One element");
        System.out.printf("%d\t%s\t%d $\n", car.getId(), car.getName(), car.getPrice());
        
        sql = "SELECT Count(*) FROM CARS";
        int numOfCars = jtm.queryForObject(sql, Integer.class);
        
        System.out.format("There are %d cars in the table", numOfCars);
        
        System.out.println("\nGetting All cars With queryForList:");
        sql = "SELECT * FROM CARS";
        List<Map<String, Object>> rows = jtm.queryForList(sql);
        /*
        for (final Map row : rows) {
        	System.out.printf("%d\t%s\t%d $\n", row.getId(), row.getName(), row.getPrice());
        }
        */
        List<Car> results = new ArrayList<Car>();
        
        rows.stream().forEach( (row) -> {
        	System.out.printf("%d\t%s\t%d $\n", row.get("ID"), row.get("NAME"), row.get("PRICE"));
        	Car voit = new Car((Long) row.get("ID"), (String) row.get("NAME"), (int) row.get("PRICE"));
        	results.add(voit);
        });
        
        System.out.println("\nGetting All cars With queryForList exctracting cars :");
        System.out.println(results);
        results.forEach((voit) -> {
        	System.out.printf("%d\t%s\t%d $\n", voit.getId(), voit.getName(), voit.getPrice());
        });

        System.out.println("\nGetting All cars With query and BeanPropertyRowMapper :");
        
    	List<Car> voitures  = jtm.query(sql,
    			new BeanPropertyRowMapper(Car.class));
    	
    	voitures.forEach((voit) -> {
        	System.out.printf("%d\t%s\t%d $\n", voit.getId(), voit.getName(), voit.getPrice());
        });
    
    
    }
}
