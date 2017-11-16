package ar.edu.unq.arqsoft.controllers

import com.google.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.database.Database
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class InitController @Inject()(cc: ControllerComponents, database: Database) extends AbstractController(cc) {

  def seedDatabase = Action {
    database.seed()
    Ok("Database seeded")
  }

}
