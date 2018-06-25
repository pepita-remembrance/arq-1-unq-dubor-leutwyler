# Roadmap del alumno

En principio la carga esperada para la aplicación es la de los alumnos respondiendo las encuestas. Así que le roadmap tiene que ser lo que nosotros esperamos que haga un alumno a la hora de llenar la encuesta.
Los pasos normales a seguir serían:

* POST /login
  * Autenticarse: obtener el token para poder realizar todas las demás operaciones
* GET /students/:filenumber
  * Obtener la información inicial para saber cuales son las encuestas abiertas.
* GET /careers/:career_short_name/polls/:poll_key
  * Obtener las preguntas e items de la poll a responder
* GET /students/:filenumber/careers/:career_short_name/polls/poll-result
  * Obtener la respuesta actual (404 en caso de que no haya respondido nunca)
* POST /students/:filenumber/careers/:career_short_name/polls/poll-result body: {} (caso de 404 en la request anterior)
  * Crear la respuesta
* PATCH /students/:filenumber/careers/:career_short_name/polls/poll-result
  * Enviar las respuestas
* POST /logout
  * Cerrar sesión

# Container de docker

* App
  * 3 cores
  * 2gb de memoria

* Base de datos
  * 1 core
  * 2gb de memoria

# Performance tests

Como en nuestra aplicación se arman unas respuestas basadas en cada alumno en particular, a diferencia de las encuestas tradicionales nosotros asumimos que los alumnos responderán solo un subset de los items (ya que con suerte la mayoría ya estarán respondidas como ellos lo hubieran hecho). Una cosa que no sabemos en cuanto a la usabilidad es cuantas veces haran un update a sus antiguas respuestas, pero asumimos que será entre 0 y 4 veces como una suposición ya que no tenemos datos reales para hacer un análisis más inteligente.

En cuanto a cuántas materias responderá el alumno, elegimos una distribución gaussiana con μ=3, σ=1, basándonos en que la mayoría de los alumnos se anota en 3 materias por cuatrimestre, con varianza 1. Teniendo casos más raros de gente que se anota en 5 y gente que se anota en 1.

## Resultados

La siguiente imagen representa los resultados de 500 usuarios siguiendo el roadmap previamente explicado, durante aproximadamente 15 minutos.

![performance](https://user-images.githubusercontent.com/12850723/41824904-6fb6eeae-77ee-11e8-9146-3d90ed6b5745.png)

Como se puede observar, los tiempos de respuesta estan alrededor de los 300ms. Y el gap entre el response time y el tiempo de la jvm + el tiempo de postgres nos muestra que hay una pequeña competencia entre los threads para despachar las respuestas ya procesadas. Esto nos indica que esta carga es la mayor que el container puede soportar sin empezar a degradar la performance.

# Stress tests

En este caso basandonos en los tests de performance, simplemente aumentamos la cantidad de usuarios over time hasta que la aplicación no pudo más.

## Resultados

Una de las conclusiones que se pueden sacar de la imagen es que el limite del container es ~4.5k rpm.
![stress](https://user-images.githubusercontent.com/12850723/41825023-594d8914-77f0-11e8-84ab-8790cfd7b2d4.png)
