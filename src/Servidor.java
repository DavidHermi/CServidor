import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor {


    //Declaraciones
    private Socket socket;
    private ServerSocket serverSocket;
    private DataInputStream EntradaDatos;
    private DataOutputStream SalidaDatos;
    Scanner escaner = new Scanner(System.in);
    final String COMANDO_TERMINACION = "salir()";


    //Levantamos conexion del servidor
    public void levantarConexion(int puerto) {
        try {
            //Levantamos la conexion diciendole el puerto
            serverSocket = new ServerSocket(puerto);
            //Texto para recibir la conexion del cliente
            mostrarTexto("Esperando conexión entrante en el puerto " + String.valueOf(puerto) + "...");
            //aceptamos conexion
            socket = serverSocket.accept();
            //texto conexion establecida
            mostrarTexto("Conexión establecida con: " + socket.getInetAddress().getHostName() + "\n\n\n");

        } catch (Exception e) {
            //Excepcion por si falla la conexion
            mostrarTexto("Error en levantarConexion(): " + e.getMessage());
            System.exit(0);
        }
    }


    //metodo flujos con los datas imput y output
    public void flujos() {
        try {
            // OBJETO para entrada de datos
            EntradaDatos = new DataInputStream(socket.getInputStream());
            //objeto salida de datos
            SalidaDatos = new DataOutputStream(socket.getOutputStream());
            //flujo de salida de datos
            SalidaDatos.flush();
        } catch (IOException e) {
            //Exception por si falla el try
            mostrarTexto("Error en la apertura de flujos");
        }
    }

    //Metodo para recibir datos
    public void recibirDatos() {
        //string del mensaje
        String st = "";
        try {
            do {
                //Recibimos mensaje cliente
                st = EntradaDatos.readUTF();
                //Mostramos por pantalla el mensaje recibido
                mostrarTexto("\n[Cliente] : " + st);
                //Escribimos el nuestro
                System.out.print("\n[Usted] : ");

            } while (true);
        } catch (IOException e) {
            //Excepcion si falla cerrar conexion
            cerrarConexion();
        }
    }

    //Metodo para enviar el mensaje de vuelta al cliente
    public void enviar(String s) {
        try {
            //Enviamos el mensaje devuelto con el String y él data output
            SalidaDatos.writeUTF(s);
            //Flush para que fluyan bien los datos
            SalidaDatos.flush();
        } catch (IOException e) {
            //Exception  mensaje por  si fala el try
            mostrarTexto("Error en enviar(): " + e.getMessage());
        }
    }

    public static void mostrarTexto(String s) {
        System.out.print(s);
    }

    public void escribirDatos() {

        while (true) {
            System.out.print("[Usted] => ");
            enviar(escaner.nextLine());
        }
    }

    // Metoodo cerrar conexion
    public void cerrarConexion() {
        try {
            EntradaDatos.close();
            SalidaDatos.close();
            socket.close();
        } catch (IOException e) {
            mostrarTexto("Excepción en cerrarConexion(): " + e.getMessage());
        } finally {
            mostrarTexto("Conversación finalizada....");
            System.exit(0);

        }
    }

    //Metodo ejecutar connexion
    public void ejecutarConexion(int puerto) {

        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        levantarConexion(puerto);
                        flujos();
                        recibirDatos();
                    } finally {
                        cerrarConexion();
                    }
                }
            }
        });
        hilo.start();
    }

    public static void main(String[] args) throws IOException {
        Servidor s = new Servidor();
        Scanner sc = new Scanner(System.in);

        mostrarTexto("Ingresa el puerto [5050 por defecto]: ");
        String puerto = sc.nextLine();
        if (puerto.length() <= 0) puerto = "5050";
        s.ejecutarConexion(Integer.parseInt(puerto));
        s.escribirDatos();
    }
}
