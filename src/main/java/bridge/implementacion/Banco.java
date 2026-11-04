package bridge.implementacion;

/**
 * Implementador (GoF): operaciones que cualquier banco de la red debe
 * ofrecer al cajero, sin exponer cómo cada banco las procesa internamente.
 *
 * Los montos son enteros en Bs porque un cajero solo entrega billetes.
 */
public interface Banco {
    String getNombre();
    boolean autenticar(String pin);
    int getSaldo();
    boolean debitar(int monto);   // false si el saldo no alcanza
    void acreditar(int monto);
    int getComision();            // comisión fija por operación, en Bs
}
