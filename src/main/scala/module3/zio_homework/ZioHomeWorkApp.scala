package module3.zio_homework

import module3.zio_homework
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.{ExitCode, URIO}

object ZioHomeWorkApp extends zio.App {
  override def run(args: List[String]): URIO[Clock with Random with Console, ExitCode] =
//    zio_homework.guessProgram.exitCode
//    zio_homework.doWhile(guessProgram2).exitCode
//    zio_homework.loadConfigOrDefault.exitCode
//    zio_homework.eff.exitCode
//    zio_homework.app.exitCode
    zio_homework.appSpeedUp.exitCode
}
