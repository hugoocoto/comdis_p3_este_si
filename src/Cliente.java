import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Cliente extends UnicastRemoteObject implements ICliente {

    private String nombre;
    private String clave;
    private Integer puerto;
    private IServidor servidor;
    private static final String SERVER_HOST = "localhost";
    private static final Integer SERVER_PORT = 1099;
    /* Todos los mensajes enviados y recibidos de / a un amigo conectado */
    private volatile HashMap<String, ArrayList<String>> mensajes;
    private HashMap<String, ICliente> amigosConectados;

    public Cliente() throws RemoteException {
        super();
        mensajes = new HashMap<>();
        amigosConectados = new HashMap<>();
    }

    public boolean logout(String nombre) {
        try {
            servidor.logout(nombre);
            return true;
        } catch (RemoteException e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean login(String nombre, String clave, Integer puerto) {
        this.nombre = nombre;
        this.clave = clave;
        this.puerto = puerto;
        try {
            /*
             * El cliente tiene que servir el objeto RMI antes de hacer el login
             * en el servidor, porque es ahi donde el servidor intenta obtener
             * la interfaz remota del cliente
             */
            servidor = getServer("rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/Servidor");
            servir(this, puerto);
            return servidor.login(nombre, Utils.encrypt(clave), "rmi://localhost:" + puerto + "/Cliente");

        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            System.out.println(e);
            return false;
        }
    }

    @Override
    public String getNombre() {
        return this.nombre;
    }

    @Override
    public void nuevoAmigo(String amigo) throws RemoteException {
    }

    @Override
    /*
     * Esta funcion se llama cada vez que un cliente le envia un mensaje a este
     * cliente. La invova a traves de la interfaz remota. Este cliente no debe
     * usar esta llamada para enviar a otra persona. Para esto, existe enviar
     * con parametros ICliente y String. Somos totalmente conscientes de la
     * confusion que puede causar esto pero desde el punto de vista de enviar un
     * mensaje a un cliente remoto es lo mas intuitivo.
     */
    public void enviar(String de, String mensaje) throws RemoteException {
        // Este no se tiene que usar
        synchronized (this) {
            this.notifyAll();
        }
        mensajes.get(de).add(de + ": " + mensaje);
    }

    // Este para enviar
    public void enviar(ICliente a, String mensaje) {
        try {
            a.enviar(this.nombre, mensaje);
            mensajes.get(a.getNombre()).add(nombre + ": " + mensaje);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void amigoConectado(String amigo, ICliente interfaz) {
        amigosConectados.put(amigo, interfaz);
        mensajes.put(amigo, new ArrayList<>());
    }

    @Override
    public void amigoDesconectado(String amigo) throws RemoteException {
        amigosConectados.remove(amigo);
        mensajes.remove(amigo);
    }

    public void servir(ICliente cliente, Integer puerto) {

        try {
            startRegistry(puerto);
            String registryURL = "rmi://localhost:" + puerto + "/Cliente";
            Naming.rebind(registryURL, cliente);
            System.out.println("Server registered. Registry currently contains:");

            listRegistry(registryURL);
            System.out.println("Server ready.");
        } catch (Exception re) {
            System.out.println("servir: " + re);
        }
    }

    private static void startRegistry(int RMIPortNum) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();

        } catch (RemoteException e) {

            System.out.println("RMI registry cannot be located at port " + RMIPortNum);
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println("RMI registry created at port " + RMIPortNum);
        }
    }

    private static void listRegistry(String registryURL) throws RemoteException, MalformedURLException {
        System.out.println("Registry " + registryURL + " contains: ");
        String[] names = Naming.list(registryURL);
        for (String name : names) {
            System.out.println(name);
        }
    }

    private IServidor getServer(String host) throws MalformedURLException, RemoteException, NotBoundException {
        return (IServidor) Naming.lookup(host);
    }

    public boolean register(String nombre2, String clave2) {
        try {
            return servidor.registrarUsuario(nombre2, Utils.encrypt(clave2));
        } catch (RemoteException e) {
            System.out.println(e);
            return false;
        }
    }

    public ArrayList<String> getchats() {
        return new ArrayList<String>(mensajes.keySet());
    }

    public ArrayList<String> getamigos() {
        try {
            return new ArrayList<>(servidor.getAmigos(nombre));
        } catch (RemoteException e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public void enviarSolicitud(String string) {
        try {
            servidor.solicitarAmistad(nombre, string);
        } catch (RemoteException e) {
            System.out.println(e);
        }
    }

    public void aceptarSolicitud(String string) {
        try {
            servidor.aceptarSolicitudAmistad(nombre, string);
        } catch (RemoteException e) {
            System.out.println(e);
        }
    }

    public ArrayList<String> buscarUsuario(String ask) {
        try {
            return servidor.buscarUsuario(ask);
        } catch (RemoteException e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public ArrayList<String> getSolicitudes() {
        try {
            return new ArrayList<>(servidor.getSolicitudesPendientes(nombre));
        } catch (RemoteException e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public ArrayList<String> getMensajes(String amigo) {
        return new ArrayList<>(mensajes.get(amigo));
    }

    public ICliente getInterfaz(String amigo) {
        return amigosConectados.get(amigo);
    }

}
