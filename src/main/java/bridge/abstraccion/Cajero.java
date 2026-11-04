package bridge.abstraccion;

import bridge.implementacion.Banco;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Abstracción (GoF): mantiene una referencia al Implementador (Banco) y
 * delega en él toda la operación real. No sabe qué banco concreto es:
 * no hay ningún if por tipo de banco, solo llamadas a la interfaz Banco.
 */
public class Cajero {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    protected Banco banco;
    protected boolean sesionActiva = false;
    protected String ticket = "Inserte su tarjeta\ne ingrese su PIN.";

    public Cajero(Banco banco) {
        this.banco = banco;
    }

    /** Cambiar de tarjeta = cambiar el puente en tiempo de ejecución. */
    public void setBanco(Banco banco) {
        this.banco = banco;
        sesionActiva = false;
        ticket = "Tarjeta " + banco.getNombre() + "\nIngrese su PIN.";
    }

    public Banco getBanco() { return banco; }
    public boolean haySesion() { return sesionActiva; }
    public String getTicket() { return ticket; }

    public boolean ingresarPin(String pin) {
        sesionActiva = banco.autenticar(pin);
        ticket = sesionActiva
                ? "PIN correcto.\nBienvenido a " + banco.getNombre() + "."
                : "PIN incorrecto.\nIntente de nuevo.";
        return sesionActiva;
    }

    public void cerrarSesion() {
        sesionActiva = false;
        ticket = "Retire su tarjeta.\nGracias por usar la red.";
    }

    public boolean consultarSaldo() {
        if (!sesionActiva) { rechazar("Ingrese su PIN primero."); return false; }
        emitirTicket("CONSULTA", 0, 0);
        return true;
    }

    public boolean retirar(int monto) {
        if (!validar(monto)) return false;
        int comision = banco.getComision();
        if (!banco.debitar(monto + comision)) { rechazar("Saldo insuficiente."); return false; }
        emitirTicket("RETIRO", monto, comision);
        return true;
    }

    public boolean depositar(int monto) {
        if (!validar(monto)) return false;
        banco.acreditar(monto);
        emitirTicket("DEPÓSITO", monto, 0);
        return true;
    }

    protected boolean validar(int monto) {
        if (!sesionActiva) { rechazar("Ingrese su PIN primero."); return false; }
        if (monto <= 0) { rechazar("Monto inválido."); return false; }
        return true;
    }

    protected void rechazar(String motivo) {
        ticket = "OPERACIÓN RECHAZADA\n" + motivo;
    }

    protected void emitirTicket(String operacion, int monto, int comision) {
        ticket = String.join("\n",
                "   CAJERO RED BRIDGE",
                LocalDateTime.now().format(FECHA),
                "----------------------",
                "Banco:     " + banco.getNombre(),
                "Operación: " + operacion,
                "Monto:     " + monto + " Bs",
                "Comisión:  " + comision + " Bs",
                "----------------------",
                "Saldo:     " + banco.getSaldo() + " Bs");
    }
}
