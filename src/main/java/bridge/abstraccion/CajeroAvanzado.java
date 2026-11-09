package bridge.abstraccion;

import bridge.implementacion.Banco;

/**
 * Abstracción Refinada (GoF): cajero con límite diario de retiro, retiro
 * rápido y transferencias entre bancos. Implementa las mismas operaciones
 * que CajeroBasico a su manera, llamando directamente a la interfaz Banco.
 */
public class CajeroAvanzado extends Cajero {
    private static final int RETIRO_RAPIDO = 100;

    private final int limiteDiario = 1000;
    private int retiradoHoy = 0;

    public CajeroAvanzado(Banco banco) {
        super(banco);
    }

    /** Otra tarjeta, otro cliente: su límite empieza de cero. */
    @Override
    public void setBanco(Banco banco) {
        super.setBanco(banco);
        retiradoHoy = 0;
    }

    // usa banco.getSaldo() y además muestra el límite restante
    @Override
    public boolean consultarSaldo() {
        if (!sesionActiva) { rechazar("Ingrese su PIN primero."); return false; }
        emitirTicket("CONSULTA", 0, 0);
        ticket += "\nLímite hoy: " + getLimiteRestante() + " Bs";
        return true;
    }

    // verifica el límite diario; luego usa banco.getComision() y banco.debitar()
    @Override
    public boolean retirar(int monto) {
        if (!validar(monto)) return false;
        if (retiradoHoy + monto > limiteDiario) {
            rechazar("Límite diario excedido.\nDisponible hoy: " + getLimiteRestante() + " Bs");
            return false;
        }
        int comision = banco.getComision(monto);
        if (!banco.debitar(monto + comision)) { rechazar("Fondos insuficientes."); return false; }
        retiradoHoy += monto;
        emitirTicket("RETIRO", monto, comision);
        ticket += "\nLímite hoy: " + getLimiteRestante() + " Bs";
        return true;
    }

    // usa banco.acreditar()
    @Override
    public boolean depositar(int monto) {
        if (!validar(monto)) return false;
        banco.acreditar(monto);
        emitirTicket("DEPÓSITO", monto, 0);
        return true;
    }

    // usa retirar() con un monto fijo
    public boolean retiroRapido() {
        return retirar(RETIRO_RAPIDO);
    }

    public int getRetiroRapido() { return RETIRO_RAPIDO; }

    // usa banco.getComision() y banco.debitar() en el origen, y destino.acreditar()
    public boolean transferir(Banco destino, int monto) {
        if (!validar(monto)) return false;
        int comision = banco.getComision(monto);
        if (!banco.debitar(monto + comision)) { rechazar("Fondos insuficientes."); return false; }
        destino.acreditar(monto);
        emitirTicket("TRANSFERENCIA", monto, comision);
        ticket += "\nDestino:   " + destino.getNombre();
        return true;
    }

    public int getLimiteRestante() { return limiteDiario - retiradoHoy; }
}
