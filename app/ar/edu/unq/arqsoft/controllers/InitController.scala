package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.database.Database
import com.google.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class InitController @Inject()(cc: ControllerComponents, database: Database) extends AbstractController(cc) {

  def seedDatabase = Action {
    database.seed()
    Ok("Database seeded")
  }

  def seedForStress(amount: Option[Int]) = Action {
    database.seedForStress(amount)
    Ok("Database seeded for stress testing")
  }

}
