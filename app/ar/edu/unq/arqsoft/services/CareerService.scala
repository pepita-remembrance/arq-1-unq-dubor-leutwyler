package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.maybe.{Maybe, Something}
import ar.edu.unq.arqsoft.model.{AdminCareer, StudentCareer}
import com.google.inject.Singleton
import org.joda.time.DateTime

@Singleton
class CareerService
  extends Service {

  def create(dto: CreateCareerDTO): Maybe[CareerDTO] = inTransaction {
    val newCareer = dto.asModel
    CareerDAO.save(newCareer)
    dto.subjects.foreach(subjects => SubjectDAO.save(subjects.map(_.asModel(newCareer))))
    Something(newCareer)
  }

  def all: Maybe[Iterable[PartialCareerDTO]] = inTransaction {
    CareerDAO.all.mapAs[PartialCareerDTO]
  }

  def byShortName(shortName: String): Maybe[CareerDTO] = inTransaction {
    CareerDAO.byShortName(shortName)
      .orNotFoundWith("short name", shortName)
      .as[CareerDTO]
  }

  def joinStudent(dto: CreateStudentCareerDTO, joinDate: DateTime = DateTime.now): Maybe[CareerDTO] = inTransaction {
    (for {
      student <- StudentDAO.byFileNumber(dto.studentFileNumber).orNotFoundWith("file number", dto.studentFileNumber)
      career <- CareerDAO.byShortName(dto.careerShortName).orNotFoundWith("short name", dto.careerShortName)
      _ = StudentCareerDAO.save(StudentCareer(student.id, career.id, joinDate))
    } yield career
      ).as[CareerDTO]
  }

  def joinAdmin(dto: CreateAdminCareerDTO): Maybe[CareerDTO] = inTransaction {
    (for {
      admin <- AdminDAO.byFileNumber(dto.adminFileNumber).orNotFoundWith("file number", dto.adminFileNumber)
      career <- CareerDAO.byShortName(dto.careerShortName).orNotFoundWith("short name", dto.careerShortName)
      _ = AdminCareerDAO.save(AdminCareer(admin.id, career.id))
    } yield career
      ).as[CareerDTO]
  }

}
