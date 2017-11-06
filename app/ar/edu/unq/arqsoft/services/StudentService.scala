package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.DAOs.StudentDAO
import ar.edu.unq.arqsoft.api.CreateStudentDTO
import ar.edu.unq.arqsoft.model.Student
import ar.edu.unq.arqsoft.database.Database.inTransaction

trait StudentService {

  def create(dto: CreateStudentDTO): Student = inTransaction {
    StudentDAO.create(dto)
  }

}

object StudentService extends StudentService
