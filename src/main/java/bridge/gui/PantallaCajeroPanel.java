package bridge.gui;

import bridge.abstraccion.Cajero;
import bridge.abstraccion.CajeroAvanzado;
import bridge.implementacion.Banco;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * No forma parte del patrón: solo dibuja el cajero (pantalla con el estado
 * del banco actual) y el ticket impreso de la última operación.
 */
public class PantallaCajeroPanel extends JPanel {
    private static final Color CUERPO = new Color(60, 68, 82);
    private static final Color PANTALLA_ON = new Color(46, 125, 122);
    private static final Color PANTALLA_OFF = new Color(52, 73, 104);
    private static final Color RANURA = new Color(30, 34, 42);
    private static final Color ACENTO = new Color(240, 173, 78);
    private static final Color TEXTO = Color.WHITE;
    private static final Color PAPEL = new Color(253, 252, 247);
    private static final Color SOMBRA = new Color(0, 0, 0, 40);
    private static final Color TINTA = new Color(40, 40, 40);

    private Cajero cajero;

    public PantallaCajeroPanel(Cajero cajeroInicial) {
        this.cajero = cajeroInicial;
        setPreferredSize(new Dimension(600, 340));
        setOpaque(false);
    }

    public void setCajero(Cajero cajero) {
        this.cajero = cajero;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth(), alto = getHeight();
        int anchoCajero = (int) (ancho * 0.55);
        dibujarCajero(g2, 10, 10, anchoCajero - 20, alto - 20);
        dibujarTicket(g2, anchoCajero + 10, 10, ancho - anchoCajero - 20, alto - 20);
    }

    private void dibujarCajero(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(CUERPO);
        g2.fill(new RoundRectangle2D.Double(x, y, w, h, 30, 30));

        Banco banco = cajero.getBanco();
        boolean sesion = cajero.haySesion();

        Rectangle pantalla = new Rectangle(x + 25, y + 25, w - 50, h - 120);
        g2.setColor(sesion ? PANTALLA_ON : PANTALLA_OFF);
        g2.fill(new RoundRectangle2D.Double(pantalla.x, pantalla.y, pantalla.width, pantalla.height, 18, 18));

        g2.setColor(TEXTO);
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        centrarTexto(g2, banco.getNombre(), pantalla.x, pantalla.y + 32, pantalla.width);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        centrarTexto(g2, cajero.getClass().getSimpleName(), pantalla.x, pantalla.y + 52, pantalla.width);

        int centroY = pantalla.y + pantalla.height / 2 + 10;
        if (sesion) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 30));
            centrarTexto(g2, "Saldo: " + banco.getSaldo() + " Bs", pantalla.x, centroY, pantalla.width);
        } else {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
            centrarTexto(g2, "Ingrese su PIN", pantalla.x, centroY, pantalla.width);
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        int infoY = pantalla.y + pantalla.height - 38;
        centrarTexto(g2, "Comisión por operación: " + banco.getComision() + " Bs", pantalla.x, infoY, pantalla.width);
        if (cajero instanceof CajeroAvanzado avanzado) {
            g2.setColor(ACENTO);
            centrarTexto(g2, "Límite diario restante: " + avanzado.getLimiteRestante() + " Bs",
                    pantalla.x, infoY + 20, pantalla.width);
        }

        // Ranura de tarjeta y dispensador de billetes (decorativo).
        int baseY = y + h - 75;
        g2.setColor(RANURA);
        g2.fillRoundRect(x + 30, baseY, 90, 12, 6, 6);
        g2.fillRoundRect(x + w - 30 - (w / 2), baseY + 30, w / 2, 16, 6, 6);
        g2.setColor(sesion ? ACENTO : new Color(120, 124, 130));
        g2.fillRoundRect(x + 30 + 30, baseY + 4, 30, 4, 2, 2);
        g2.setColor(new Color(200, 204, 210));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("Tarjeta", x + 30, baseY + 30);
        g2.drawString("Billetes", x + w - 30 - (w / 2), baseY + 62);
    }

    private void dibujarTicket(Graphics2D g2, int x, int y, int w, int h) {
        String[] lineas = cajero.getTicket().split("\n");
        int altoLinea = 19;
        int altoPapel = Math.min(h, Math.max(120, lineas.length * altoLinea + 40));

        g2.setColor(SOMBRA);
        g2.fillRect(x + 4, y + 4, w, altoPapel);
        g2.setColor(PAPEL);
        g2.fillRect(x, y, w, altoPapel);

        // Borde inferior dentado, como papel térmico cortado.
        g2.setColor(getParent() != null ? getParent().getBackground() : Color.WHITE);
        for (int dx = 0; dx < w; dx += 10) {
            g2.fillPolygon(new int[]{x + dx, x + dx + 5, x + dx + 10},
                    new int[]{y + altoPapel, y + altoPapel - 5, y + altoPapel}, 3);
        }

        g2.setColor(TINTA);
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        int ty = y + 28;
        for (String linea : lineas) {
            g2.drawString(linea, x + 14, ty);
            ty += altoLinea;
        }
    }

    private void centrarTexto(Graphics2D g2, String texto, int x, int y, int ancho) {
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (ancho - fm.stringWidth(texto)) / 2;
        g2.drawString(texto, tx, y);
    }
}
