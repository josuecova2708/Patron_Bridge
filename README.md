# Patrón Bridge — Cajero automático multibanco

Demo Java/Swing del patrón de diseño **Bridge (GoF)** para exposición de
Arquitectura de Software. Un cajero de red atiende tarjetas de varios
bancos: la jerarquía de **cajeros** (Abstracción) está separada de la
jerarquía de **bancos** (Implementador), así que ambas pueden crecer de
forma independiente.

> Es una simulación: PIN de prueba fijo (`1234`), saldos en memoria y
> comisiones fijas. No hay base de datos ni seguridad real.

## Cómo ejecutar

**Con Maven** (si lo tienes instalado, o desde NetBeans/IntelliJ):

```
mvn -q compile exec:java
```

**Sin Maven** (usando solo `javac`/`java`):

```
run.bat        # Windows
./run.sh       # bash / Git Bash / WSL
```

Ambos scripts compilan todo `src/main/java` hacia `out/` y luego ejecutan
`bridge.Main`.

## Roles del patrón (GoF)

| Clase                                                  | Rol GoF                  |
|--------------------------------------------------------|--------------------------|
| `implementacion/Banco`                                 | Implementador (interfaz) |
| `implementacion/BancoUnion`, `BancoSol`, `BancoNacional` | Implementador Concreto |
| `abstraccion/Cajero`                                   | Abstracción              |
| `abstraccion/CajeroAvanzado`                           | Abstracción Refinada     |
| `gui/VentanaPrincipal`                                 | Cliente                  |
| `gui/PantallaCajeroPanel`                              | (solo dibujo, no es parte del patrón) |

El puente es el campo `protected Banco banco` dentro de `Cajero`. El cajero
nunca sabe qué banco tiene detrás: no hay ningún `if` por tipo de banco,
solo llamadas a la interfaz `Banco`.

| Banco          | Saldo inicial | Comisión por operación |
|----------------|---------------|------------------------|
| BancoUnion     | 1500 Bs       | 3 Bs                   |
| BancoSol       |  800 Bs       | 5 Bs                   |
| BancoNacional  | 3000 Bs       | 0 Bs                   |

`CajeroAvanzado` agrega un límite diario de retiro de 1000 Bs (atributo
privado `limiteDiario`), retiro rápido de 100 Bs y transferencias entre
bancos.

## Guion de demo (4 pasos)

1. **Retiro con BancoUnion.** Cajero "Básico", tarjeta "BancoUnion",
   PIN `1234` → Ingresar PIN. Retirar 100. El registro muestra
   `Cajero.retirar(100) → BancoUnion.debitar(103)`: el cajero pidió 100 y
   el banco aplicó su propia comisión de 3 Bs, que también aparece en el
   ticket.

2. **Cambiar de banco sin tocar el cajero.** Cambiar la tarjeta a
   "BancoNacional" (`setBanco(BancoNacional)`), ingresar el PIN y retirar
   100 otra vez: ahora se ve `debitar(100)`, comisión 0. Es el mismo
   `Cajero` con otro banco al otro lado del puente. BancoUnion conserva su
   saldo para cuando se vuelva a seleccionar.

3. **Cajero Avanzado.** Cambiar el cajero a "Avanzado" (aparecen "Retiro
   rápido 100" y "Transferir"). Retirar hasta pasar 1000 Bs en el día: el
   cajero rechaza la operación por límite diario sin llamar al banco.
   Luego transferir a otro banco:
   `CajeroAvanzado.transferir(200, BancoSol) → BancoNacional.debitar(200) + BancoSol.acreditar(200)`.
   Esto demuestra la Abstracción Refinada agregando comportamiento sin
   tocar ningún banco.

4. **Agregar un banco es solo una clase nueva.** Para sumar, por ejemplo,
   un `BancoFie`, basta con implementar la interfaz `Banco` y añadirlo al
   arreglo `bancos` de `VentanaPrincipal`. No cambia ni una línea de
   `Cajero` ni de `CajeroAvanzado`.
