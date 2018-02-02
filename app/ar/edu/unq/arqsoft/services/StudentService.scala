package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.maybe.Maybe
import ar.edu.unq.arqsoft.repository.StudentRepository
import com.google.inject.{Inject, Singleton}

@Singleton
class StudentService @Inject()(studentRepository: StudentRepository
                              ) extends Service {

  def create(dto: CreateStudentDTO): Maybe[StudentDTO] = {
    val newModel = dto.asModel
    for {
      _ <- studentRepository.save(newModel)
    } yield newModel
  }

  def all: Maybe[Iterable[PartialStudentDTO]] =
    studentRepository.all()

  def byFileNumber(fileNumber: Int): Maybe[StudentDTO] =
    studentRepository.byFileNumber(fileNumber)

}
