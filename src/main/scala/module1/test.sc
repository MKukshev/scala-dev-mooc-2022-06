import src.main.scala.BallsExperiment

val box: List[Boolean] = List(true, true,true, false, false, false)
val box_r2: List[Boolean] = List(true, true,true, false, false)
val r = scala.util.Random

//val res = for (i <- 1 to 10) yield box(r.nextInt(6))
//println(res)

//val res2 = for (i <- 1 to 10) yield box.zipWithIndex.filter(p => p._2 !=  r.nextInt(6)).map(p => p._1)(r.nextInt(5))
//println(res2)

//val i = r.nextInt(5)
//box.zipWithIndex.filterNot(_._2 == 1).unzip._1

def experiment(box: List[Boolean]): Boolean = {
  val a_index = r.nextInt(box.length)
  val a_ball = box(a_index)

  val box2 = box.zipWithIndex.filterNot(_._2 == a_index).unzip._1
  val b_index = r.nextInt(box2.length)
  val b_ball = box2(b_index)
  if((!a_ball) && (b_ball)) true
  else false
}

def experiment2(box: List[Boolean]): Boolean = {
  val b_index = r.nextInt(box_r2.length)
  val b_ball = box(b_index)
  b_ball
}
var  expCount = 10000
val res3 = for (i <- 1 to expCount) yield experiment2(box_r2)


val t: Float = res3.count(_ == true)

println(t/expCount)

//val explist: List[BallsExperiment] = for (i <- 1 to expCount) List(ВallsExperiment:: explist)

//println(res3.count(_ == false))
//println(s"fold result: ${res3.fold(0)((z,i) => z + i)}")
