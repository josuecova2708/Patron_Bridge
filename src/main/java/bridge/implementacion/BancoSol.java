package bridge.implementacion;

/** Implementador Concreto (GoF): cobra 5 Bs de comisión por operación. */
public class BancoSol implements Banco {
    private static final String PIN_PRUEBA = "1234";
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
    public int getComision() { return 5; }
}
