import module1.opt.Option
import module1.list._



val a: Option[Int] = Option(5)
a.printIfAny()

val s: Option[String] = Option("5")
def zipString(x: String, y: String): Option[String] = Option(x + y)

val z = s.zip(Option("3"))
z.printIfAny()

s.zip(Option.None)

val i: Option[Int] = Option(5)
i.zip(Option(3))


val b: Option[Int] = Option.None
b.printIfAny()
a.filter((i: Int) => i % 2 == 0)


val l1 = List(1,2,3,4,5,6)
l1.mkString(";")

val rl = l1.reverse()
rl.mkString(";")

val l2 = l1.map((x: Int) => x * 2)
l2.mkString(";")

val l3 = l2.filter((i: Int) => i > 7)

val l4 = incList(l3)

val ls = List("a","b","c","d")

val ls2 = shoutString(ls)