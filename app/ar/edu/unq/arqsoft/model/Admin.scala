package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.InscriptionPollSchema
import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Admin(fileNumber: Int, email: String, name: String, surname: String, password: String) extends TableRow
  with Password {
  lazy val careers = InscriptionPollSchema.adminsCareers.left(this)
}

case class AdminCareer(adminId: KeyType, careerId: KeyType) extends TableRow