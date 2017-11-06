package ar.edu.unq.arqsoft.DAOs

import ar.edu.unq.arqsoft.api.CreateStudentDTO
import ar.edu.unq.arqsoft.model.Student
import ar.edu.unq.arqsoft.database.Database._

trait StudentDAO {

  def create(dto: CreateStudentDTO): Student = {
    val newStudent = Student(dto.fileNumber, dto.email, dto.name, dto.surname)
    students.insert(newStudent)
  }

}

object StudentDAO extends StudentDAO
