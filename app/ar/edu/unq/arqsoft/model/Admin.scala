package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.database.InscriptionPollSchema
import ar.edu.unq.arqsoft.model.TableRow.KeyType
import org.joda.time.DateTime

case class Admin(fileNumber: Int, email: String, name: String, surname: String) extends TableRow{
  lazy val careers = InscriptionPollSchema.adminsCareers.left(this)
}

case class AdminCareer(adminId: KeyType, careerId: KeyType, joinDate: DateTime) extends TableRow