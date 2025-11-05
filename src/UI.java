import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

public class UI {
    private BufferedReader br;

    public UI() throws RemoteException {
        String nombre;
        String clave;
        Integer puerto;

        br = new BufferedReader(new InputStreamReader(System.in));
        Cliente c = new Cliente();

        while (!c.login(
                nombre = ask("Nombre: "),
                clave = ask("Clave: "),
                puerto = Integer.parseInt(ask("Puerto: "))))
            ;
        System.out.println("Login exitoso!");

        Integer status = mainloop();
        c.logout(nombre);
        System.exit(status);
    }

    private Integer mainloop() {
        ask("Press any button to close: ");
        return 0; // Exit code
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

    public static void main(String[] args) {
        try {
            new UI();
        } catch (RemoteException e) {
            System.out.println(e);
        }
    }
}
