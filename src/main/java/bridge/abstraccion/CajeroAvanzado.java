package bridge.abstraccion;

import bridge.implementacion.Banco;

/**
 * Abstracción Refinada (GoF): añade límite diario de retiro, retiro rápido
 * y transferencias entre bancos. Sigue delegando en la misma interfaz Banco
 * que Cajero, sin tocar ningún banco concreto.
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

    @Override
    public boolean retirar(int monto) {
        if (!validar(monto)) return false;
        if (retiradoHoy + monto > limiteDiario) {
            rechazar("Límite diario excedido.\nDisponible hoy: " + getLimiteRestante() + " Bs");
            return false;
        }
        boolean ok = super.retirar(monto);
        if (ok) retiradoHoy += monto;
        return ok;
    }

    public boolean retiroRapido() {
        return retirar(RETIRO_RAPIDO);
    }

    public int getRetiroRapido() { return RETIRO_RAPIDO; }

    /** Usa dos implementadores a la vez, ambos solo a través de la interfaz. */
    public boolean transferir(Banco destino, int monto) {
        if (!validar(monto)) return false;
        int comision = banco.getComision();
        if (!banco.debitar(monto + comision)) { rechazar("Saldo insuficiente."); return false; }
        destino.acreditar(monto);
        emitirTicket("TRANSFERENCIA", monto, comision);
        ticket += "\nDestino:   " + destino.getNombre();
        return true;
    }

    public int getLimiteRestante() { return limiteDiario - retiradoHoy; }
}
