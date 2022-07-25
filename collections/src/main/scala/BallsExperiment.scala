package src.main.scala

case class BallsExperiment() {
  //true = белый шар
  //false = черный шар
  val box: LazyList[Boolean] = LazyList(true, true,true, false, false)
  //Найти вероятность появления белого шара при втором испытании (событие В),
  // если при первом испытании был извлечен черный шар (событие А).
  // создаем ящик к котором уже нет одного черного шара,
  // т.к. считаем вероятность наступления второго события при условии что первое наступило
  def experiment(): Boolean = {
    val box: LazyList[Boolean] = LazyList(true, true,true, false, false)
    val b_index = scala.util.Random.nextInt(box.length)
    box(b_index)
  }

  // Функция из задания(случайного выбора 2х шариков без возвращения (scala.util.Random),
  // возвращать эта функция должна true (если первый шарик был черный, а второй белый)
  // и false (в противном случае))
  def experiment2(): Boolean = {
    val box: LazyList[Boolean] = LazyList(true, true,true, false, false, false)
    val a_index = scala.util.Random.nextInt(box.length)
    (box(a_index)) match {
      case false => {
        val box2 = box.zipWithIndex.filterNot(_._2 == a_index).unzip._1
        val b_index = scala.util.Random.nextInt(box2.length)
        (box2(b_index)) match {
          case true => true
          case _ => false
        }
      }
      case _ => false
    }
  }
}
