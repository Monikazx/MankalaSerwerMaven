package mankalaDB;

import java.sql.*;
import java.util.Properties;

public class PolaczBD {
    private static Connection connection = null;

    public static void polaczBD() throws SQLException {
        String url = "jdbc:mysql://localhost/studenci";
        Properties properties = new Properties();
        properties.setProperty("user","root");
        properties.setProperty("password","");

        try {
            connection = DriverManager.getConnection(url, properties);
            System.out.println("Połączono z bazą danych studenci ...");
        } catch (SQLException e) {
            System.out.println("Błąd połączenia z bazą danych studenci ...");
            e.printStackTrace();
            throw e;
        }
    }

    public static void rozlaczBD() throws SQLException {
        try {
            if (connection!=null  && !connection.isClosed()){
                connection.close();
                connection = null;
                System.out.println("Zamknięto połączenie z bazą danych studenci ...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Błąd podczas zamykania połączenia z bazą danych studenci ...");
            throw e;
        }
    }


    public static void wykonajDML(String sql) throws SQLException {
        Statement statement = null;

        try {
            if(connection == null){
                throw new SQLException();
            }
            statement = connection.createStatement();
            statement.execute(sql);
            System.out.println("Pomyślnie wykonano instarukcję SQL typu DML.");
        } catch (SQLException e) {
            System.out.println("Błąd podczas wkonywania instarukcji SQL typu DML.");
            e.printStackTrace();
            throw e;

        } finally {
            if(!statement.isClosed())
                statement.close();

        }
    }




    public static ResultSet pobierzDane(String sql) throws SQLException {
        polaczBD();
        Statement statement = null;
        ResultSet resultSet = null;

        statement = connection.createStatement();
        resultSet = statement.executeQuery(sql);

        return resultSet;
    }

}
