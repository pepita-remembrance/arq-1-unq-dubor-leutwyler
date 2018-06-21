package ar.edu.unq.arqsoft.controllers

import ar.edu.unq.arqsoft.database.Database
import com.google.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class InitController @Inject()(cc: ControllerComponents, database: Database) extends AbstractController(cc) {

  def seedDatabase = Action {
    database.seed()
    Ok("Database seeded")
  }

  def seedForStress = Action {
    database.seedForStress()
    Ok("Database seeded fro stress testing")
  }

}
