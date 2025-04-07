//Este programa es un servidor que escucha en el puerto 5000 y espera que un cliente se conecte.
// Importar las liberias necesarias
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

// Clase Servidor
// Esta clase es la que se encarga de crear el servidor y de recibir las conexiones de los clientes
public class Servidor {
    public static void main(String[] args) { //Metodo principal
        // Crear el servidor que escucha en el puerto 5000
        int puerto = 5000;

        //Crear el socket del servidor
        // El socket del servidor es el que escucha las conexiones de los clientes
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor esperando conexion en el puerto " + puerto); // Mensaje que indica que el servidor esta esperando una conexion
            while (true) { //
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado. Direccion IP; " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                
                // Crear los flujos de entrada y salida para el socket
                //Los flujos de entrada y salida son los que se encargan de enviar y recibir datos del cliete
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(),true);

                // Recibir el mensaje del cliente
                String telefono = entrada.readLine();
                String respuesta = buscarPersona(telefono); //LLamar al metodo buscarPersona para buscar la persona en la base de datos
                System.out.println("Numero recibido; " + telefono); // Mensaje que indica que se ha recibido un numero de telefono
                System.out.println("Respuesta del servidor: " + respuesta); // Mensaje que indica que se ha recibido una respuesta del servidor
                salida.println(respuesta); // Enviar la respuesta al cliente)
                socket.close(); // Cerrar el socket
            }
        } catch (IOException e) { // Capturar la excepcion de entrada y salida
            System.out.println("Error al crear el servidor: " + e.getMessage()); // Mensaje que indica que ha ocurrido un error al crear el servidor
        }
    }

public static String buscarPersona(String telefono) {
    String url = "jdbc:mysql://localhost:3306/mi_basedatos"; // URL de la base datos
    String usuario = "cliente"; // Usuario de la base de datos esclusivo para la conexion Cliente - Servidor
    String contrasena = "456789"; // Contraseña de la base de datos
    String consulta = "SELECT p.dir_tel, p.dir_nombre, p.dir_direccion, c.ciud_nombre " +
                        "FROM personas p JOIN ciudades c ON p.dir_ciudad_id = c.ciud_id " +
                        "WHERE p.dir_tel = ?"; // Consulta SQL para buscar la persona por su numero de telefono
    String resultado = ""; // Variable que almacena el resultado de la consulta

    try (Connection conexion = DriverManager.getConnection(url, usuario, contrasena); // Crear la conexion a la base de datos
            PreparedStatement sentencia = conexion.prepareStatement(consulta)) { // Crear la sentencia SQL preparada
                
                sentencia.setString(1, telefono); // Establecer el numero de telefono en la consulta
                ResultSet resultadoConsulta = sentencia.executeQuery(); // Ejecutar la consulta

                if ( resultadoConsulta.next()) {
                    return "Telefono> " + resultadoConsulta.getString(1) + "\n" +
                            "Nombre: " + resultadoConsulta.getString(2) + "\n" +
                            "Direccion: " + resultadoConsulta.getString(3) + "\n" +
                            "Ciudad: " + resultadoConsulta.getString(4); // Retornar el resultado de la consulta
                } else {
                    return "Persona duena de ese numero telefonico no existe."; // Retornar un mensaje indicando que no se ha encontrado la persona
                }
            } catch (SQLException e) { // Capturar la excepcion de SQL
                e.printStackTrace(); //Imprimir la traza de la excepcion en la consola del servidor
                return "Error en el servidor"; // Retornar un mensaje indicando que ha ocurrido un error al buscar la persona
            }
    }
}
// Fin de la clase Servidor
// Fin del programa