package ar.edu.unq.arqsoft.database

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.services.{CareerService, StudentService}
import org.joda.time.DateTimeZone
import org.squeryl.SessionFactory

@Singleton
class Database @Inject()(connector: DBConnector) extends DemoDatabase with SeedData {

  DateTimeZone.setDefault(DateTimeZone.forOffsetHours(-3)) // Buenos Aires
  SessionFactory.concreteFactory = connector.sessionCreator
  init()
}

trait DemoDatabase {
  def init(): Unit = DSLFlavor.inTransaction {
    InscriptionPollSchema.drop
    InscriptionPollSchema.create
  }
}

trait SeedData {
  @Inject
  var studentService: StudentService = _
  @Inject
  var careerService: CareerService = _

  def seed(): Unit = {
    List(
      CreateStudentDTO(123, "marcogomez@gmail.com", "Marco", "Gomez"),
      CreateStudentDTO(456, "joaquinsanchez@gmail.com", "Joaquin", "Sanchez")
    ).foreach(studentService.create)
    List(
      CreateCareerDTO("TPI", "Tecnicatura Universitaria en Programacion Informatica", Some(List(
        // Basicas
        CreateSubjectDTO("InPr", "Introduccion a la Programacion"),
        CreateSubjectDTO("Orga", "Organizacion de Computadoras"),
        CreateSubjectDTO("Mate1", "Matematica I"),
        CreateSubjectDTO("Obj1", "Programacion con Objetos I"),
        CreateSubjectDTO("BD", "Bases de Datos"),
        CreateSubjectDTO("EstrD", "Estructuras de Datos"),
        CreateSubjectDTO("Obj2", "Programacion con Objetos II"),
        // Avanzadas
        CreateSubjectDTO("Redes", "Redes de Computadoras"),
        CreateSubjectDTO("SO", "Sistemas Operativos"),
        CreateSubjectDTO("PConc", "Programacion Concurrente"),
        CreateSubjectDTO("Mate2", "Matematica II"),
        CreateSubjectDTO("IngSoft", "Elementos de Ingenieria de Software"),
        CreateSubjectDTO("UIs", "Construccion de interfaces de Usuario"),
        CreateSubjectDTO("EPers", "Estrategias de Persistencia"),
        CreateSubjectDTO("PF", "Programacion Funcional"),
        CreateSubjectDTO("DesApp", "Desarrollo de Aplicaciones"),
        CreateSubjectDTO("LabSOR", "Laboratorio de Redes y Sistemas Operativos"),
        // Idioma
        CreateSubjectDTO("Ing1", "Ingles I"),
        CreateSubjectDTO("Ing2", "Ingles II"),
        // Opcionales
        CreateSubjectDTO("Seg", "Seguridad Informatica"),
        CreateSubjectDTO("BD2", "Bases de Datos II"),
        CreateSubjectDTO("ProyLib", "Participacion y Gestion en Proyectos de Software Libre"),
        CreateSubjectDTO("InArq", "Introduccion a las Arquitecturas de Software"),
        CreateSubjectDTO("Obj3", "Programacion con Objetos III"),
        CreateSubjectDTO("InBio", "Introduccion a la Bioinformatica"),
        CreateSubjectDTO("Politicas", "Politicas Publicas en la Sociedad de la Informacion y la Era Digital"),
        CreateSubjectDTO("Geo", "Sistemas de Informacion Geografica"),
        CreateSubjectDTO("Decl", "Herramientas Declarativas en Programacion"),
        CreateSubjectDTO("videojuegos", "Introduccion al Desarrollo de Videojuegos"),
        CreateSubjectDTO("DADC", "Derechos de Autor y Derecho de Copia en la Era Digital"),
        // Seminarios
        CreateSubjectDTO("CLP", "Seminario: Caracteristicas de Lenguajes de Programacion"),
        CreateSubjectDTO("SemMod", "Seminario: Taller de Desarrollo de Servicios Web/Cloud Modernos"),
        // TTI/TTU
        CreateSubjectDTO("TTI-TTU", "Seminario sobre Herramientas o Tecnicas Puntuales"),
        // Trabajo Final
        CreateSubjectDTO("TIP", "Taller de Trabajo de Insercion Profesional")
      )))
    ).foreach(careerService.create)
  }
}