# TPE2: Alquiler de Bicicletas

Trabajo práctico especial de Programación de Objetos Distribuidos en el que se diseña e implementa una aplicación de consola que utilice el modelo de programación MapReduce junto con el framework HazelCast para el procesamiento de alquileres de bicicletas, basado en datos reales.


## Autores
- [Tomas Alvarez Escalante](https://github.com/tomalvarezz)
- [Lucas Agustin Ferreiro](https://github.com/lukyferreiro)
- [Roman Gomez Kiss](https://github.com/rgomezkiss)

## Compilación

Para compilar el proyecto, se debe ejecutar el siguiente comando en la raíz del proyecto:

```bash
mvn clean install
```

Tras esto, en las carpetas /client/target y /server/target se generarán tp1-g5-client-1.0-SNAPSHOT-bin.tar.gz y tp1-g5-server-1.0-SNAPSHOT-bin.tar.gz
Dichos .tar.gz, los cuales se deberán descomprimir utilizando el siguiente comando:

```bash
tar -xzvf <nombre-del-archivo>.tar.gz
```

## Ejecución

Tras haber realizado lo anterior, podremos ejecutar el siguiente servicio y los clientes. Asegurarse de otorgar permisos de ejecución a los ejecutables.

