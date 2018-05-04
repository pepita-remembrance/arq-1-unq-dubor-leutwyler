package ar.edu.unq.arqsoft.utils

import ar.edu.unq.arqsoft.api.InputDTO
import org.squeryl.KeyedEntity

abstract class ModelConverter0[DTO <: InputDTO, Model <: KeyedEntity[_]](dto: DTO) {
  def asModel: Model
}

abstract class ModelConverter1[DTO <: InputDTO, Model <: KeyedEntity[_]](dto: DTO) {
  type Extra1

  def asModel(extra1: Extra1): Model
}

abstract class ModelConverter2[DTO <: InputDTO, Model <: KeyedEntity[_]](dto: DTO) {
  type Extra1
  type Extra2

  def asModel(extra1: Extra1, extra2: Extra2): Model
}
