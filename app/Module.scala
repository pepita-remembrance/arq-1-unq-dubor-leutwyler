import ar.edu.unq.arqsoft.DAOs._
import ar.edu.unq.arqsoft.controllers._
import ar.edu.unq.arqsoft.database.{DBConnector, Database, PostgresConnector}
import ar.edu.unq.arqsoft.repository._
import ar.edu.unq.arqsoft.security.JWTService
import ar.edu.unq.arqsoft.services._
import net.codingwell.scalaguice.ScalaModule

class Module extends ScalaModule {
  def configure(): Unit = {
    // Controllers
    bind[AdminController].asEagerSingleton()
    bind[CareerController].asEagerSingleton()
    bind[InitController].asEagerSingleton()
    bind[LoginController].asEagerSingleton()
    bind[PollController].asEagerSingleton()
    bind[PollResultController].asEagerSingleton()
    bind[StudentController].asEagerSingleton()
    // Services
    bind[AdminService].asEagerSingleton()
    bind[CareerService].asEagerSingleton()
    bind[LoginService].asEagerSingleton()
    bind[PollResultService].asEagerSingleton()
    bind[PollService].asEagerSingleton()
    bind[StudentService].asEagerSingleton()
    bind[JWTService].asEagerSingleton()
    // Repositories
    bind[StudentRepository].asEagerSingleton()
    bind[AdminRepository].asEagerSingleton()
    bind[CareerRepository].asEagerSingleton()
    bind[SubjectRepository].asEagerSingleton()
    bind[OfferRepository].asEagerSingleton()
    bind[CourseRepository].asEagerSingleton()
    bind[NonCourseRepository].asEagerSingleton()
    bind[ScheduleRepository].asEagerSingleton()
    bind[PollRepository].asEagerSingleton()
    bind[PollResultRepository].asEagerSingleton()
    bind[PollSubjectOptionRepository].asEagerSingleton()
    bind[PollOfferOptionRepository].asEagerSingleton()
    bind[PollSelectedOptionRepository].asEagerSingleton()
    bind[StudentCareerRepository].asEagerSingleton()
    bind[AdminCareerRepository].asEagerSingleton()
    // DAO
    bind[StudentDAO].asEagerSingleton()
    bind[AdminDAO].asEagerSingleton()
    bind[CareerDAO].asEagerSingleton()
    bind[SubjectDAO].asEagerSingleton()
    bind[OfferDAO].asEagerSingleton()
    bind[CourseDAO].asEagerSingleton()
    bind[NonCourseDAO].asEagerSingleton()
    bind[ScheduleDAO].asEagerSingleton()
    bind[PollDAO].asEagerSingleton()
    bind[PollResultDAO].asEagerSingleton()
    bind[PollSubjectOptionDAO].asEagerSingleton()
    bind[PollOfferOptionDAO].asEagerSingleton()
    bind[PollSelectedOptionDAO].asEagerSingleton()
    bind[AdminCareerDAO].asEagerSingleton()
    bind[StudentCareerDAO].asEagerSingleton()
    // Database
    bind[DBConnector].to[PostgresConnector].asEagerSingleton()
    bind[Database].asEagerSingleton()
  }
}
