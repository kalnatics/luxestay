/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author kallisya
 */
public class Koneksi {
    static Connection koneksi;
    
    public static Connection getConnection(){
        if (koneksi==null){
            MysqlDataSource data = new MysqlDataSource();
            data.setDatabaseName("hotelbook");
            data.setUser("root");
            data.setPassword("");
            
            try{
                koneksi = data.getConnection();
                System.out.println("Koneksi Sukses");
            } catch(SQLException se){
                System.out.println("Koneksi Gagal, " + se);
            }
        }
        return koneksi;
    }
    public static void main(String[] args) {
       getConnection();
    }
}
