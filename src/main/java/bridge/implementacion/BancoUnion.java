package bridge.implementacion;

/** Implementador Concreto (GoF): comisión fija de 3 Bs, sin importar el monto. */
public class BancoUnion implements Banco {
    private static final String PIN_PRUEBA = "1234";
    private static final int COMISION_FIJA = 3;
    private int saldo = 1500;

    public String getNombre() { return "Banco Unión"; }
    public boolean autenticar(String pin) { return PIN_PRUEBA.equals(pin); }
    public int getSaldo() { return saldo; }

    public boolean debitar(int monto) {
        if (monto > saldo) return false;
        saldo -= monto;
        return true;
    }

    public void acreditar(int monto) { saldo += monto; }
    public int getComision(int monto) { return COMISION_FIJA; }
    public String describirReglas() { return "Comisión fija: " + COMISION_FIJA + " Bs"; }
}
