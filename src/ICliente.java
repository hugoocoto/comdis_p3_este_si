import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * ICliente. Interfaz que implementan los clientes para conectarse tanto entre
 * sí como con el servidor.
*/
public interface ICliente extends Remote {

    /*
     * Este método permite enviar un mensaje al cliente que implementa esta
     * interfaz; debe especificarse en DE el nombre de quién envia el mensaje y
     * en MENSAJE el mensaje en formato de String.
     */
    void enviar(String de, String mensaje)
            throws RemoteException;

    /*
     * Obtiene el nombre de usuario del cliente a quién le pertenece la
     * interfaz.
     */
    String getNombre()
            throws RemoteException;

    /*
     * Notifica al cliente que implementa esta interfaz que su amigo, llamado
     * AMIGO, cuya interfaz es INTERFAZ está conectado.
     */
    void notificarAmigoConectado(String amigo, ICliente interfaz)
            throws RemoteException;

    /*
     * Notifica al cliente que implementa esta interfaz que su amigo, llamado
     * AMIGO, se desconectó.
     */
    void notificarAmigoDesconectado(String amigo)
            throws RemoteException;

    /*
     * Notifica al cliente que implementa esta interfaz que un cliente, de
     * nombre USUARIO le mandó solicitud.
     */
    void notificarSolicitudPendiente(String usuario)
            throws RemoteException;
}
