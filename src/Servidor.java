import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Servidor extends UnicastRemoteObject implements IServidor {

    private HashMap<String, ArrayList<String>> amigos = new HashMap<>();
    private HashMap<String, String> passwords = new HashMap<>();
    private HashMap<String, ICliente> clientes = new HashMap<>();
    private HashMap<String, String> clientesdirs = new HashMap<>();
    private HashMap<String, ArrayList<String>> solicitudesPendientes = new HashMap<>();
    // solicitudesPendientes.get(aUsuario).add(deUsuario);

    private final static String PASSWORDS_FILE = "./data/user/passwords/pswd";
    private final static String FRIENDS_FILE = "./data/user/friends/amigos";
    private final static String PENDING_REQUESTS_FILE = "./data/user/requests/solicitudes";
    private static final int RMI_PORT = 1099;

    public Servidor() throws RemoteException {
        super();
        loadAllData();
        System.out.println("Servidor RMI iniciado");
        System.out.println("Usuarios registrados: " + passwords.keySet());
        servir(this, RMI_PORT);
    }

    private ArrayList<String> obtenerUsuarios() {
        return new ArrayList<>(this.passwords.keySet());
    }

    private void loadAllData() {
        passwords.putAll(Utils.loadUsersFromFile(PASSWORDS_FILE));
        amigos.putAll(Utils.loadFriendsFromFile(FRIENDS_FILE));
        solicitudesPendientes.putAll(Utils.loadFriendsFromFile(PENDING_REQUESTS_FILE));
    }

    private void saveAllData() {
        Utils.saveUsersToFile(passwords, PASSWORDS_FILE);
        Utils.saveFriendsToFile(amigos, FRIENDS_FILE);
        Utils.saveFriendsToFile(solicitudesPendientes, PENDING_REQUESTS_FILE);
    }

    private boolean loginExitoso(String nombre, String clave) {
        return obtenerUsuarios().contains(nombre) &&
                passwords.get(nombre).equals(clave);
    }

    @Override
    public boolean login(String nombre, String clave, String direccion) throws RemoteException {
        if (!loginExitoso(nombre, clave))
            return false; // nombre y clave no coinciden

        try {
            clientes.put(nombre, (ICliente) Naming.lookup(direccion));
            clientesdirs.put(nombre, direccion);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }

        for (String amigo : amigos.get(nombre)) {
            // Posible null exception si algo va mal
            clientes.get(amigo).amigoConectado(nombre);
        }

        return true;
    }

    @Override
    public void logout(String nombre) throws RemoteException {
        for (String amigo : amigos.get(nombre)) {
            // Posible null exception si algo va mal
            clientes.get(amigo).amigoDesconectado(nombre);
        }

        clientes.remove(nombre);
        clientesdirs.remove(nombre);
    }

    @Override
    public boolean registrarUsuario(String user, String clave) throws RemoteException {

        if (obtenerUsuarios().contains(user))
            return false;

        amigos.put(user, new ArrayList<>());
        passwords.put(user, clave);
        solicitudesPendientes.put(user, new ArrayList<>());
        return true;
    }

    @Override
    public void solicitarAmistad(String deUsuario, String aUsuario) throws RemoteException {
        if (amigos.get(aUsuario).contains(deUsuario))
            return; // No mandar si ya son amigos
        solicitudesPendientes.get(aUsuario).add(deUsuario);
    }

    @Override
    public List<String> getSolicitudesPendientes(String usuario) throws RemoteException {
        return solicitudesPendientes.get(usuario);
    }

    @Override
    public boolean aceptarSolicitudAmistad(String usuario, String amigo) throws RemoteException {
        amigos.get(usuario).add(amigo);
        amigos.get(amigo).add(usuario);

        solicitudesPendientes.get(usuario).remove(amigo);
        solicitudesPendientes.get(amigo).remove(usuario);

        if (clientes.containsKey(usuario))
            clientes.get(usuario).nuevoAmigo(amigo);
        if (clientes.containsKey(amigo))
            clientes.get(amigo).nuevoAmigo(usuario);
        return true;
    }

    @Override
    public boolean rechazarSolicitudAmistad(String usuario, String amigo) throws RemoteException {
        solicitudesPendientes.get(usuario).remove(amigo);
        solicitudesPendientes.get(amigo).remove(usuario);
        return true;
    }

    // ===== INFORMACIÓN DE USUARIOS =====
    @Override
    public ArrayList<String> getAmigos(String user) throws RemoteException {
        return amigos.get(user);
    }

    @Override
    public boolean isUsuarioConectado(String user) throws RemoteException {
        return clientes.containsKey(user);
    }

    public static void main(String[] args) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(RMI_PORT);
            Servidor servidor = new Servidor();
            Naming.rebind("Servidor", servidor);
            System.out.println("Servidor RMI listo en puerto " + RMI_PORT);
        } catch (Exception e) {
            System.out.println("Error iniciando servidor: " + e);
        }
    }

    @Override
    public String obtenerDireccionRMI(String usuario) throws RemoteException {
        return isUsuarioConectado(usuario) ? clientesdirs.get(usuario) : null;
    }

    public void servir(Servidor servidor, Integer puerto) {

        try {
            startRegistry(puerto);
            String registryURL = "rmi://localhost:" + puerto + "/Servidor";
            Naming.rebind(registryURL, servidor);
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
}
