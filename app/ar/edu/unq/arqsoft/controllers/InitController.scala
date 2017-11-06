package ar.edu.unq.arqsoft.controllers

import javax.inject.{Inject, Singleton}

import ar.edu.unq.arqsoft.database.Database
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class InitController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def seedDatabase() = Action {
    Database.seed()
    Ok("Database seeded")
  }

}
