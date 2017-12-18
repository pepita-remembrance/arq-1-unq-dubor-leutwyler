package ar.edu.unq.arqsoft.database

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.logging.Logging
import ar.edu.unq.arqsoft.model.Day.Day
import ar.edu.unq.arqsoft.services._
import com.google.inject.Inject
import org.joda.time.DateTime

trait SeedData extends Logging {
  @Inject
  var studentService: StudentService = _
  @Inject
  var adminService: AdminService = _
  @Inject
  var careerService: CareerService = _
  @Inject
  var pollService: PollService = _
  @Inject
  var pollResultService: PollResultService = _

  def defaultOffer: Map[String, List[CreateOfferOptionDTO]] = {
    import ar.edu.unq.arqsoft.model.Day.{Friday => Viernes, Monday => Lunes, Saturday => Sabado, Thursday => Jueves, Tuesday => Martes, Wednesday => Miercoles}
    Map(
      // Basicas
      "InPr" -> List(
        CreateCourseDTO("C1", 40, List(
          Lunes.from(9).to(11, 30),
          Jueves.from(9).to(11, 30),
          Miercoles.from(9).to(12)
        )),
        CreateCourseDTO("C2", 40, List(
          Lunes.from(12).to(14, 30),
          Jueves.from(12).to(14, 30),
          Miercoles.from(9).to(12)
        )),
        CreateCourseDTO("C3", 40, List(
          Lunes.from(16).to(18, 30),
          Jueves.from(16).to(18, 30),
          Miercoles.from(18).to(21)
        )),
        CreateCourseDTO("C4", 40, List(
          Lunes.from(19).to(21, 30),
          Jueves.from(19).to(21, 30),
          Miercoles.from(18).to(21)
        ))
      ),
      "Orga" -> List(
        CreateCourseDTO("C1", 40, List(
          Martes.from(9).to(12),
          Viernes.from(9).to(12)
        )),
        CreateCourseDTO("C2", 40, List(
          Martes.from(8, 30).to(11, 30),
          Sabado.from(9).to(12)
        )),
        CreateCourseDTO("C3", 40, List(
          Martes.from(16).to(19),
          Viernes.from(16).to(19)
        )),
        CreateCourseDTO("C4", 40, List(
          Martes.from(19).to(22),
          Viernes.from(19).to(22)
        ))
      ),
      "Mate1" -> List(
        CreateCourseDTO("C1", 40, List(
          Martes.from(9).to(13),
          Viernes.from(9).to(13)
        )),
        CreateCourseDTO("C2", 40, List(
          Lunes.from(18).to(22),
          Miercoles.from(18).to(22)
        )),
        CreateCourseDTO("C3", 40, List(
          Lunes.from(9).to(13),
          Miercoles.from(9).to(13)
        ))
      ),
      "Obj1" -> List(
        CreateCourseDTO("C1", 40, List(
          Miercoles.from(15).to(19),
          Jueves.from(14).to(18)
        )),
        CreateCourseDTO("C2", 40, List(
          Lunes.from(18).to(22),
          Miercoles.from(18).to(22)
        ))
      ),
      "BD" -> List(
        CreateCourseDTO("C1", 30, List(
          Martes.from(9).to(12),
          Miercoles.from(8).to(11)
        )),
        CreateCourseDTO("C2", 30, List(
          Martes.from(9).to(12),
          Miercoles.from(11).to(14)
        ))
      ),
      "EstrD" -> List(
        CreateCourseDTO("C1", 35, List(
          Lunes.from(15).to(17, 30),
          Martes.from(18).to(21),
          Sabado.from(8).to(10, 30)
        )),
        CreateCourseDTO("C2", 35, List(
          Lunes.from(18).to(20, 30),
          Martes.from(18).to(21),
          Sabado.from(11).to(13, 30)
        ))
      ),
      "Obj2" -> List(
        CreateCourseDTO("C1", 30, List(
          Lunes.from(18).to(21),
          Martes.from(15).to(18)
        )),
        CreateCourseDTO("C2", 30, List(
          Lunes.from(18).to(21),
          Martes.from(18).to(21)
        ))
      ),
      // Avanzadas
      "Redes" -> List(
        CreateCourseDTO("C1", 30, List(
          Martes.from(9).to(11),
          Jueves.from(8).to(12)
        )),
        CreateCourseDTO("C2", 30, List(
          Martes.from(18).to(22),
          Jueves.from(18).to(20)
        ))
      ),
      "SO" -> List(
        CreateCourseDTO("C1", 30, List(
          Martes.from(18).to(22),
          Jueves.from(18).to(20)
        )),
        CreateCourseDTO("C2", 30, List(
          Lunes.from(18).to(22),
          Jueves.from(20).to(22)
        ))
      ),
      "PConc" -> List(
        CreateCourseDTO("C1", 30, List(
          Lunes.from(14).to(18)
        ))
      ),
      "Mate2" -> List(
        CreateCourseDTO("C1", 30, List(
          Lunes.from(11).to(13),
          Martes.from(12).to(14)
        )),
        CreateCourseDTO("C2", 30, List(
          Jueves.from(18).to(22)
        ))
      ),
      "IngSoft" -> List(
        CreateCourseDTO("C1", 30, List(
          Sabado.from(10).to(13),
          Miercoles.from(19).to(22)
        ))
      ),
      "UIs" -> List(
        CreateCourseDTO("C1", 30, List(
          Jueves.from(16).to(22)
        ))
      ),
      "EPers" -> List(
        CreateCourseDTO("C1", 30, List(
          Viernes.from(16).to(22)
        ))
      ),
      "PF" -> List(
        CreateCourseDTO("C1", 30, List(
          Lunes.from(12).to(14),
          Martes.from(11).to(13)
        ))
      ),
      "DesApp" -> List(
        CreateCourseDTO("C1", 30, List(
          Martes.from(16).to(22)
        ))
      ),
      "LabSOR" -> List(
        CreateCourseDTO("C1", 30, List(
          Jueves.from(18).to(22)
        ))
      ),
      // Idioma
      "Ing1" -> List(
        CreateNonCourseDTO("Voy a cursar anual segun oferta del Departamento de Idiomas"),
        CreateNonCourseDTO("Ya estoy cursando"),
        CreateNonCourseDTO("Voy a rendir libre")
      ),
      "Ing2" -> List(
        CreateCourseDTO("C1", 30, List(
          Jueves.from(14).to(18)
        ))
      ),
      // Opcionales
      "Seg" -> List(
        CreateCourseDTO("C1", 30, List(
          Sabado.from(9).to(13)
        ))
      ),
      //      "BD2" -> List(),
      //      "ProyLib" -> List(),
      //      "InArq" -> List(),
      "Obj3" -> List(
        CreateCourseDTO("C1", 30, List(
          Miercoles.from(18).to(22)
        ))
      ),
      //      "InBio" -> List(),
      //      "Politicas" -> List(),
      //      "Geo" -> List(),
      //      "Decl" -> List(),
      "videojuegos" -> List(
        CreateCourseDTO("C1", 30, List(
          Miercoles.from(9).to(13)
        ))
      ),
      //      "DADC" -> List(),
      // Seminarios
      "CLP" -> List(
        CreateCourseDTO("C1", 30, List(
          Martes.from(18).to(20),
          Miercoles.from(18).to(20)
        ))
      ),
      "SemMod" -> List(
        CreateCourseDTO("C1", 30, List(
          Martes.from(14).to(18)
        ))
      ),
      // TTI/TTU
      "TTI-TTU" -> List(
        CreateNonCourseDTO("Voy a cursar TTI segun oferta del Departamento de Ciencia y Tecnologia"),
        CreateNonCourseDTO("Voy a cursar TTU segun oferta del Departamento de Ciencia y Tecnologia")
      ),
      // Trabajo Final
      "TIP" -> List(
        CreateCourseDTO("C1", 30, List(
          Sabado.from(18).to(13)
        ))
      )
    )
  }

  def defaultExtraData: Map[String, String] = Map(
    // Basicas
    "InPr" -> "",
    "Orga" -> "",
    "Mate1" -> "",
    "Obj1" -> "Requiere InPr",
    "BD" -> "",
    "EstrD" -> "",
    "Obj2" -> "Requiere Obj1",
    // Avanzadas
    "Redes" -> "Requiere Orga",
    "SO" -> "Requiere Obj2 y EstrD",
    "PConc" -> "Requiere Obj2 y EstrD",
    "Mate2" -> "Requiere Mate1",
    "IngSoft" -> "Requiere Obj2",
    "UIs" -> "Requiere Obj2",
    "EPers" -> "Requiere Obj2",
    "PF" -> "Requiere EstrD",
    "DesApp" -> "Requiere UIs y EPers",
    "LabSOR" -> "Requiere Redes",
    // Idioma
    "Ing1" -> "",
    "Ing2" -> "Requiere Ing2",
    // Opcionales
    "Seg" -> "Requiere Redes y SO",
    //      "BD2" -> "",
    //      "ProyLib" -> "",
    //      "InArq" -> "",
    "Obj3" -> "Requiere 2 de 3: UIs, EPers o PF",
    //      "InBio" -> "",
    //      "Politicas" -> "",
    //      "Geo" -> "",
    //      "Decl" -> "",
    "videojuegos" -> "Requiere Obj2 y EstrD",
    //      "DADC" -> "",
    // Seminarios
    "CLP" -> "Requiere Obj2 y PF",
    "SemMod" -> "Requiere Obj2",
    // TTI/TTU
    "TTI-TTU" -> "",
    // Trabajo Final
    "TIP" -> "Requiere el 85% de la carrera"
  )

  def seed(): Unit = {
    info("Seeding database...")
    info("Creating default poll offer options...")
    pollService.createDefaultOptions()
    info("Creating admins...")
    List(
      CreateAdminDTO(147, "fidel.ml@gmail.com", "Pablo", "Suarez"),
      CreateAdminDTO(258, "pablo.suarez@gmail.com", "Pablo", "Suarez"),
      CreateAdminDTO(369, "gabriela.arevalo@gmail.com", "Gabriela", "Arevalo")
    ).map(adminService.create)
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
    info("Creating students...")
    List(
      CreateStudentDTO(123, "marcogomez@gmail.com", "Marco", "Gomez"),
      CreateStudentDTO(456, "joaquinsanchez@gmail.com", "Joaquin", "Sanchez")
    ).map(studentService.create)
    info("Admins join their careers")
    careerService.joinAdmin(CreateAdminCareerDTO(147, "TPI"))
    careerService.joinAdmin(CreateAdminCareerDTO(258, "TPI"))
    info("Students join their careers")
    careerService.joinStudent(CreateStudentCareerDTO(123, "TPI"), joinDate = DateTime.now.withDate(2014, 12, 15))
    careerService.joinStudent(CreateStudentCareerDTO(456, "TPI"), joinDate = DateTime.now.withDate(2017, 5, 15))
    info("Creating poll for TPI")
    pollService.create("TPI", CreatePollDTO("2015s1", Some(defaultOffer), Some(defaultExtraData)), createDate = DateTime.now.withDate(2015, 2, 1))
    pollService.create("TPI", CreatePollDTO("2015s2", Some(defaultOffer), Some(defaultExtraData)), createDate = DateTime.now.withDate(2015, 6, 1))
    pollService.create("TPI", CreatePollDTO("2016s1", Some(defaultOffer), Some(defaultExtraData)), createDate = DateTime.now.withDate(2016, 2, 1))
    pollService.create("TPI", CreatePollDTO("2016s2", Some(defaultOffer), Some(defaultExtraData)), createDate = DateTime.now.withDate(2016, 6, 1))
    pollService.create("TPI", CreatePollDTO("2017s1", Some(defaultOffer), Some(defaultExtraData)), createDate = DateTime.now.withDate(2017, 2, 1))
    pollService.create("TPI", CreatePollDTO("2017s2", Some(defaultOffer), Some(defaultExtraData)), createDate = DateTime.now.withDate(2017, 6, 1))
    pollService.create("TPI", CreatePollDTO("2018s1", Some(defaultOffer), Some(defaultExtraData)), createDate = DateTime.now.withDate(2018, 2, 1))
    info("Answers for TPI 2015s1")
    pollResultService.update(123, "TPI", "2015s1", Map(
      "InPr" -> SelectedCourse("C1"),
      "Orga" -> SelectedCourse("C1"),
      "Mate1" -> SelectedCourse("C1"),
      "Ing1" -> SelectedNonCourse("Voy a rendir libre")
    ), updateDate = DateTime.now.withDate(2015, 2, 12))
    info("Answers for TPI 2015s2")
    pollResultService.update(123, "TPI", "2015s2", Map(
      "InPr" -> Passed,
      "Orga" -> Passed,
      "Mate1" -> Passed,
      "Ing1" -> Passed,
      "Obj1" -> SelectedCourse("C1"),
      "BD" -> SelectedCourse("C1"),
      "EstrD" -> SelectedCourse("C1"),
      "Ing2" -> SelectedCourse("C1")
    ), updateDate = DateTime.now.withDate(2015, 6, 12))
    info("Answers for TPI 2016s1")
    pollResultService.update(123, "TPI", "2016s1", Map(
      "Obj1" -> Passed,
      "BD" -> Passed,
      "EstrD" -> Passed,
      "Ing2" -> Passed,
      "Mate2" -> SelectedCourse("C1"),
      "Obj2" -> SelectedCourse("C1"),
      "SO" -> SelectedCourse("C1"),
      "Redes" -> SelectedCourse("C1")
    ), updateDate = DateTime.now.withDate(2016, 2, 12))
    info("Answers for TPI 2016s2")
    pollResultService.update(123, "TPI", "2016s2", Map(
      "Mate2" -> Passed,
      "Obj2" -> Passed,
      "SO" -> Passed,
      "Redes" -> Passed,
      "EPers" -> SelectedCourse("C1"),
      "UIs" -> SelectedCourse("C1"),
      "IngSoft" -> SelectedCourse("C1"),
      "PConc" -> SelectedCourse("C1")
    ), updateDate = DateTime.now.withDate(2016, 6, 12))
    info("Answers for TPI 2017s1")
    pollResultService.update(123, "TPI", "2017s1", Map(
      "EPers" -> Passed,
      "UIs" -> Passed,
      "IngSoft" -> SelectedCourse("C1"),
      "PConc" -> SelectedCourse("C1"),
      "LabSOR" -> SelectedCourse("C1")
    ), updateDate = DateTime.now.withDate(2017, 2, 12))
    info("Answers for TPI 2017s2")
    pollResultService.update(123, "TPI", "2017s2", Map(
      "IngSoft" -> Passed,
      "LabSOR" -> Passed,
      "PConc" -> SelectedCourse("C1"),
      "TTI-TTU" -> SelectedNonCourse("Voy a cursar TTI segun oferta del Departamento de Ciencia y Tecnologia"),
      "SemMod" -> SelectedCourse("C1")
    ), updateDate = DateTime.now.withDate(2017, 6, 12))
    info("Answers for TPI 2018s1")
    pollResultService.update(123, "TPI", "2018s1", Map(
      "PConc" -> Passed,
      "TTI-TTU" -> Passed,
      "SemMod" -> Passed,
      "PF" -> SelectedCourse("C1"),
      "Obj3" -> SelectedCourse("C1")
    ), updateDate = DateTime.now.withDate(2018, 2, 12))
    info("Done seeding!")
  }

  def SelectedCourse(key: String) = PollSelectedOptionDTO(key, isCourse = true)

  def SelectedNonCourse(key: String) = PollSelectedOptionDTO(key, isCourse = false)

  def Passed = SelectedNonCourse("Ya aprobe")

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
