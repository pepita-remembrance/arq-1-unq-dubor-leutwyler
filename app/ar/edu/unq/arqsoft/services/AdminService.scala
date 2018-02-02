package ar.edu.unq.arqsoft.services

import ar.edu.unq.arqsoft.api._
import ar.edu.unq.arqsoft.maybe.Maybe
import ar.edu.unq.arqsoft.repository.{AdminRepository, CareerRepository, PollRepository}
import com.google.inject.{Inject, Singleton}

@Singleton
class AdminService @Inject()(adminRepository: AdminRepository,
                             careerRepository: CareerRepository,
                             pollRepository: PollRepository
                            ) extends Service {

  def create(dto: CreateAdminDTO): Maybe[AdminDTO] = {
    val newModel = dto.asModel
    for {
      _ <- adminRepository.save(newModel)
    } yield newModel
  }

  def all: Maybe[Iterable[PartialAdminDTO]] =
    adminRepository.all()

  def byFileNumber(fileNumber: Int): Maybe[AdminDTO] =
    adminRepository.byFileNumber(fileNumber)

  def careers(fileNumber: Int): Maybe[Iterable[PartialCareerForAdminDTO]] =
    for {
      admin <- adminRepository.byFileNumber(fileNumber)
      careers <- careerRepository.getOfAdmin(admin)
    } yield careers

  def polls(fileNumber: Int): Maybe[Iterable[PartialPollForAdminDTO]] =
    for {
      admin <- adminRepository.byFileNumber(fileNumber)
      polls <- pollRepository.getOfAdmin(admin)
    } yield polls
}