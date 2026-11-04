package bridge.implementacion;

/** Implementador Concreto (GoF): comisión del 1 % del monto, mínimo 2 Bs. */
public class BancoSol implements Banco {
    private static final String PIN_PRUEBA = "1234";
    private static final int COMISION_MINIMA = 2;
    private int saldo = 800;

    public String getNombre() { return "Banco Sol"; }
    public boolean autenticar(String pin) { return PIN_PRUEBA.equals(pin); }
    public int getSaldo() { return saldo; }

    public boolean debitar(int monto) {
        if (monto > saldo) return false;
        saldo -= monto;
        return true;
    }

    public void acreditar(int monto) { saldo += monto; }
    public int getComision(int monto) { return Math.max(COMISION_MINIMA, monto / 100); }
    public String describirReglas() { return "Comisión: 1 % del monto (mín. " + COMISION_MINIMA + " Bs)"; }
}
