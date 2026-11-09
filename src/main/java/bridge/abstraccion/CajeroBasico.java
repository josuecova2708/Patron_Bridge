package bridge.abstraccion;

import bridge.implementacion.Banco;

/**
 * Abstracción Refinada (GoF): cajero simple. Implementa cada operación
 * llamando directamente a los métodos del banco, sin reglas extra.
 */
public class CajeroBasico extends Cajero {

    public CajeroBasico(Banco banco) {
        super(banco);
    }

    // usa banco.getSaldo()
    @Override
    public boolean consultarSaldo() {
        if (!sesionActiva) { rechazar("Ingrese su PIN primero."); return false; }
        emitirTicket("CONSULTA", 0, 0);
        return true;
    }

    // usa banco.getComision() y banco.debitar()
    @Override
    public boolean retirar(int monto) {
        if (!validar(monto)) return false;
        int comision = banco.getComision(monto);
        if (!banco.debitar(monto + comision)) { rechazar("Fondos insuficientes."); return false; }
        emitirTicket("RETIRO", monto, comision);
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
}
