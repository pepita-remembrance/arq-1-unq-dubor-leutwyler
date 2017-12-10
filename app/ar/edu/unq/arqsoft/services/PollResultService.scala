package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api.InputAlias.PollDeltaDTO
import ar.edu.unq.arqsoft.api.PollResultDTO
import ar.edu.unq.arqsoft.model.{PollResult, PollSelectedOption}
import com.google.inject.Singleton
import org.joda.time.DateTime

@Singleton
class PollResultService extends Service {

  def pollResultFor(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResultDTO = inTransaction {
    val resultOption =
      PollResultDAO.resultsOf(
        StudentDAO.whereFileNumber(studentFileNumber),
        PollDAO.pollsOf(CareerDAO.whereShortName(careerShortName), pollKey)
      )
        .singleOption
    val result = resultOption.getOrElse(updatedPollResult(studentFileNumber, careerShortName, pollKey))
    result
  }

  protected def newPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResult = inTransaction {
    val pollQuery = PollDAO.pollsOf(CareerDAO.whereShortName(careerShortName), pollKey)
    val student = StudentDAO.whereFileNumber(studentFileNumber).single
    val poll = pollQuery.single
    val pollSubjects = SubjectDAO.subjectsOf(pollQuery).toList
    val defaultOption = OfferDAO.baseOfferOf(NonCourseDAO.notYetOption).single
    val newResult = PollResult(student.id, poll.id, DateTime.now)
    PollResultDAO.save(newResult)
    val selectedOptions = pollSubjects.map(subject => PollSelectedOption(newResult.id, subject.id, defaultOption.id))
    PollSelectedOptionDAO.save(selectedOptions)
    newResult
  }

  protected def updatedPollResult(studentFileNumber: Int, careerShortName: String, pollKey: String): PollResult = inTransaction {
    val baseResult = newPollResult(studentFileNumber, careerShortName, pollKey)
    val alreadyPassedOption = OfferDAO.baseOfferOf(NonCourseDAO.alreadyPassedOption).single
    val passedSubjects = SubjectDAO.subjectsWithOption(
      PollSelectedOptionDAO.optionsOf(PollResultDAO.resultsOf(StudentDAO.whereFileNumber(studentFileNumber), PollDAO.pollsOf(CareerDAO.whereShortName(careerShortName)))),
      OfferDAO.baseOfferOf(NonCourseDAO.alreadyPassedOption),
      SubjectDAO.subjectsOf(PollDAO.pollsOf(CareerDAO.whereShortName(careerShortName), pollKey))
    )
    val optionsToUpdate = PollSelectedOptionDAO.optionsOfWithSubject(PollResultDAO.find(baseResult.id), passedSubjects)
    PollSelectedOptionDAO.updateSelectionTo(optionsToUpdate, alreadyPassedOption.id)
    baseResult
  }

  def update(studentFileNumber: Int, careerShortName: String, pollKey: String, delta: PollDeltaDTO): PollResultDTO = inTransaction {
    val base = pollResultFor(studentFileNumber, careerShortName, pollKey)
    //TODO: apply delta
    info(delta.toString())
    base
  }


}
