package module3

import module3.zioConcurrency.printEffectRunningTime
import zio.{Has, IO, Ref, Schedule, Task, UIO, ULayer, URIO, ZIO, ZLayer, clock, console}
import zio.clock.{Clock, sleep}
import zio.console.{Console, getStrLn, putStrLn}
import zio.duration.durationInt
import zio.macros.accessible
import zio.random._

import java.util.concurrent.TimeUnit
import scala.language.postfixOps
import module3.zio_homework.config._

package object zio_homework {
  /**
   * 1.
   * Используя сервисы Random и Console, напишите консольную ZIO программу которая будет предлагать пользователю угадать число от 1 до 3
   * и печатать в когнсоль угадал или нет. Подумайте, на какие наиболее простые эффекты ее можно декомпозировать.
   */


  //def writeLine(str: String) = putStrLn(str)

  lazy val lineToInt = getStrLn.flatMap(s => ZIO.effect(s.toInt))

  lazy val lineToIntOrRetry: ZIO[Console, Nothing, Int] = lineToInt.orElse(
    putStrLn("Не корректный ввод, попробуй еще") *> lineToIntOrRetry)

  lazy val random = nextIntBetween(1, 4)

  lazy val startGuess = putStrLn("Угадайте число от 1 до 3") *> random

  def printCheck(a: Int, b: Int) =
    if(a == b) putStrLn("Вы угадали!")
    else  putStrLn("Вы не угадали")

  lazy val guessProgram: ZIO[Console with Random, Throwable, Unit] = for{
    rnd <- startGuess
    i <- lineToIntOrRetry
    _ <- printCheck(i, rnd)
  } yield ()

  lazy val guessProgram2: ZIO[Console with Random, Throwable, Boolean] = for{
    rnd <- startGuess
    i <- lineToIntOrRetry
    _ <- printCheck(i, rnd)
  } yield (rnd == i)

  /**
   * 2. реализовать функцию doWhile (общего назначения), которая будет выполнять эффект до тех пор, пока его значение в условии не даст true
   * 
   */

  val schedule = Schedule.recurWhile[Boolean](_ != true)


  def doWhile[R,E](ef:ZIO[R, E, Boolean]) = ef.repeat(schedule)



  /**
   * 3. Реализовать метод, который безопасно прочитает конфиг из файла, а в случае ошибки вернет дефолтный конфиг
   * и выведет его в консоль
   * Используйте эффект "load" из пакета config
   */

  def loadConfigOrDefault =  load.orElse(ZIO.effect(AppConfig("localhost","8080")))


  /**
   * 4. Следуйте инструкциям ниже для написания 2-х ZIO программ,
   * обратите внимание на сигнатуры эффектов, которые будут у вас получаться,
   * на изменение этих сигнатур
   */


  /**
   * 4.1 Создайте эффект, который будет возвращать случайеым образом выбранное число от 0 до 10 спустя 1 секунду
   * Используйте сервис zio Random
   */


 lazy val eff = for{
    _ <- ZIO.sleep(1.seconds)
    rnd <- nextIntBetween(0, 11)
  }yield rnd

  /**
   * 4.2 Создайте коллукцию из 10 выше описанных эффектов (eff)
   */
  lazy val effects = List.fill(10)(eff)

  
  /**
   * 4.3 Напишите программу которая вычислит сумму элементов коллекци "effects",
   * напечатает ее в консоль и вернет результат, а также залогирует затраченное время на выполнение,
   * можно использовать ф-цию printEffectRunningTime, которую мы разработали на занятиях
   */

  lazy val empty: ZIO[Random with Clock, Throwable, Int] = ZIO.succeed(0)
  lazy val sumEffects = effects.foldLeft(empty){ (acc, cur) => acc.flatMap(v => cur.map(_ + v))}

  lazy val app:  ZIO[Console with Random with Clock, Throwable, Int] = for{
    sum <- printEffectRunningTime(sumEffects)
    _ <- putStrLn(sum.toString)
  } yield sum

  /**
   * 4.4 Усовершенствуйте программу 4.3 так, чтобы минимизировать время ее выполнения
   */

  lazy val sumEffectsPar= for {
    sum <- Ref.make(0)
    _ <- ZIO.foreachPar((effects)) {ef => ef.flatMap(i => sum.update(_ + i))}
    value <- sum.get
  } yield (value)

  lazy val appSpeedUp = for{
    sum <- printEffectRunningTime(sumEffectsPar)
    _ <- putStrLn(sum.toString)
  } yield sum


  /**
   * 5. Оформите ф-цию printEffectRunningTime разработанную на занятиях в отдельный сервис, так чтобы ее
   * молжно было использовать аналогично zio.console.putStrLn например
   */

  type EffectRunTimeService = Has[EffectRunTimeService.Service]
  
 // @accessible
  object EffectRunTimeService{

    trait Service{
      def printRunTime[R, E, A](eff: ZIO[R, E, A]): ZIO[Console with Clock with R, E, A]
    }

    class ServiceImpl() extends Service {
      override def printRunTime[R, E, A](eff: ZIO[R, E, A]): ZIO[Console with Clock with R, E, A] = for{
        start <-  zio.clock.currentTime(TimeUnit.SECONDS)
        r <- eff
        end <- zio.clock.currentTime(TimeUnit.SECONDS)
        _ <- zio.console.putStrLn(s"Running time ${end - start}")
      } yield r
    }

    val live = ZLayer.succeed( new ServiceImpl())

    def printRunTime[R, E, A](eff: ZIO[R, E, A]): ZIO[EffectRunTimeService with Console with Clock with R, E, A] =
        ZIO.accessM(_.get.printRunTime(eff))
  }


   /**
     * 6.
     * Воспользуйтесь написанным сервисом, чтобы созадть эффект, который будет логировать время выполнения прогаммы из пункта 4.3
     *
     * 
     */

  lazy val appWithTimeLogg = for{
    sum <- EffectRunTimeService.printRunTime(sumEffects)
    _ <- putStrLn(sum.toString)
  } yield sum

  /**
    * 
    * Подготовьте его к запуску и затем запустите воспользовавшись ZioHomeWorkApp
    */

  val appEnv: ZLayer[Any, Nothing, EffectRunTimeService] = EffectRunTimeService.live

  lazy val runApp = appWithTimeLogg.provideSomeLayer[Console with Clock with Random](appEnv)
  
}
