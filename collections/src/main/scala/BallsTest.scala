package src.main.scala

object BallsTest {
  def main(args: Array[String]): Unit = {
    val iteration = 100000
    val explist: List[BallsExperiment] = (for(i <- 1 to iteration) yield new BallsExperiment).toList
    val res1 = for (e <- explist) yield e.experiment()
    val t: Float = res1.count(_ == true)
    println(t/iteration) // вероятность 3/5 что второй шар был белым если первый черный

    val res2 = for (e <- explist) yield e.experiment2()
    val t2: Float = res2.count(_ == true)
    println(t2/iteration) //вероятность 1/3 что первый шар был черным а второй белым
  }
}
