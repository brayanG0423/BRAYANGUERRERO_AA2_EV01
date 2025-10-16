package stayconnect;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 *
 * @author brayanG
 */
public class Stayconnect {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MySQLConect bdConnect = new MySQLConect();
            Connection con = bdConnect.abrirConexion();
            String RUT, nombre, telefono, metodoPago, estado;
            Date fechaNacimiento;
            LocalDate fecha = LocalDate.of(2001, 10, 27);
            fechaNacimiento = Date.valueOf(fecha);
            fecha = LocalDate.of(2025, 10, 20);
            Date fechaInicio = Date.valueOf(fecha);
            fecha = LocalDate.of(2025, 10, 23);
            Date fechaFin = Date.valueOf(fecha);
            nombre = "Brayan Guerrero";
            RUT = "1000493547";
            telefono = "3202825809";
            metodoPago = "MasterCard";
            estado = "pendiente";
            
            //bdConnect.mostrarReserva(con);    
            //bdConnect.crearReserva(con, RUT, nombre, fechaNacimiento, telefono, metodoPago, fechaInicio, fechaFin, estado);
            //bdConnect.mostrarReserva(con);
            //bdConnect.actualizarHuesped(con, RUT, nombre, fechaNacimiento, telefono, metodoPago);
            
            //bdConnect.mostrarReserva(con);
            //bdConnect.eliminarReserva(con, 7);
            //bdConnect.actualizarReserva(con, 7, fechaInicio, fechaFin, estado);
            bdConnect.mostrarReserva(con);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
