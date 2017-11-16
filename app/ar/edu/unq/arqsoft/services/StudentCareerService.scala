package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.model.{Career, Student}

trait StudentCareerService extends Service {

  protected def joinCareer(student: Student, career: Career): Unit = inTransaction {
    student.careers.associate(career)
  }

}
