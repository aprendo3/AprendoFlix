[x] 1) al registrar peliculas con el campo "release date" ingresando un dato vacio se tiene:
Exception in thread "main" java.time.format.DateTimeParseException: Text '' could not be parsed at index 0
        at java.base/java.time.format.DateTimeFormatter.parseResolved0(DateTimeFormatter.java:2108)
        at java.base/java.time.format.DateTimeFormatter.parse(DateTimeFormatter.java:2010)
        at java.base/java.time.LocalDate.parse(LocalDate.java:435)
        at java.base/java.time.LocalDate.parse(LocalDate.java:420)
        at Main.addMovie(Main.java:365)
        at Main.showAdminMenu(Main.java:174)
        at Main.main(Main.java:33)
 Terminando inmediatamente con la applicacion.

[x] 2) Los usuarios pueden registrase con el mismo nombre varias veces si son diferentes letras mayusculas y minisculas

[X] 3) se pueden registrar usuarios con campos vacios para username y password

[x] 4) agregar peliculas con los campos vacios no debe ser permitido

[x] 5) se puede agregar Peliculas con la misma informacion, duplicados
 
[x] 6) Un usuario puede votar (Rating) varias veces por la misma pelicula
 
[ ] 7) La busqueda de peliculas parece no estar completa, solo lista las peliculas de las que se pueden seleccionar alguna para verla.

[ ] 8) Registrar peliculas con mucha informacion o campos muy largos puede deformar la aplaicion al momento de mostrarlos