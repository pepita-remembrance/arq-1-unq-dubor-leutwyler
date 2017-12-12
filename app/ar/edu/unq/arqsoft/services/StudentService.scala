package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import com.google.inject.Singleton
import org.joda.time.DateTime

@Singleton
class StudentService extends Service with StudentCareerService {

  def create(dto: CreateStudentDTO): StudentDTO = inTransaction {
    val newStudent = dto.asModel
    StudentDAO.save(newStudent)
    newStudent
  }

  def all: Iterable[PartialStudentDTO] = inTransaction {
    StudentDAO.all.mapAs[PartialStudentDTO]
  }

  def byFileNumber(fileNumber: Int): StudentDTO = inTransaction {
    StudentDAO.whereFileNumber(fileNumber).single
  }

  def joinCareer(dto: CreateStudentCareerDTO, joinDate:DateTime = DateTime.now): StudentDTO = inTransaction {
    createStudentCareer(dto, joinDate)._1
  }

}
