import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Pipe.SourceChannel;
import java.rmi.RemoteException;
import java.sql.Time;
import java.util.ArrayList;

public class UI {
    private BufferedReader br;
    private String nombre;
    private String clave;
    private Integer puerto;
    private boolean should_quit = false;
    private String prompt = ">> ";
    private Cliente cliente;
    private String state = "";

    public UI() throws RemoteException {
        br = new BufferedReader(new InputStreamReader(System.in));
        cliente = new Cliente();

        login(cliente);
        StartMessgelistener();
        Integer status = mainloop();
        cliente.logout(nombre);
        System.exit(status);
    }

    private void StartMessgelistener() {
        new Thread(() -> {
            while (true) {
                try {
                    synchronized (cliente) {
                        cliente.wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                refresh();
            }
        }).start();
    }

    private void refresh() {
        if (state.startsWith("chateando")) {
            refreshChat(state.substring("chateando con ".length()));
        }
    }

    private void refreshChat(String amigo) {
        System.out.print("\033[s");
        System.out.print("\033[H");
        System.out.println("Chat con " + amigo);
        displayList(cliente.getMensajes(amigo), 10, 10);
        System.out.print("\033[u");
    }

    private Boolean login(Cliente c) {
        while (true) {
            boolean status = c.login(
                    this.nombre = ask("Nombre: "),
                    this.clave = ask("Clave: "),
                    this.puerto = Integer.parseInt(ask("Puerto: ")));
            if (status)
                break;

            System.out.println("Usuario no registrado!");

            if (ask_sn("Quieres crear el usuario?")) {
                if (c.register(nombre, clave)) {
                    break;
                }
                System.out.println("No se pudo crear el usuario!");
            }

        }

        System.out.println("Login exitoso!");
        return true;
    }

    private void print_menu() {
        clearScreen();
        System.out.println("MENU");
        System.out.println("[C] chats");
        System.out.println("[A] amigos");
        System.out.println("[B] buscar");
        System.out.println("[S] solicitudes");
        System.out.println("[Q] salir");
    }

    private Integer mainloop() {
        while (!should_quit) {
            print_menu();
            char resp = ask().toLowerCase().toCharArray()[0];
            switch (resp) {
                case 'c':
                    windowChats();
                    break;
                case 'a':
                    windowAmigos();
                    break;
                case 'b':
                    windowBuscar();
                    break;
                case 's':
                    windowSolicitudes();
                    break;
                case 'q':
                    should_quit = true;

            }
        }
        return 0; // Exit code
    }

    private void displayList(ArrayList<String> list, Integer max) {
        max = Math.min(list.size(), max);
        for (int i = list.size() - max; i < list.size(); i++) {
            System.out.println("[" + i + "] " + list.get(i));
        }
    }

    private void displayList(ArrayList<String> list, Integer max, Integer size) {
        max = Math.min(list.size(), max);
        // I truly think that J clears from the cursor to the end of the line
        for (int i = list.size() - max; i < list.size(); i++) {
            System.out.print("| ");
            if (list.get(i).startsWith(this.nombre)) {
                System.out.print("\033[J\t\t");
            }
            System.out.println(list.get(i));
        }
        for (int i = max; i < size; i++)
            System.out.println("| ");
    }

    private void windowChats() {
        clearScreen();
        System.out.println("Chats:");
        ArrayList<String> chats = cliente.getchats();
        displayList(chats, 100);
        System.out.println("Escribe el numero de chat o vacio para salir");
        String resp = ask();
        if (resp.isEmpty())
            return;
        Integer n = getInteger(resp, 0, chats.size());
        openChat(chats.get(n));
        state = "";
    }

    private void openChat(String amigo) {
        // chat de nombre <-> amigo
        state = "chateando con " + amigo;

        while (true) {
            clearScreen();
            System.out.println("Chat con " + amigo);
            displayList(cliente.getMensajes(amigo), 10, 10);
            String resp = ask("Enviar: ");
            if (resp.isEmpty())
                return;
            if (cliente != null) {
                cliente.enviar(cliente.getInterfaz(amigo), resp);
            }
        }
    }

    private Integer getInteger(String s, Integer min, Integer max) {
        try {
            Integer n = Integer.parseInt(s);
            if (n < min || n > max)
                return -1;
            return n;
        } catch (Exception e) {
            return -1;
        }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
    }

    private void windowAmigos() {
        clearScreen();
        System.out.println("Amigos:");
        ArrayList<String> amigos = cliente.getamigos();
        displayList(amigos, 100);
        ask("Pulsa cualquier tecla para salir");
    }

    private void windowBuscar() {
        clearScreen();
        System.out.println("Buscar:");
        ArrayList<String> buscar = cliente.buscarUsuario(ask());
        System.out.println("Selecciona un numero para enviar solicitud:");
        displayList(buscar, 100);
        String resp = ask();

        if (resp.isEmpty()) {
            return;
        }

        Integer n = getInteger(resp, 0, buscar.size());
        if (n.equals(-1)) {
            return;
        }

        cliente.enviarSolicitud(buscar.get(n));
    }

    private void windowSolicitudes() {
        clearScreen();
        System.out.println("Solicitudes:");
        ArrayList<String> solis = cliente.getSolicitudes();
        System.out.println("Selecciona un numero para aceptar la solicitud:");
        displayList(solis, 100);
        String resp = ask();
        if (resp.isEmpty())
            return;
        Integer n = getInteger(resp, 0, solis.size());
        cliente.aceptarSolicitud(solis.get(n));
    }

    private String ask(String prompt) {
        try {
            System.out.print(prompt);
            return br.readLine();

        } catch (IOException e) {
            System.out.println(e);
            return "";
        }
    }

    private String ask() {
        return ask(prompt);
    }

    private boolean ask_sn(String prompt) {
        try {
            System.out.print(prompt + " (S/n) ");
            char resp = br.readLine()
                    .toLowerCase().toCharArray()[0];
            return resp == 's' || resp == 'y';

        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            new UI();
        } catch (RemoteException e) {
            System.out.println(e);
        }
    }
}
