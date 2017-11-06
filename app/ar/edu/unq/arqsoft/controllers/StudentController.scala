package ar.edu.unq.arqsoft.controllers

import javax.inject._

import play.api.mvc._

@Singleton
class StudentController @Inject()(cc: ControllerComponents, parse: PlayBodyParsers) extends BasicController(cc, parse)
