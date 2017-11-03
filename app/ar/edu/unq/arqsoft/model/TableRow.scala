package ar.edu.unq.arqsoft.model

import org.squeryl.KeyedEntity

object TableRow {
  type KeyType = Long
}

import TableRow._

trait TableRow extends KeyedEntity[KeyType] {
  var id: KeyType = 0
}
