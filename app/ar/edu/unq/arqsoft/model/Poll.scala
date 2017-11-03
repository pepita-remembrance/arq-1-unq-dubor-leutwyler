package ar.edu.unq.arqsoft.model

import ar.edu.unq.arqsoft.model.TableRow.KeyType

case class Poll(key: String, careerId: KeyType, isOpen: Boolean) extends TableRow

case class PollSubjectOffer(pollId: KeyType, subjectId: KeyType, offerId: KeyType)

