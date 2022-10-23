package module3.cats_effect_homework

import cats.data.NonEmptyList
import cats.effect.{FiberIO, IO, IOApp, Resource,Sync}
import cats.implicits._

import scala.concurrent.duration.{DurationInt, FiniteDuration}

// Поиграемся с кошельками на файлах и файберами.

// Нужно написать программу где инициализируются три разных кошелька и для каждого из них работает фоновый процесс,
// который регулярно пополняет кошелек на 100 рублей раз в определенный промежуток времени. Промежуток надо сделать разный, чтобы легче было наблюдать разницу.
// Для определенности: первый кошелек пополняем раз в 100ms, второй каждые 500ms и третий каждые 2000ms.
// Помимо этих трёх фоновых процессов (подсказка - это файберы), нужен четвертый, который раз в одну секунду будет выводить балансы всех трех кошельков в консоль.
// Основной процесс программы должен просто ждать ввода пользователя (IO.readline) и завершить программу (включая все фоновые процессы) когда ввод будет получен.
// Итого у нас 5 процессов: 3 фоновых процесса регулярного пополнения кошельков, 1 фоновый процесс регулярного вывода балансов на экран и 1 основной процесс просто ждущий ввода пользователя.

// Можно делать всё на IO, tagless final тут не нужен.

// Подсказка: чтобы сделать бесконечный цикл на IO достаточно сделать рекурсивный вызов через flatMap:
// def loop(): IO[Unit] = IO.println("hello").flatMap(_ => loop())




object WalletFibersApp extends IOApp.Simple {

  def periodicTopup(wallet: Wallet[IO], amount: BigDecimal, duration: FiniteDuration): IO[Unit] =
    IO.sleep(duration) *> wallet.topup(amount).flatMap(_ => periodicTopup(wallet,amount, duration))

  def printWalletBalance(list: NonEmptyList[Wallet[IO]], amount: BigDecimal, duration: FiniteDuration): IO[Unit] =
    IO.sleep(duration) *> IO.pure(list.map(wallet =>
      wallet.balance.map(balance =>
        IO.println(s"Wallet: $balance"))))
      .flatMap(_ => printWalletBalance(list,amount, duration))


  def periodicTopupF(wallet: IO[Wallet[IO]], amount: BigDecimal, duration: FiniteDuration) = {
    def repeat(wallet:  IO[Wallet[IO]], amount: BigDecimal, duration: FiniteDuration): IO[Unit] =
      IO.sleep(duration) *> wallet.flatMap(w => w.topup(amount)).flatMap(_ => repeat(wallet,amount, duration))
    repeat(wallet,amount, duration).start
  }


  def printWalletBalanceF(wallet1:  IO[Wallet[IO]], wallet2:  IO[Wallet[IO]], wallet3:  IO[Wallet[IO]], amount: BigDecimal, duration: FiniteDuration) = {
    def repeat(wallet1:  IO[Wallet[IO]], wallet2:  IO[Wallet[IO]], wallet3:  IO[Wallet[IO]], amount: BigDecimal, duration: FiniteDuration): IO[Unit] =
      IO.sleep(duration) *> wallet1.flatMap(w => w.balance.flatMap(IO.println)) *> wallet2.flatMap(w => w.balance.flatMap(IO.println))*> wallet3.flatMap(w => w.balance.flatMap(IO.println))
        .flatMap(_ => repeat(wallet1, wallet2, wallet3, amount, duration))
    repeat(wallet1, wallet2, wallet3, amount, duration).start
  }

  final case class Environment(
    wallet1: FiberIO[Unit],
    wallet2: FiberIO[Unit],
    wallet3: FiberIO[Unit],
    balance: FiberIO[Unit]
  )

  object Environment {
    def build: Resource[IO, Environment] = {

      val wallet1 = Wallet.fileWallet[IO]("1")
      val wallet2 = Wallet.fileWallet[IO]("2")
      val wallet3 = Wallet.fileWallet[IO]("3")

      val fiberWallet1: Resource[IO, FiberIO[Unit]] = Resource.make(periodicTopupF(wallet1, 100, 100.millis))(w =>
        w.cancel *> IO.println(s"Destroying fiberWallet1")
      )
      val fiberWallet2 = Resource.make(periodicTopupF(wallet2, 100, 500.millis))(w =>
        w.cancel *> IO.println(s"Destroying fiberWallet2")
      )
      val fiberWallet3 = Resource.make(periodicTopupF(wallet3, 100, 2000.millis))(w =>
        w.cancel *> IO.println(s"Destroying fiberWallet3")
      )
      val fiberBalance = Resource.make(printWalletBalanceF(wallet1, wallet2, wallet3, 100, 1000.millis))(w =>
        w.cancel *> IO.println(s"Destroying fiberBalance")
      )
      for {
        fw1 <- fiberWallet1
        fw2 <- fiberWallet2
        fw3 <- fiberWallet3
        fb <- fiberBalance
      } yield Environment(fw1, fw2, fw3, fb)
    }
  }



  def program(env: Environment): IO[Unit] =
    IO.readLine.flatMap { cmd => IO.println("Bye bye")}

  def run: IO[Unit] =
    Environment.build.use { env =>
      IO.print("Press any key to stop...") *> program(env)
    }

}
