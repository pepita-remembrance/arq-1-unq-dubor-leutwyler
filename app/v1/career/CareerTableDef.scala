package v1.career
import slick.jdbc.JdbcProfile


trait CareerTableDef {
  protected val driver: JdbcProfile
  import driver.api._

  class CareerTable(tag: Tag) extends Table[CareerData](tag, "career"){
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def name = column[String]("name")
    def description = column[String]("description")

    override def * = (id, name, description) <>(CareerData.tupled, CareerData.unapply)
  }
}
