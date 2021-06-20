# proyecto-CRUD
aplicación web crud, springboot, Thymeleaf mas JPA

Esta aplicación fue desarrollada con Spring Tool Suite 4 en la versión de SpringFramework 2.4.5, y Java 11

 En este proyecto se creo una aplicación web “CRUD” Crear, Leer, Actualizar y Borrar para una lista de clientes, donde además se puede ver el detalle de cada cliente seleccionándolo desde la lista, en el detalle incluye un archivo de imagen que también se puede editar, eliminar o crear, según sea la necesidad. La aplicación también cuenta con una clase paginator que nos permite organizar la cantidad de clientes que se pueden observar por vista y a su vez nos permite movernos entre las paginas para hacer mucho mas amigable la para el usuario.

El proyecto se organizo en cinco paquetes principales 

Package controllers
clase ClienteController : es donde se crean las anotaciones propias de spring para el mapping HTTP los métodos handler. (@RequestMapping, @GetMapping)

Aquí implementamos los métodos handler  para: verFoto, ver, listar, crear, editar, guardar , eliminar.

Package models.dao
IclienteDao : creamos esta interface que extiende de PagingAndSortingRepository<T, Id>. Una interface propia de Spring que se extiende de CrudRepository.

Package models.entity
clase Cliente : anotamos esta clase con la anotación @Entity. Creamos el objeto cliente con cada uno de sus atributos (Id, nombre, apellido, email, fecha de creación, foto) que van mapeados a la tabla “clientes” a traves de la anotación @Table(name= “”), por tratarse de una clase de persistencia implementamos la interfaz serializable. En esta clase también implementamos las las anotaciones  @NotNull, @NotEmpty para sus atributos.
En el atributo “Id” utilizamos la anotación @Id y @GenerateValue (strategy = GenerationType.IDENTITY).

Package models.service
 creamos las diferentes clases @Service y las Interfaces 
clase ClienteServiceImpl
Interface IClienteService
UploadFileServiceImpl
IUploadFileService

package util.paginator
creamos las clases para la paginación de la app.
Clase PageRender
clase PageItem



![listar](https://user-images.githubusercontent.com/72769320/122682617-86ca4780-d1c8-11eb-95eb-14cceadc1ae5.png)
![ver](https://user-images.githubusercontent.com/72769320/122682983-6d29ff80-d1ca-11eb-9b95-7cb909b9519f.png)
![form](https://user-images.githubusercontent.com/72769320/122682992-77e49480-d1ca-11eb-807c-275fb5b9e8f7.png)
