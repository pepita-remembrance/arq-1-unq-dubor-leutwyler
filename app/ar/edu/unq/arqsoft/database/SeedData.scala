package ar.edu.unq.arqsoft.database

import com.google.inject.Inject

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.logging.Logging
import ar.edu.unq.arqsoft.model.Day.Day
import ar.edu.unq.arqsoft.services._

trait SeedData extends Logging {
  @Inject
  var studentService: StudentService = _
  @Inject
  var careerService: CareerService = _
  @Inject
  var pollService: PollService = _

  def seed(): Unit = {
    info("Seeding database...")
    import ar.edu.unq.arqsoft.model.Day.{Friday => Viernes, Monday => Lunes, Saturday => Sabado, Thursday => Jueves, Tuesday => Martes, Wednesday => Miercoles}
    info("Creating students...")
    List(
      CreateStudentDTO(123, "marcogomez@gmail.com", "Marco", "Gomez"),
      CreateStudentDTO(456, "joaquinsanchez@gmail.com", "Joaquin", "Sanchez")
    ).map(studentService.create)
    info("Creating careers...")
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
    info("Students join their careers")
    studentService.joinCareer(CreateStudentCareerDTO(123, "TPI"))
//    studentService.joinCareer(CreateStudentCareerDTO(456, "TPI"))
    info("Creating poll TPI 2017s2")
    pollService.create("TPI", CreatePollDTO("2017s2", Some(Map(
      // Basicas
      "InPr" -> List(
        CreateCourseDTO("C1", List(
          Lunes.from(9).to(11, 30),
          Jueves.from(9).to(11, 30),
          Miercoles.from(9).to(12)
        )),
        CreateCourseDTO("C2", List(
          Lunes.from(12).to(14, 30),
          Jueves.from(12).to(14, 30),
          Miercoles.from(9).to(12)
        )),
        CreateCourseDTO("C3", List(
          Lunes.from(16).to(18, 30),
          Jueves.from(16).to(18, 30),
          Miercoles.from(18).to(21)
        )),
        CreateCourseDTO("C4", List(
          Lunes.from(19).to(21, 30),
          Jueves.from(19).to(21, 30),
          Miercoles.from(18).to(21)
        ))
      ),
      "Orga" -> List(
        CreateCourseDTO("C1", List(
          Martes.from(9).to(12),
          Viernes.from(9).to(12)
        )),
        CreateCourseDTO("C2", List(
          Martes.from(8, 30).to(11, 30),
          Sabado.from(9).to(12)
        )),
        CreateCourseDTO("C3", List(
          Martes.from(16).to(19),
          Viernes.from(16).to(19)
        )),
        CreateCourseDTO("C4", List(
          Martes.from(19).to(22),
          Viernes.from(19).to(22)
        ))
      ),
      "Mate1" -> List(
        CreateCourseDTO("C1", List(
          Martes.from(9).to(13),
          Viernes.from(9).to(13)
        )),
        CreateCourseDTO("C2", List(
          Lunes.from(18).to(22),
          Miercoles.from(18).to(22)
        )),
        CreateCourseDTO("C3", List(
          Lunes.from(9).to(13),
          Miercoles.from(9).to(13)
        ))
      ),
      "Obj1" -> List(
        CreateCourseDTO("C1", List(
          Miercoles.from(15).to(19),
          Jueves.from(14).to(18)
        )),
        CreateCourseDTO("C2", List(
          Lunes.from(18).to(22),
          Miercoles.from(18).to(22)
        ))
      ),
      "BD" -> List(
        CreateCourseDTO("C1", List(
          Martes.from(9).to(12),
          Miercoles.from(8).to(11)
        )),
        CreateCourseDTO("C2", List(
          Martes.from(9).to(12),
          Miercoles.from(11).to(14)
        ))
      ),
      "EstrD" -> List(
        CreateCourseDTO("C1", List(
          Lunes.from(15).to(17, 30),
          Martes.from(18).to(21),
          Sabado.from(8).to(10, 30)
        )),
        CreateCourseDTO("C2", List(
          Lunes.from(18).to(20, 30),
          Martes.from(18).to(21),
          Sabado.from(11).to(13, 30)
        ))
      ),
      "Obj2" -> List(
        CreateCourseDTO("C1", List(
          Lunes.from(18).to(21),
          Martes.from(15).to(18)
        )),
        CreateCourseDTO("C2", List(
          Lunes.from(18).to(21),
          Martes.from(18).to(21)
        ))
      ),
      // Avanzadas
      "Redes" -> List(
        CreateCourseDTO("C1", List(
          Martes.from(9).to(11),
          Jueves.from(8).to(12)
        )),
        CreateCourseDTO("C2", List(
          Martes.from(18).to(22),
          Jueves.from(18).to(20)
        ))
      ),
      "SO" -> List(
        CreateCourseDTO("C1", List(
          Martes.from(18).to(22),
          Jueves.from(18).to(20)
        )),
        CreateCourseDTO("C2", List(
          Lunes.from(18).to(22),
          Jueves.from(20).to(22)
        ))
      ),
      "PConc" -> List(
        CreateCourseDTO("C1", List(
          Lunes.from(14).to(18)
        ))
      ),
      "Mate2" -> List(
        CreateCourseDTO("C1", List(
          Lunes.from(11).to(13),
          Martes.from(12).to(14)
        )),
        CreateCourseDTO("C2", List(
          Jueves.from(18).to(22)
        ))
      ),
      "IngSoft" -> List(
        CreateCourseDTO("C1", List(
          Sabado.from(10).to(13),
          Miercoles.from(19).to(22)
        ))
      ),
      "UIs" -> List(
        CreateCourseDTO("C1", List(
          Jueves.from(16).to(22)
        ))
      ),
      "EPers" -> List(
        CreateCourseDTO("C1", List(
          Viernes.from(16).to(22)
        ))
      ),
      "PF" -> List(
        CreateCourseDTO("C1", List(
          Lunes.from(12).to(14),
          Martes.from(11).to(13)
        ))
      ),
      "DesApp" -> List(
        CreateCourseDTO("C1", List(
          Martes.from(16).to(22)
        ))
      ),
      "LabSOR" -> List(
        CreateCourseDTO("C1", List(
          Jueves.from(18).to(22)
        ))
      ),
      // Idioma
      "Ing1" -> List(
        CreateNonCourseDTO("Voy a cursar anual segun oferta del Departamento de Idiomas"),
        CreateNonCourseDTO("Ya estoy cursando")
      ),
      "Ing2" -> List(
        CreateCourseDTO("C1", List(
          Jueves.from(14).to(18)
        ))
      ),
      // Opcionales
      "Seg" -> List(
        CreateCourseDTO("C1", List(
          Sabado.from(9).to(13)
        ))
      ),
      //      "BD2" -> List(),
      //      "ProyLib" -> List(),
      //      "InArq" -> List(),
      "Obj3" -> List(
        CreateCourseDTO("C1", List(
          Miercoles.from(18).to(22)
        ))
      ),
      //      "InBio" -> List(),
      //      "Politicas" -> List(),
      //      "Geo" -> List(),
      //      "Decl" -> List(),
      "videojuegos" -> List(
        CreateCourseDTO("C1", List(
          Miercoles.from(9).to(13)
        ))
      ),
      //      "DADC" -> List(),
      // Seminarios
      "CLP" -> List(
        CreateCourseDTO("C1", List(
          Martes.from(18).to(20),
          Miercoles.from(18).to(20)
        ))
      ),
      "SemMod" -> List(
        CreateCourseDTO("C1", List(
          Martes.from(14).to(18)
        ))
      ),
      // TTI/TTU
      "TTI-TTU" -> List(
        CreateNonCourseDTO("Voy a cursar segun oferta del Departamento de Ciencia y Tecnologia")
      ),
      // Trabajo Final
      "TIP" -> List(
        CreateCourseDTO("C1", List(
          Sabado.from(18).to(13)
        ))
      )
    ))))
    info("Done seeding!")
  }

  implicit class ScheduleFromBuilder(day: Day) {
    def from(fromHour: Int, fromMinutes: Int = 0): ScheduleToBuilder =
      new ScheduleToBuilder(day, fromHour, fromMinutes)
  }

  class ScheduleToBuilder(day: Day, fromHour: Int, fromMinutes: Int) {
    def to(toHour: Int, toMinutes: Int = -1): CreateScheduleDTO =
      CreateScheduleDTO(day.id, fromHour, fromMinutes,
        if (toMinutes == -1) toHour - 1 else toHour,
        if (toMinutes == -1) 59 else toMinutes
      )
  }

}
