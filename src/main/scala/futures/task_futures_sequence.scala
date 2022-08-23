package futures

import HomeworksUtils.TaskSyntax

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

    val full: Future[List[Try[A]]] = Future.sequence(futures.map(_.transform(Success(_))))
    val success: Future[List[A]] = ???
    val throwable: Future[List[Throwable]] = ???
    success zip throwable
/*
    val success: Future[List[A]] = Future.sequence {
      full.map(_.collect {
        case Success(a) => a
      })
    }

    val throwable: Future[List[Throwable]] = Future.sequence(full.map(_.collect {
      case Failure(a) => a
    }))

    success zip throwable

 */
/*
    val full: Future[List[A]] = Future.sequence(futures.map(a => {
      val p = Promise[A]
      a.onComplete {
        case Failure(exception) => p.failure(exception)
        case Success(value) => p.success(value)
      }
      p.future
    }))
    val successes: Future[List[A]] = full.map(_.collect { case Success(x) => x })
    val failures: Future[List[Throwable]] = full.map(_.collect { case Failure(x) => x })
    successes zip failures
*/

  }


}