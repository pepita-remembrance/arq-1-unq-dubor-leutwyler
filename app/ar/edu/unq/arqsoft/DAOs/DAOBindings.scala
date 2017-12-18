package ar.edu.unq.arqsoft.DAOs

import com.google.inject.Inject

trait DAOBindings {

  @Inject
  var StudentDAO: StudentDAO = _

  @Inject
  var AdminDAO: AdminDAO = _

  @Inject
  var CareerDAO: CareerDAO = _

  @Inject
  var SubjectDAO: SubjectDAO = _

  @Inject
  var OfferDAO: OfferDAO = _

  @Inject
  var CourseDAO: CourseDAO = _

  @Inject
  var NonCourseDAO: NonCourseDAO = _

  @Inject
  var ScheduleDAO: ScheduleDAO = _

  @Inject
  var PollDAO: PollDAO = _

  @Inject
  var PollResultDAO: PollResultDAO = _

  @Inject
  var PollSubjectOptionDAO: PollSubjectOptionDAO = _

  @Inject
  var PollOfferOptionDAO: PollOfferOptionDAO = _

  @Inject
  var PollSelectedOptionDAO: PollSelectedOptionDAO = _

  @Inject
  var StudentCareerDAO: StudentCareerDAO = _

  @Inject
  var AdminCareerDAO: AdminCareerDAO = _

}
