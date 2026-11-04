package bridge.gui;

import bridge.abstraccion.Cajero;
import bridge.abstraccion.CajeroAvanzado;
import bridge.implementacion.Banco;
import bridge.implementacion.BancoNacional;
import bridge.implementacion.BancoSol;
import bridge.implementacion.BancoUnion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Cliente (GoF): usa el Cajero solo a través de la Abstracción, sin
 * conocer qué Banco concreto hay detrás del puente.
 */
public class VentanaPrincipal extends JFrame {
    private static final Color FONDO = new Color(245, 246, 248);
    private static final Color PANEL = new Color(255, 255, 255);
    private static final Font FUENTE = new Font("SansSerif", Font.PLAIN, 16);

    private final Banco[] bancos = {new BancoUnion(), new BancoSol(), new BancoNacional()};

    private Banco bancoActual = bancos[0];
    private Cajero cajero = new Cajero(bancoActual);

    private final PantallaCajeroPanel pantalla = new PantallaCajeroPanel(cajero);
    private final JTextArea log = new JTextArea();
    private final JPasswordField campoPin = new JPasswordField(6);
    private final JSpinner spinnerMonto = new JSpinner(new SpinnerNumberModel(100, 10, 5000, 10));
    private final JButton botonRetiroRapido = new JButton("Retiro rápido 100");
    private final JButton botonTransferir = new JButton("Transferir");

    public VentanaPrincipal() {
        super("Patrón Bridge — Cajero automático multibanco");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(FONDO);
        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(14, 14, 14, 14));

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelBotones(), BorderLayout.WEST);
        add(crearPanelCajero(), BorderLayout.CENTER);
        add(crearPanelLog(), BorderLayout.SOUTH);

        setMinimumSize(new Dimension(980, 700));
        pack();
        setLocationRelativeTo(null);
    }

    // ---------- Construcción de la interfaz ----------

    private JPanel crearPanelSuperior() {
        JComboBox<String> comboCajero = new JComboBox<>(new String[]{"Básico", "Avanzado"});
        JComboBox<String> comboTarjeta = new JComboBox<>(
                Arrays.stream(bancos).map(VentanaPrincipal::clase).toArray(String[]::new));
        comboCajero.setFont(FUENTE);
        comboTarjeta.setFont(FUENTE);
        campoPin.setFont(FUENTE);
        spinnerMonto.setFont(FUENTE);

        JButton botonIngresar = new JButton("Ingresar PIN");
        botonIngresar.setFont(FUENTE);
        botonIngresar.setFocusPainted(false);
        botonIngresar.addActionListener(e -> ingresarPin());
        campoPin.addActionListener(e -> ingresarPin());

        JPanel fila1 = fila();
        fila1.add(etiqueta("Cajero:"));
        fila1.add(comboCajero);
        fila1.add(etiqueta("Tarjeta:"));
        fila1.add(comboTarjeta);

        JPanel fila2 = fila();
        fila2.add(etiqueta("PIN (prueba: 1234):"));
        fila2.add(campoPin);
        fila2.add(botonIngresar);
        fila2.add(etiqueta("Monto (Bs):"));
        fila2.add(spinnerMonto);

        comboCajero.addActionListener(e -> cambiarTipoCajero((String) comboCajero.getSelectedItem()));
        comboTarjeta.addActionListener(e -> cambiarBanco(bancos[comboTarjeta.getSelectedIndex()]));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(FONDO);
        panel.add(fila1);
        panel.add(fila2);
        return panel;
    }

    private JPanel fila() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        p.setBackground(FONDO);
        return p;
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE);
        return l;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setLayout(new GridLayout(0, 1, 10, 12));

        panel.add(botonAccion("Consultar saldo", () -> {
            boolean ok = cajero.consultarSaldo();
            registrar("consultarSaldo()", ok ? clase(bancoActual) + ".getSaldo() = " + bancoActual.getSaldo() : null);
        }));
        panel.add(botonAccion("Retirar", () -> {
            int monto = leerMonto();
            boolean ok = cajero.retirar(monto);
            registrar("retirar(" + monto + ")", ok ? debito(monto) : null);
        }));
        panel.add(botonAccion("Depositar", () -> {
            int monto = leerMonto();
            boolean ok = cajero.depositar(monto);
            registrar("depositar(" + monto + ")", ok ? clase(bancoActual) + ".acreditar(" + monto + ")" : null);
        }));

        configurarBoton(botonRetiroRapido, () -> {
            CajeroAvanzado avanzado = (CajeroAvanzado) cajero;
            boolean ok = avanzado.retiroRapido();
            registrar("retiroRapido()", ok ? debito(avanzado.getRetiroRapido()) : null);
        });
        configurarBoton(botonTransferir, this::transferir);
        botonRetiroRapido.setVisible(false);
        botonTransferir.setVisible(false);
        panel.add(botonRetiroRapido);
        panel.add(botonTransferir);

        panel.add(botonAccion("Retirar tarjeta", () -> {
            cajero.cerrarSesion();
            registrar("cerrarSesion()", "sesión finalizada");
        }));

        return panel;
    }

    private JButton botonAccion(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        configurarBoton(boton, accion);
        return boton;
    }

    private void configurarBoton(JButton boton, Runnable accion) {
        boton.setFont(FUENTE);
        boton.setFocusPainted(false);
        boton.addActionListener(e -> { accion.run(); pantalla.repaint(); });
    }

    private JPanel crearPanelCajero() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(PANEL);
        contenedor.setBorder(new EmptyBorder(20, 20, 20, 20));
        contenedor.add(pantalla, BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel crearPanelLog() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(FONDO);
        panel.setPreferredSize(new Dimension(0, 170));

        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        log.setBackground(PANEL);
        panel.add(new JScrollPane(log), BorderLayout.CENTER);

        JButton limpiar = new JButton("Limpiar registro");
        limpiar.setFont(FUENTE);
        limpiar.addActionListener(e -> log.setText(""));
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.setBackground(FONDO);
        sur.add(limpiar);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    // ---------- Acciones ----------

    private void ingresarPin() {
        boolean ok = cajero.ingresarPin(new String(campoPin.getPassword()));
        campoPin.setText("");
        registrar("ingresarPin(****)", clase(bancoActual) + ".autenticar() = " + ok);
        pantalla.repaint();
    }

    private void transferir() {
        Banco[] destinos = Arrays.stream(bancos).filter(b -> b != bancoActual).toArray(Banco[]::new);
        String[] nombres = Arrays.stream(destinos).map(Banco::getNombre).toArray(String[]::new);
        Object eleccion = JOptionPane.showInputDialog(this, "Banco destino:", "Transferir",
                JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);
        if (eleccion == null) return;
        Banco destino = destinos[Arrays.asList(nombres).indexOf(eleccion)];

        int monto = leerMonto();
        boolean ok = ((CajeroAvanzado) cajero).transferir(destino, monto);
        registrar("transferir(" + monto + ", " + clase(destino) + ")",
                ok ? debito(monto) + " + " + clase(destino) + ".acreditar(" + monto + ")" : null);
    }

    private void cambiarBanco(Banco nuevo) {
        bancoActual = nuevo;
        cajero.setBanco(nuevo);
        registrar("setBanco(" + clase(nuevo) + ")", "puente cambiado en tiempo de ejecución");
    }

    private void cambiarTipoCajero(String tipo) {
        boolean avanzado = "Avanzado".equals(tipo);
        cajero = avanzado ? new CajeroAvanzado(bancoActual) : new Cajero(bancoActual);
        pantalla.setCajero(cajero);
        botonRetiroRapido.setVisible(avanzado);
        botonTransferir.setVisible(avanzado);
        registrarLinea("nuevo " + cajero.getClass().getSimpleName() + "(" + clase(bancoActual)
                + ")  →  mismo puente, otra abstracción");
    }

    // ---------- Utilidades ----------

    private int leerMonto() {
        try {
            spinnerMonto.commitEdit();
        } catch (ParseException ignored) {
            // Si el texto no es válido, se usa el último valor aceptado.
        }
        return (Integer) spinnerMonto.getValue();
    }

    /** Texto del débito real que recibe el banco: monto + su comisión. */
    private String debito(int monto) {
        return clase(bancoActual) + ".debitar(" + (monto + bancoActual.getComision(monto)) + ")";
    }

    private static String clase(Banco banco) {
        return banco.getClass().getSimpleName();
    }

    /** llamadaBanco == null significa que el cajero rechazó la operación. */
    private void registrar(String metodoCajero, String llamadaBanco) {
        String clase = cajero.getClass().getSimpleName();
        String destino = llamadaBanco != null ? llamadaBanco : "rechazado (ver ticket)";
        registrarLinea(clase + "." + metodoCajero + "  →  " + destino);
    }

    private void registrarLinea(String linea) {
        log.append(linea + "\n");
        log.setCaretPosition(log.getDocument().getLength());
        pantalla.repaint();
    }
}
