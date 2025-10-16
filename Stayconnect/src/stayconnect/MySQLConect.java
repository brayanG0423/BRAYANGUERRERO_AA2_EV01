package stayconnect;

import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;

public class MySQLConect {

    private String url = "jdbc:mysql://localhost:3306/stayconnect";
    private String usuario = "root";
    private String contraseña = "";

    public MySQLConect() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conectando a la base de datos...");
            conexion.close();
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Error: No se encontró el driver de MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar a la base de datos.");
            e.printStackTrace();
        }
        System.out.println("Conexion a la base de datos exitosa");
    }

    public Connection abrirConexion() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, usuario, contraseña);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el driver de MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos.");
            e.printStackTrace();
        }
        return conexion;
    }

    public void cerrarConexion(Connection conexion) {
        try {
            conexion.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión o consulta SQL");
            e.printStackTrace();
        }
    }

    public void mostrarReserva(Connection conexion) {
        String sql = "SELECT \n"
                + "    r.id_reserva,\n"
                + "    h.nombre_huesped,\n"
                + "    h.RUT_huesped,\n"
                + "    h.fecha_nacimiento,\n"
                + "    h.metodo_pago_favorito,\n"
                + "    h.telefono,\n"
                + "    r.fecha_inicio,\n"
                + "    r.fecha_fin,\n"
                + "    r.estado\n"
                + "FROM reserva r\n"
                + "JOIN huesped h ON r.id_huesped = h.id_huesped;";
        try {
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_reserva");
                String nombre = rs.getString("nombre_huesped");
                String RUT = rs.getString("RUT_huesped");
                java.sql.Date fechaNacimiento = rs.getDate("fecha_nacimiento");
                String telefono = rs.getString("telefono");
                java.sql.Date fechaInicio = rs.getDate("fecha_inicio");
                java.sql.Date fechaFin = rs.getDate("fecha_fin");
                String estado = rs.getString("estado");
                String metodoPago = rs.getString("metodo_pago_favorito");
                System.out.println("===============================================================================================================");
                System.out.printf("%-5s | %-15s | %-12s | %-15s | %-12s | %-12s | %-12s | %-12s | %-20s%n",
                        "ID", "Nombre", "RUT", "Nacimiento", "Teléfono", "Inicio", "Fin", "Estado", "Método Pago");
                System.out.println("---------------------------------------------------------------------------------------------------------------");
                System.out.printf("%-5d | %-15s | %-12s | %-15s | %-12s | %-12s | %-12s | %-12s | %-20s%n",
                        id, nombre, RUT, fechaNacimiento, telefono, fechaInicio, fechaFin, estado, metodoPago);
                System.out.println("===============================================================================================================");

            }
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private long existeHuesped(Connection conexion, String RUT, String nombre, Date fechaNacimiento, String telefono, String metodoPago) {
        long idHuesped = 0;
        String query = "SELECT id_huesped FROM huesped WHERE RUT_huesped = ?";
        try {
            PreparedStatement pstmt = conexion.prepareStatement(query);
            pstmt.setString(1, RUT);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {//SI NO EXISTE- SI NO HAY NADA RETORNA FALSO
                String sqlInsert = "INSERT INTO huesped(RUT_huesped, nombre_huesped, fecha_nacimiento, telefono, metodo_pago_favorito) VALUES (?,?,?,?,?);";
                PreparedStatement ps = conexion.prepareStatement(sqlInsert);
                ps.setString(1, RUT);
                ps.setString(2, nombre);
                ps.setDate(3, fechaNacimiento);
                ps.setString(4, telefono);
                ps.setString(5, metodoPago);
                ps.executeUpdate();

                PreparedStatement getIdstmt = conexion.prepareStatement("SELECT LAST_INSERT_ID()");
                ResultSet rsId = getIdstmt.executeQuery();

                if (rsId.next()) {
                    idHuesped = rsId.getLong(1);
                    System.out.println("Huesped Creado con Id: " + idHuesped);
                } else {
                    System.out.println("Error");
                }

                rsId.close();
                //DEVOLVER EL ID INGRESADO
            } else {
                idHuesped = rs.getInt("id_huesped");
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
            e.printStackTrace();
        }
        return idHuesped;
    }

    public void crearReserva(Connection conexion, String RUT, String nombre, Date fechaNacimiento, String telefono, String metodoPago, Date fechaInicio, Date fechaFin, String estado) {
        String sql = "INSERT INTO reserva(id_huesped, fecha_inicio, fecha_fin, estado) VALUES (?,?,?,?);";
        long idHuesped = existeHuesped(conexion, RUT, nombre, fechaNacimiento, telefono, metodoPago);
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, idHuesped);
            ps.setDate(2, fechaInicio);
            ps.setDate(3, fechaFin);
            ps.setString(4, estado);
            ps.executeUpdate();
            ps.close();
            System.out.println("La reserva fue creada.");
        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actualizarHuesped(Connection conexion, String RUT, String nombre, Date fechaNacimiento, String telefono, String metodoPago) {
        String query = "SELECT id_huesped FROM huesped WHERE RUT_huesped = ?";
        try {
            PreparedStatement pstmt = conexion.prepareStatement(query);
            pstmt.setString(1, RUT);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {//SI NO EXISTE- SI NO HAY NADA RETORNA FALSO
                System.out.println("No existe el huesped con ese RUT");
                //DEVOLVER EL ID INGRESADO
            } else {
                String queryUpdate = "UPDATE huesped SET nombre_huesped=?, fecha_nacimiento=?, telefono = ?, metodo_pago_favorito = ? WHERE RUT_huesped = ?;";
                PreparedStatement ps = conexion.prepareStatement(queryUpdate);
                ps.setString(1, nombre);
                ps.setDate(2, fechaNacimiento);
                ps.setString(3, telefono);
                ps.setString(4, metodoPago);
                ps.setString(5, RUT);
                ps.executeUpdate();
                System.out.println("Huesped actulizado");
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actualizarReserva(Connection conexion, int id_reserva, Date fechaInicio, Date fechaFin, String estado) {
        String sql = "UPDATE reserva SET fecha_inicio = ?, fecha_fin = ?, estado=? WHERE id_reserva = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setDate(1, fechaInicio);
            ps.setDate(2, fechaFin);
            ps.setString(3, estado);
            ps.setInt(4, id_reserva);
            ps.executeUpdate();
            ps.close();
            System.out.println("La reserva fue actualizada.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarReserva(Connection conexion, int id_reserva) {
        String sql = "DELETE FROM reserva WHERE id_reserva = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_reserva);
            ps.executeUpdate();
            ps.close();
            System.out.println("La reserva fue eliminada del sistema.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
