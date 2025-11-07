// =============================================================
// SwingApp.java  —  UI Swing para tu chat RMI (azul pastel)
// con “Cambiar contraseña” integrado (abre como un chat)
// =============================================================

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

// =================== Punto de entrada ========================
public class SwingApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PastelTheme.apply();
            try {
                Cliente cliente = new Cliente();
                new LoginRegisterFrame(cliente).setVisible(true);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(null,
                        "Error iniciando cliente: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

// =================== Tema / Colores ==========================
class PastelTheme {
    public static final Color BG_LIGHT = new Color(230, 240, 248);
    public static final Color BG_PANEL = new Color(214, 228, 242);
    public static final Color ACCENT = new Color(145, 170, 208);
    public static final Color ACCENT_DARK = new Color(108, 141, 185);
    public static final Color TEXT_DARK = new Color(30, 45, 60);
    public static final Color TEXT_SOFT = new Color(70, 90, 110);

    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UIManager.put("Panel.background", BG_LIGHT);
        UIManager.put("OptionPane.background", BG_LIGHT);
        UIManager.put("OptionPane.messageForeground", TEXT_DARK);
        UIManager.put("Label.foreground", TEXT_DARK);
        UIManager.put("TextField.background", Color.white);
        UIManager.put("PasswordField.background", Color.white);
        UIManager.put("List.background", Color.white);
        UIManager.put("ScrollPane.background", BG_PANEL);
        UIManager.put("TextArea.background", Color.white);
        UIManager.put("TextArea.foreground", TEXT_DARK);
        UIManager.put("List.foreground", TEXT_DARK);
        UIManager.put("TabbedPane.selected", BG_PANEL);
    }

    public static void stylePrimary(AbstractButton b) {
        b.setBackground(ACCENT);
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(ACCENT_DARK);
            }

            public void mouseExited(MouseEvent e) {
                b.setBackground(ACCENT);
            }
        });
    }

    public static void styleSecondary(AbstractButton b) {
        b.setBackground(new Color(200, 210, 225));
        b.setForeground(TEXT_DARK);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setContentAreaFilled(true);
        b.setOpaque(true);
    }

    public static JPanel cardPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(BG_PANEL);
        p.setOpaque(true);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return p;
    }
}

// =================== Login / Registro ========================
class LoginRegisterFrame extends JFrame {
    private final Cliente cliente;

    LoginRegisterFrame(Cliente cliente) {
        super("Conectar – Chat RMI");
        this.cliente = cliente;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 420);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Login", buildLoginPanel());
        tabs.addTab("Registro", buildRegisterPanel());
        setContentPane(tabs);
    }

    private JPanel buildLoginPanel() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.setBackground(PastelTheme.BG_LIGHT);

        JLabel title = new JLabel("Chat RMI");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        JLabel sub = new JLabel("Inicia sesión para continuar");
        sub.setForeground(PastelTheme.TEXT_SOFT);

        JPanel header = new JPanel(new GridLayout(0, 1, 0, 4));
        header.setOpaque(true);
        header.setBackground(PastelTheme.BG_LIGHT);
        header.add(title);
        header.add(sub);
        root.add(header, BorderLayout.NORTH);

        JPanel card = PastelTheme.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JSpinner spPort = new JSpinner(new SpinnerNumberModel(1100, 1024, 65535, 1));

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Usuario"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        form.add(txtUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Clave"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        form.add(txtPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Puerto local"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        form.add(spPort, gbc);

        card.add(form, BorderLayout.CENTER);

        JButton btnLogin = new JButton("Entrar");
        JButton btnRegister = new JButton("Registrarse");
        PastelTheme.stylePrimary(btnLogin);
        PastelTheme.styleSecondary(btnRegister);

        btnLogin.addActionListener(e -> doLogin(txtUser, txtPass, spPort));
        btnRegister.addActionListener(e -> doQuickRegister(txtUser, txtPass));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(btnRegister);
        actions.add(btnLogin);
        card.add(actions, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildRegisterPanel() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.setBackground(PastelTheme.BG_LIGHT);

        JLabel title = new JLabel("Crear cuenta");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        JLabel sub = new JLabel("Regístrate para usar el chat");
        sub.setForeground(PastelTheme.TEXT_SOFT);

        JPanel header = new JPanel(new GridLayout(0, 1, 0, 4));
        header.setOpaque(true);
        header.setBackground(PastelTheme.BG_LIGHT);
        header.add(title);
        header.add(sub);
        root.add(header, BorderLayout.NORTH);

        JPanel card = PastelTheme.cardPanel();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        // 👇 NUEVO: spinner para el puerto (igual que en login)
        JSpinner spPortReg = new JSpinner(new SpinnerNumberModel(1100, 1024, 65535, 1));

        JButton btnRegister = new JButton("Crear cuenta");
        PastelTheme.stylePrimary(btnRegister);

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Usuario"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        form.add(txtUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Clave"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        form.add(txtPass, gbc);

        // 👇 NUEVO: fila de "Puerto local"
        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Puerto local"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        form.add(spPortReg, gbc);

        // Acción de registro + auto-login
        btnRegister.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Usuario y clave obligatorios", "Registro",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = cliente.registrarUsuario(user, pass);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Usuario creado. Iniciando sesión...");
                int port = (Integer) spPortReg.getValue();
                boolean logged = cliente.login(user, pass, port);
                if (logged) {
                    MainWindow mw = new MainWindow(this, cliente, user);
                    mw.setVisible(true);
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Registro correcto, pero no se pudo iniciar sesión.",
                            "Login", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar. ¿Quizá ya existe?",
                        "Registro", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(form, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(btnRegister);
        card.add(actions, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        return root;
    }

    private void doQuickRegister(JTextField txtUser, JPasswordField txtPass) {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena usuario y clave para registrarte.",
                    "Registro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = cliente.registrarUsuario(user, pass);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Usuario creado. Inicia sesión.");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar (¿ya existe?).",
                    "Registro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doLogin(JTextField txtUser, JPasswordField txtPass, JSpinner spPort) {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        int port = (Integer) spPort.getValue();
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuario y clave obligatorios", "Login",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = cliente.login(user, pass, port);
        if (ok) {
            MainWindow mw = new MainWindow(this, cliente, user);
            mw.setVisible(true);
            setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo iniciar sesión.",
                    "Login", JOptionPane.WARNING_MESSAGE);
        }
    }
}

// =================== Renderer de amigos ======================
class FriendListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        c.setOpaque(true);
        if (isSelected) {
            c.setBackground(PastelTheme.ACCENT);
            c.setForeground(Color.white);
        } else {
            c.setBackground(Color.white);
            c.setForeground(PastelTheme.TEXT_DARK);
        }
        c.setText("• " + value);
        return c;
    }
}

// =================== Ventana principal =======================
class MainWindow extends JFrame {
    private final JFrame parentToReturn;
    private final Cliente cliente;
    private final String usuario;

    private final DefaultListModel<String> modelConectados = new DefaultListModel<>();
    private final JList<String> listConectados = new JList<>(modelConectados);

    private final DefaultListModel<String> modelResultados = new DefaultListModel<>();
    private final JList<String> listResultados = new JList<>(modelResultados);

    private final DefaultListModel<String> modelSolicitudes = new DefaultListModel<>();
    private final JList<String> listSolicitudes = new JList<>(modelSolicitudes);

    private volatile boolean running = true;
    private javax.swing.Timer fallbackTimer; // ✅ necesario para el refresco automático

    MainWindow(JFrame parent, Cliente cliente, String usuario) {
        super("Bienvenido, " + usuario);
        this.parentToReturn = parent;
        this.cliente = cliente;
        this.usuario = usuario;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                running = false;
                cliente.logout(usuario);
                dispose();
                parentToReturn.dispose();
                System.exit(0);
            }
        });

        setSize(980, 600);
        setLocationRelativeTo(null);
        buildUI();
        startBackgroundRefresh(); // ✅ arranque del hilo y del timer
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(PastelTheme.BG_LIGHT);
        setContentPane(root);

        // Izquierda: amigos conectados + botones (sin refrescar manual)
        JPanel left = PastelTheme.cardPanel();
        JLabel lblChats = new JLabel("Amigos conectados");
        lblChats.setFont(lblChats.getFont().deriveFont(Font.BOLD));
        left.add(lblChats, BorderLayout.NORTH);

        listConectados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listConectados.setCellRenderer(new FriendListCellRenderer());
        listConectados.setOpaque(true);
        listConectados.setBackground(Color.white);
        left.add(new JScrollPane(listConectados), BorderLayout.CENTER);

        JButton btnAbrir = new JButton("Abrir chat");
        JButton btnCambiarClave = new JButton("Cambiar contraseña");
        PastelTheme.stylePrimary(btnAbrir);
        PastelTheme.styleSecondary(btnCambiarClave);

        btnAbrir.addActionListener(e -> openSelectedChat());
        btnCambiarClave.addActionListener(e -> openChangePassword());

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        leftButtons.setOpaque(false);
        leftButtons.add(btnCambiarClave);
        leftButtons.add(btnAbrir);
        left.add(leftButtons, BorderLayout.SOUTH);

        // Derecha: pestañas Buscar / Solicitudes
        JTabbedPane rightTabs = new JTabbedPane(JTabbedPane.TOP);
        rightTabs.setOpaque(true);
        rightTabs.setBackground(PastelTheme.BG_PANEL);
        rightTabs.addTab("Buscar", buildBuscarTab());
        rightTabs.addTab("Solicitudes", buildSolicitudesTab());

        root.add(left, BorderLayout.WEST);
        left.setPreferredSize(new Dimension(340, 0));
        root.add(rightTabs, BorderLayout.CENTER);
    }

    private JPanel buildBuscarTab() {
        JPanel p = PastelTheme.cardPanel();

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(true);
        top.setBackground(PastelTheme.BG_PANEL);
        JTextField txtBuscar = new JTextField();
        JButton btnBuscar = new JButton("Buscar");
        PastelTheme.styleSecondary(btnBuscar);
        btnBuscar.addActionListener(e -> {
            modelResultados.clear();
            ArrayList<String> res = cliente.buscarUsuario(txtBuscar.getText().trim());
            for (String u : res)
                if (!u.equals(usuario))
                    modelResultados.addElement(u);
        });

        top.add(new JLabel("Busca usuarios y envía solicitudes"), BorderLayout.WEST);
        JPanel search = new JPanel(new BorderLayout(6, 6));
        search.setOpaque(true);
        search.setBackground(PastelTheme.BG_PANEL);
        search.add(txtBuscar, BorderLayout.CENTER);
        search.add(btnBuscar, BorderLayout.EAST);
        top.add(search, BorderLayout.SOUTH);

        p.add(top, BorderLayout.NORTH);

        listResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listResultados.setOpaque(true);
        listResultados.setBackground(Color.white);
        p.add(new JScrollPane(listResultados), BorderLayout.CENTER);

        JButton btnSolicitar = new JButton("Enviar solicitud");
        PastelTheme.stylePrimary(btnSolicitar);
        btnSolicitar.addActionListener(e -> {
            String sel = listResultados.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un usuario primero.");
                return;
            }

            // Evita enviarte solicitud a ti mismo
            if (sel.equals(usuario)) {
                JOptionPane.showMessageDialog(
                        this,
                        "No puedes enviarte una solicitud a ti mismo.",
                        "Solicitud de amistad",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ❌ Ya sois amigos: muestra error y NO envía
            java.util.ArrayList<String> amigos = cliente.getamigos();
            if (amigos != null && amigos.contains(sel)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Ya sois amigos: " + usuario + " y " + sel,
                        "Solicitud de amistad",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ✅ Enviar solicitud
            cliente.enviarSolicitud(sel);
            JOptionPane.showMessageDialog(this, "Solicitud enviada a " + sel);
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(btnSolicitar);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildSolicitudesTab() {
        JPanel p = PastelTheme.cardPanel();

        p.add(new JLabel("Solicitudes pendientes"), BorderLayout.NORTH);
        listSolicitudes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSolicitudes.setOpaque(true);
        listSolicitudes.setBackground(Color.white);
        p.add(new JScrollPane(listSolicitudes), BorderLayout.CENTER);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnRechazar = new JButton("Rechazar");
        PastelTheme.stylePrimary(btnAceptar);
        PastelTheme.styleSecondary(btnRechazar);

        btnAceptar.addActionListener(e -> {
            String sel = listSolicitudes.getSelectedValue();
            if (sel != null) {
                cliente.aceptarSolicitud(sel);
                refreshLists();
            }
        });
        btnRechazar.addActionListener(e -> {
            String sel = listSolicitudes.getSelectedValue();
            if (sel != null) {
                cliente.rechazarSolicitud(sel);
                refreshLists();
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(btnRechazar);
        bottom.add(btnAceptar);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void openSelectedChat() {
        String sel = listConectados.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un amigo conectado");
            return;
        }
        setVisible(false);
        ChatWindow chat = new ChatWindow(this, cliente, sel);
        chat.setVisible(true);
    }

    private void openChangePassword() {
        setVisible(false);
        ChangePasswordWindow w = new ChangePasswordWindow(this, cliente);
        w.setVisible(true);
    }

    void returnFromChild() {
        setVisible(true);
        refreshLists();
    }

    // ✅ Método actualizado con fallbackTimer
    private void startBackgroundRefresh() {
        // Hilo que escucha notifyAll() del cliente (cuando se conecta o desconecta
        // alguien)
        Thread t = new Thread(() -> {
            while (running && isDisplayable()) {
                synchronized (cliente) {
                    try {
                        cliente.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
                SwingUtilities.invokeLater(this::refreshLists);
            }
        }, "UI-Refresh");
        t.setDaemon(true);
        t.start();

        // Fallback: actualiza cada 2 segundos por si se pierde alguna notificación
        fallbackTimer = new javax.swing.Timer(2000, e -> {
            if (isDisplayable())
                refreshLists();
            else
                ((javax.swing.Timer) e.getSource()).stop();
        });
        fallbackTimer.start();

        // Primera carga inicial
        refreshLists();
    }

    // Reemplaza íntegro en MainWindow
    private void refreshLists() {
        // --- Amigos conectados: preserva selección y evita reseteos innecesarios
        String prevSel = listConectados.getSelectedValue();

        ArrayList<String> online = new ArrayList<>();
        for (String a : cliente.getamigos()) {
            if (cliente.usuarioConectado(a))
                online.add(a);
        }

        boolean changed = (modelConectados.size() != online.size());
        if (!changed) {
            for (int i = 0; i < online.size(); i++) {
                if (!online.get(i).equals(modelConectados.get(i))) {
                    changed = true;
                    break;
                }
            }
        }
        if (changed) {
            // Evita eventos intermedios mientras actualizas
            listConectados.setValueIsAdjusting(true);
            modelConectados.clear();
            for (String s : online)
                modelConectados.addElement(s);
            listConectados.setValueIsAdjusting(false);
        }

        // Restaura selección si sigue existiendo
        if (prevSel != null) {
            int idx = online.indexOf(prevSel);
            if (idx >= 0) {
                listConectados.setSelectedIndex(idx);
                listConectados.ensureIndexIsVisible(idx);
            }
        }

        // --- Solicitudes pendientes (mismo patrón)
        String prevReq = listSolicitudes.getSelectedValue();
        ArrayList<String> sol = cliente.getSolicitudes();

        boolean changedReq = (modelSolicitudes.size() != sol.size());
        if (!changedReq) {
            for (int i = 0; i < sol.size(); i++) {
                if (!sol.get(i).equals(modelSolicitudes.get(i))) {
                    changedReq = true;
                    break;
                }
            }
        }
        if (changedReq) {
            listSolicitudes.setValueIsAdjusting(true);
            modelSolicitudes.clear();
            for (String s : sol)
                modelSolicitudes.addElement(s);
            listSolicitudes.setValueIsAdjusting(false);
        }
        if (prevReq != null) {
            int idx2 = sol.indexOf(prevReq);
            if (idx2 >= 0)
                listSolicitudes.setSelectedIndex(idx2);
        }
    }
}

// =================== Ventana de chat =========================
class ChatWindow extends JFrame {
    private final MainWindow parent;
    private final Cliente cliente;
    private final String amigo;

    private final DefaultListModel<String> modelMensajes = new DefaultListModel<>();
    private final JList<String> listMensajes = new JList<>(modelMensajes);
    private volatile boolean running = true;

    ChatWindow(MainWindow parent, Cliente cliente, String amigo) {
        super("Chat con " + amigo);
        this.parent = parent;
        this.cliente = cliente;
        this.amigo = amigo;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                running = false;
                parent.returnFromChild();
            }
        });

        setSize(780, 560);
        setLocationRelativeTo(parent);
        buildUI();
        startBackgroundRefresh();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(PastelTheme.BG_LIGHT);
        setContentPane(root);

        listMensajes.setBackground(Color.white);
        listMensajes.setOpaque(true);
        listMensajes.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        root.add(new JScrollPane(listMensajes), BorderLayout.CENTER);

        JPanel sendBar = PastelTheme.cardPanel();
        JTextField txt = new JTextField();
        JButton btn = new JButton("Enviar");
        PastelTheme.stylePrimary(btn);
        btn.addActionListener(e -> send(txt));
        txt.addActionListener(e -> send(txt));

        sendBar.add(txt, BorderLayout.CENTER);
        sendBar.add(btn, BorderLayout.EAST);
        root.add(sendBar, BorderLayout.SOUTH);

        refreshMessages();
    }

    private void send(JTextField txt) {
        String m = txt.getText();
        if (m == null || m.isEmpty())
            return;
        try {
            ICliente iface = cliente.getInterfaz(amigo);
            if (iface == null) {
                JOptionPane.showMessageDialog(this, amigo + " no está conectado");
                return;
            }
            cliente.enviar(iface, m);
            txt.setText("");
            refreshMessages();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo enviar: " + ex.getMessage());
        }
    }

    private void startBackgroundRefresh() {
        Thread t = new Thread(() -> {
            while (running) {
                synchronized (cliente) {
                    try {
                        cliente.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
                SwingUtilities.invokeLater(this::refreshMessages);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void refreshMessages() {
        modelMensajes.clear();
        ArrayList<String> msgs = cliente.getMensajes(amigo);
        for (String s : msgs)
            modelMensajes.addElement(s);
    }
}

// ============== Ventana Cambiar Contraseña ===================
class ChangePasswordWindow extends JFrame {
    private final MainWindow parent;
    private final Cliente cliente;

    ChangePasswordWindow(MainWindow parent, Cliente cliente) {
        super("Cambiar contraseña");
        this.parent = parent;
        this.cliente = cliente;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                parent.returnFromChild();
            }
        });

        setSize(520, 340);
        setLocationRelativeTo(parent);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.setBackground(PastelTheme.BG_LIGHT);
        setContentPane(root);

        JPanel card = PastelTheme.cardPanel();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JPasswordField txtOld = new JPasswordField();
        JPasswordField txtNew = new JPasswordField();
        JPasswordField txtNew2 = new JPasswordField();

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Contraseña antigua"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        form.add(txtOld, gbc);

        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Nueva contraseña"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        form.add(txtNew, gbc);

        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Repite la nueva"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        form.add(txtNew2, gbc);

        card.add(form, BorderLayout.CENTER);

        JButton btnSave = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");
        PastelTheme.stylePrimary(btnSave);
        PastelTheme.styleSecondary(btnCancel);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            String oldP = new String(txtOld.getPassword());
            String newP = new String(txtNew.getPassword());
            String new2 = new String(txtNew2.getPassword());

            if (oldP.isEmpty() || newP.isEmpty() || new2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Rellena todos los campos.");
                return;
            }
            if (!newP.equals(new2)) {
                JOptionPane.showMessageDialog(this, "La confirmación no coincide.");
                return;
            }
            if (oldP.equals(newP)) {
                JOptionPane.showMessageDialog(
                        this,
                        "La nueva contraseña no puede ser igual a la anterior.",
                        "Cambiar contraseña",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean ok = cliente.cambiarClave(oldP, newP);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Contraseña cambiada con éxito.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cambiar. Comprueba la contraseña antigua.");
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(btnCancel);
        actions.add(btnSave);
        card.add(actions, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
    }
}
