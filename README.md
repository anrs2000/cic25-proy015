Proyecto de 010. 


-> Trivial
<- Sea V un espacio vectorial...

Ejemplo de como generar un container:

Tenemos el Dockerfile y ejecutamos:

- docker build -t nombre:tag .

Ejecutar un container:

- docker run nombre:tag

Tiene otro modificadores como -v (volúmenes) -p (puertos) -d (detached)

Ver cotainers en ejecución:

- docker ps

Ver containers parados:

- docker ps -a

Borrar container parado

- docker rm nombre(o hash)

Tenemos el Jenkinsfile que nos va a permitir construir todas las ramas en Jenkins (con publicación en Sonar) si generamos el job desde el ya existente: curso00-proyZZZ