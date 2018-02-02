package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.maybe.Maybe
import ar.edu.unq.arqsoft.model.{AdminCareer, StudentCareer}
import ar.edu.unq.arqsoft.repository._
import com.google.inject.{Inject, Singleton}
import org.joda.time.DateTime

@Singleton
class CareerService @Inject()(careerRepository: CareerRepository,
                              studentRepository: StudentRepository,
                              subjectRepository: SubjectRepository,
                              studentCareerRepository: StudentCareerRepository,
                              adminRepository: AdminRepository,
                              adminCareerRepository: AdminCareerRepository
                             ) extends Service {

  def create(dto: CreateCareerDTO): Maybe[CareerDTO] = {
    val newModel = dto.asModel
    for {
      _ <- careerRepository.save(newModel)
      newSubjects = dto.subjects.getOrElse(Nil).map(_.asModel(newModel))
      _ <- subjectRepository.save(newSubjects)
    } yield newModel
  }

  def all: Maybe[Iterable[PartialCareerDTO]] =
    careerRepository.all()

  def byShortName(shortName: String): Maybe[CareerDTO] =
    careerRepository.byShortName(shortName)

  def joinStudent(dto: CreateStudentCareerDTO, joinDate: DateTime = DateTime.now): Maybe[CareerDTO] =
    for {
      student <- studentRepository.byFileNumber(dto.studentFileNumber)
      career <- careerRepository.byShortName(dto.careerShortName)
      _ <- studentCareerRepository.save(StudentCareer(student.id, career.id, joinDate))
    } yield career

  def joinAdmin(dto: CreateAdminCareerDTO): Maybe[CareerDTO] =
    for {
      admin <- adminRepository.byFileNumber(dto.adminFileNumber)
      career <- careerRepository.byShortName(dto.careerShortName)
      _ <- adminCareerRepository.save(AdminCareer(admin.id, career.id))
    } yield career
}
