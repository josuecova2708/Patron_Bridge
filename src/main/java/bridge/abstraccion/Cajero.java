package bridge.abstraccion;

import bridge.implementacion.Banco;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Abstracción (GoF): clase abstracta que mantiene la referencia al
 * Implementador (Banco). Declara las operaciones del cajero y deja que
 * cada cajero concreto las implemente llamando a los métodos del banco.
 * No sabe qué banco concreto es: solo conoce la interfaz Banco.
 */
public abstract class Cajero {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    protected Banco banco;   // el puente
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

    // usa banco.autenticar()
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

    // ---------- Operaciones que cada cajero implementa a su manera ----------

    public abstract boolean consultarSaldo();
    public abstract boolean retirar(int monto);
    public abstract boolean depositar(int monto);

    // ---------- Ayudas comunes para los cajeros concretos ----------

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
