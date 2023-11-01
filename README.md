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

### Servicio
Para ejecutar el servicio se debe correr:

```
/run-server.sh
```

Si se ejecuta este comando en la misma terminal, se levantaran diferentes nodos. También se pueden realizar en Pampero.
Hay que tener en cuenta que se debe modificar en el código la IP "base" que escucha el servidor.

### Cliente

Todas las queries siempre tendrán en común los siguientes argumentos:

| Argumento      | Descripción                                                                         |
|----------------|-------------------------------------------------------------------------------------|
| `-Daddresses`  | Direcciones IP de los nodos con sus puertos (una o más, separadas por punto y coma) |
| `-DinPath`     | Path donde están los archivos de entrada bikes.csv y stations.csv                   |
| `-DoutPath`    | Path donde estarán los archivos de salida queryX.csv y timeX.txt                    |

#### Query 1

Devolverá el nombre de la estación A, el nombre de la estación B y la cantidad total de viajes iniciados en la estación A y finalizados en la estación B.

```sh
/query1 -Daddresses='xx.xx.xx.xx:XXXX;yy.yy.yy.yy:YYYY' -DinPath=XX -DoutPath=YY
```

#### Query 2

Devolverá el nombre de la estación y el promedio de la distancia aproximada (en km) del total de viajes iniciados por miembros en esa estación que finalizan en una estación distinta.

```sh
/query2 -Daddresses='xx.xx.xx.xx:XXXX;yy.yy.yy.yy:YYYY' -DinPath=XX -DoutPath=YY -Dn=N
```

- N es el top de las estaciones con mayor promedio

#### Query 3

Devolverá la estación de inicio del viaje, la estación de destino del viaje, la fecha y hora de inicio del viaje y la duración en minutos de ese viaje más largo.

```sh
/query3 -Daddresses='xx.xx.xx.xx:XXXX;yy.yy.yy.yy:YYYY' -DinPath=XX -DoutPath=YY
```

#### Query 4

Devolverá el nombre de la estación, la cantidad de días con afluencia neta positiva de esa estación, la cantidad de días con afluencia neta neutra de esa estación y la cantidad de días con afluencia neta negativa de esa estación, dentro de un rango de fechas que se indica por parámetro.

```sh
/query4 -Daddresses='xx.xx.xx.xx:XXXX;yy.yy.yy.yy:YYYY' -DinPath=XX -DoutPath=YY -DstartDate=DD/MM/YYYY -DendDate=DD/MM/YYYY
```

- startDate la fecha de inicio del rango en formato DD/MM/YYYY
- endDate la fecha de fin del rango en formato DD/MM/YYYY