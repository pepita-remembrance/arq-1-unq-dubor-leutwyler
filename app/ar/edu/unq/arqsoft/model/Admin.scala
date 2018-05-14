package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.InscriptionPollSchema
import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Admin(username: String, password: String, fileNumber: Int, email: String, name: String, surname: String)
  extends User with TableRow {
  lazy val careers = InscriptionPollSchema.adminsCareers.left(this)
}

case class AdminCareer(adminId: KeyType, careerId: KeyType) extends TableRow