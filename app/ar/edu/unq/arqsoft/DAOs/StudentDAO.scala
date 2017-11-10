package ar.edu.unq.arqsoft.DAOs

import javax.inject.Singleton

import ar.edu.unq.arqsoft.database.Database._
import ar.edu.unq.arqsoft.model.Student
import ar.edu.unq.arqsoft.model.TableRow.KeyType

@Singleton
class StudentDAO extends SquerylDAO[Student, KeyType](students, None) {

}
