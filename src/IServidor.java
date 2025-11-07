import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/* Problema de seguridad
 *
 * Una vez un usuario se loguea, puede hacerse pasar por otro, usando su nombre
 * al llamar a otras funciones.
 *
 * Solucion: Pedir usuario y clave en todas las funciones que supongan un riesgo
 * para la seguridad de la aplicacion. 
 *
 * Motivo por el que no esta hecho: Para mantener la legibilidad y claridad del
 * codigo ademas de evitar posibles problemas por producir demasiado codigo en
 * muy poco tiempo. Es una funcionalidad que se puede implementar en el futuro
 * facilmente.
 */

public interface IServidor extends Remote {

    // ===== AUTENTICACIÓN Y CONEXIÓN =====
    boolean login(String user, String password, String direccion) throws RemoteException;

    void logout(String user) throws RemoteException;

    boolean registrarUsuario(String user, String password) throws RemoteException;

    // ===== GESTIÓN DE AMISTADES =====
    void solicitarAmistad(String deUsuario, String aUsuario) throws RemoteException;

    ArrayList<String> getSolicitudesPendientes(String usuario) throws RemoteException;

    boolean aceptarSolicitudAmistad(String usuario, String amigo) throws RemoteException;

    boolean rechazarSolicitudAmistad(String usuario, String amigo) throws RemoteException;

    // ===== INFORMACIÓN DE USUARIOS =====
    ArrayList<String> getAmigos(String user) throws RemoteException;

    boolean isUsuarioConectado(String user) throws RemoteException;

    String obtenerDireccionRMI(String usuario) throws RemoteException;

    ArrayList<String> buscarUsuario(String ask) throws RemoteException;

    boolean existeUsuario(String user) throws RemoteException;
}
