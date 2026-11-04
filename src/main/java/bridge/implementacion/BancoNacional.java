package bridge.implementacion;

/** Implementador Concreto (GoF): sin comisión, pero permite sobregiro hasta 200 Bs. */
public class BancoNacional implements Banco {
    private static final String PIN_PRUEBA = "1234";
    private static final int LIMITE_SOBREGIRO = 200;
    private int saldo = 3000;

    public String getNombre() { return "Banco Nacional"; }
    public boolean autenticar(String pin) { return PIN_PRUEBA.equals(pin); }
    public int getSaldo() { return saldo; }

    public boolean debitar(int monto) {
        if (saldo - monto < -LIMITE_SOBREGIRO) return false;
        saldo -= monto;
        return true;
    }

    public void acreditar(int monto) { saldo += monto; }
    public int getComision(int monto) { return 0; }
    public String describirReglas() { return "Sin comisión · sobregiro hasta " + LIMITE_SOBREGIRO + " Bs"; }
}
