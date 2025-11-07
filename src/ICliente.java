import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICliente extends Remote {

    String getNombre() throws RemoteException;;

    void amigoConectado(String amigo, ICliente interfaz) throws RemoteException;

    void amigoDesconectado(String amigo) throws RemoteException;

    void enviar(String de, String mensaje) throws RemoteException;
}
