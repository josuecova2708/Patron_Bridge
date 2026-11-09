# Patrón Bridge — Cajero automático multibanco

Demo Java/Swing del patrón de diseño **Bridge (GoF)** para exposición de
Arquitectura de Software. Un cajero de red atiende tarjetas de varios
bancos: la jerarquía de **cajeros** (Abstracción) está separada de la
jerarquía de **bancos** (Implementador), así que ambas pueden crecer de
forma independiente.

> Es una simulación: PIN de prueba fijo (`1234`) y saldos en memoria.
> No hay base de datos ni seguridad real.

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
| `abstraccion/Cajero`                                   | Abstracción (clase abstracta) |
| `abstraccion/CajeroBasico`, `CajeroAvanzado`           | Abstracción Refinada     |
| `gui/VentanaPrincipal`                                 | Cliente                  |
| `gui/PantallaCajeroPanel`                              | (solo dibujo, no es parte del patrón) |

Equivalencia con el ejemplo de clase: `Puente` (abstracto) → `Cajero`;
`PuenteArco` / `PuenteColgante` → `CajeroBasico` / `CajeroAvanzado`;
`Material` → `Banco`; `Acero` / `Madera` / `Piedra` → `BancoUnion` /
`BancoSol` / `BancoNacional`.

El puente es el campo `protected Banco banco` dentro de `Cajero`. `Cajero`
declara `consultarSaldo()`, `retirar()` y `depositar()` como abstractos, y
cada cajero concreto los implementa llamando a los métodos del banco, igual
que `PuenteArco.construirPuente()` llama a `material.construir()`. Ningún
cajero sabe qué banco tiene detrás: no hay ningún `if` por tipo de banco,
solo llamadas a la interfaz `Banco`.

### Qué métodos del banco usa cada operación

| Operación del cajero      | CajeroBasico             | CajeroAvanzado                                   |
|---------------------------|--------------------------|--------------------------------------------------|
| `ingresarPin` (en Cajero) | `autenticar`             | `autenticar`                                     |
| `consultarSaldo`          | `getSaldo`               | `getSaldo` (+ muestra el límite restante)        |
| `retirar`                 | `getComision`, `debitar` | verifica el límite diario, luego `getComision`, `debitar` |
| `depositar`               | `acreditar`              | `acreditar`                                      |
| `retiroRapido`            | —                        | `retirar(100)`                                   |
| `transferir`              | —                        | `getComision`, `debitar` y `destino.acreditar`   |

Cada banco implementa el mismo contrato **con su propio comportamiento**,
no solo con otros valores:

| Banco          | Saldo inicial | Regla propia                                   | `retirar(500)` debita |
|----------------|---------------|------------------------------------------------|-----------------------|
| BancoUnion     | 1500 Bs       | Comisión fija de 3 Bs                          | 503                   |
| BancoSol       |  800 Bs       | Comisión del 1 % del monto, mínimo 2 Bs        | 505                   |
| BancoNacional  | 3000 Bs       | Sin comisión, pero permite sobregiro hasta 200 Bs | 500                |

El cajero llama siempre igual (`banco.getComision(monto)` y
`banco.debitar(monto + comision)`); cada banco decide cuánto cobra y si
autoriza el débito.

`CajeroAvanzado` agrega un límite diario de retiro de 1000 Bs (atributo
privado `limiteDiario`), retiro rápido de 100 Bs y transferencias entre
bancos.

## Guion de demo (4 pasos)

1. **Retiro con BancoUnion.** Cajero "Básico", tarjeta "BancoUnion",
   PIN `1234` → Ingresar PIN. Monto 500 → Retirar. El registro muestra
   `CajeroBasico.retirar(500) → BancoUnion.debitar(503)`: el cajero pidió 500 y
   el banco aplicó su comisión fija de 3 Bs, que también aparece en el
   ticket.

2. **Mismo cajero, otro banco, otro comportamiento.** Cambiar la tarjeta
   a "BancoSol" (`setBanco(BancoSol)`), ingresar el PIN y retirar 500:
   ahora se ve `debitar(505)` (1 % del monto). Cambiar a "BancoNacional"
   y retirar 500 (`debitar(500)`, sin comisión); luego retirar 2600: el
   saldo queda en −100 Bs porque este banco permite sobregiro. El mismo
   retiro en BancoSol sería rechazado. El cajero no sabe nada de estas
   reglas: cada banco implementa el contrato a su manera. Además, cada
   banco conserva su saldo al volver a seleccionarlo.

3. **Cajero Avanzado.** Cambiar el cajero a "Avanzado" (aparecen "Retiro
   rápido 100" y "Transferir"). Retirar hasta pasar 1000 Bs en el día: el
   cajero rechaza la operación por límite diario sin llamar al banco.
   Luego transferir a otro banco:
   `CajeroAvanzado.transferir(200, BancoSol) → BancoNacional.debitar(200) + BancoSol.acreditar(200)`.
   Esto demuestra la Abstracción Refinada agregando comportamiento sin
   tocar ningún banco.

4. **Las dos jerarquías crecen por separado.** Para sumar un banco, por
   ejemplo `BancoFie`, basta con implementar la interfaz `Banco` y añadirlo
   al arreglo `bancos` de `VentanaPrincipal`, sin tocar ningún cajero. Y
   para sumar un tipo de cajero (por ejemplo, un `CajeroDepositario` que
   solo recibe depósitos) basta con extender `Cajero`, sin tocar ningún
   banco.
