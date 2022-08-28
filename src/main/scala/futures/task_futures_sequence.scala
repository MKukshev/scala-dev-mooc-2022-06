package futures

import HomeworksUtils.TaskSyntax

import scala.{:+, ::}
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

object task_futures_sequence {

  /**
   * В данном задании Вам предлагается реализовать функцию fullSequence,
   * похожую на Future.sequence, но в отличии от нее,
   * возвращающую все успешные и не успешные результаты.
   * Возвращаемое тип функции - кортеж из двух списков,
   * в левом хранятся результаты успешных выполнений,
   * в правово результаты неуспешных выполнений.
   * Не допускается использование методов объекта Await и мутабельных переменных var
   */

  /**
   * @param futures список асинхронных задач
   * @return асинхронную задачу с кортежом из двух списков
   */

  def fullSequence[A](futures: List[Future[A]])
                     (implicit ex: ExecutionContext): Future[(List[A], List[Throwable])] = {

   val empty = Future.successful((List.empty[A], List.empty[Throwable]))

   futures.foldLeft(empty) {  (accum, ft) =>
        for {
          (success,fail)  <- accum
          res <- ft.map( v => (success :+ v, fail)).recover(e => (success, (fail :+ e)))
        } yield res
    }
  }


}