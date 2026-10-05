package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    public static Connection makeConnection(){
        //chargement driver
        try {
            Class.forName(DataBaseConfig.NOM_DRIVER);
            System.out.println("DRIVER OK ");


        }
        catch (ClassNotFoundException e){
            System.out.println("erreur driver "+e.getMessage());

        }
        //conection  a la base
        Connection con =null;
        try {
            con= DriverManager.getConnection(DataBaseConfig.URL_DB,DataBaseConfig.USERNAME,DataBaseConfig.PASSWORD);
            System.out.println("connected ..");
        }
        catch (SQLException e){
            System.out.println("erreur de cnx "+e.getMessage());
        }

        return con;
    }

}
