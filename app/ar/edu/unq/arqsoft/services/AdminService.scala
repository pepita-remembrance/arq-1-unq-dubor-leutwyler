package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.maybe.Maybe
import ar.edu.unq.arqsoft.model.Admin
import ar.edu.unq.arqsoft.repository.{AdminRepository, CareerRepository, PollRepository}
import ar.edu.unq.arqsoft.security.Role
import com.google.inject.{Inject, Singleton}

@Singleton
class AdminService @Inject()(adminRepository: AdminRepository,
                             careerRepository: CareerRepository,
                             pollRepository: PollRepository
                            ) extends UserService[Admin](adminRepository, Role.Admin) {


  override protected def customClaims(user: Admin): Map[String, Any] =
    super.customClaims(user) + ("fileNumber" -> user.fileNumber)

  def create(dto: CreateAdminDTO): Maybe[AdminDTO] = {
    val newModel = dto.asModel
    for {
      _ <- adminRepository.save(newModel)
    } yield newModel.as[AdminDTO]
  }

  def all: Maybe[Iterable[PartialAdminDTO]] =
    adminRepository.all().mapAs[PartialAdminDTO]

  def byFileNumber(fileNumber: Int): Maybe[AdminDTO] =
    adminRepository.byFileNumber(fileNumber).as[AdminDTO]

  def careers(fileNumber: Int): Maybe[Iterable[PartialCareerForAdminDTO]] =
    for {
      admin <- adminRepository.byFileNumber(fileNumber)
      careers <- careerRepository.getOfAdmin(admin)
    } yield careers.mapAs[PartialCareerForAdminDTO]

  def polls(fileNumber: Int): Maybe[Iterable[PartialPollForAdminDTO]] =
    for {
      admin <- adminRepository.byFileNumber(fileNumber)
      polls <- pollRepository.getOfAdmin(admin)
    } yield polls.mapAs[PartialPollForAdminDTO]
}