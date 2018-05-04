package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.maybe.Maybe
import ar.edu.unq.arqsoft.model.Student
import ar.edu.unq.arqsoft.repository.StudentRepository
import authentikat.jwt.JwtClaimsSet
import com.google.inject.{Inject, Singleton}

@Singleton
class StudentService @Inject()(studentRepository: StudentRepository
                              ) extends UserService[Student](studentRepository) {


  protected def makeClaimsSet(user: Student): JwtClaimsSet =
    JwtClaimsSet(
      s"""{
         |"username": "${user.username}",
         |"email": "${user.email}",
         |"role": "student"
         |}""".stripMargin
    )

  def create(dto: CreateStudentDTO): Maybe[StudentDTO] = {
    val newModel = dto.asModel
    for {
      _ <- studentRepository.save(newModel)
    } yield newModel.as[StudentDTO]
  }

  def all: Maybe[Iterable[PartialStudentDTO]] =
    studentRepository.all().mapAs[PartialStudentDTO]

  def byFileNumber(fileNumber: Int): Maybe[StudentDTO] =
    studentRepository.byFileNumber(fileNumber).as[StudentDTO]

}
