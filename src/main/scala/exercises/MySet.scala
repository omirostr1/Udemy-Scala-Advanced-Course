package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  /*
    Exercise - implement a functional set
   */

  def apply(elem: A): Boolean =
    contains(elem)

  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A] // union

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  /*
    EXERCISE
    - removing an element
    - intersection with another set
    - difference with another set
   */

  def -(elem: A): MySet[A]
  def &(anotherSet: MySet[A]): MySet[A] // intersection
  def --(anotherSet: MySet[A]): MySet[A] // difference

  /*

   */
}

class EmptySet[A] extends MySet[A] {
  def contains(elem: A): Boolean = false

  def +(elem: A): MySet[A] = new NonEmptySet[A](elem, EmptySet.this)

  def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  def map[B](f: A => B): MySet[B] = new EmptySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  def filter(predicate: A => Boolean): MySet[A] = this

  def foreach(f: A => Unit): Unit = ()

  // part 2

  def -(elem: A): MySet[A] = this

  def &(anotherSet: MySet[A]): MySet[A] = this

  def --(anotherSet: MySet[A]): MySet[A] = this
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {

  def contains(elem: A): Boolean =
    elem == head || tail.contains(elem)

  def +(elem: A): MySet[A] =
    if (this contains(elem)) this
    else new NonEmptySet[A](elem, this)

  def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  /*
    [1 2 3] ++ [4 5] =
    [2 3] ++ [4 5] + [1] =
    [3] ++ [4 5] + [1] + [2] =
    [] ++ [4 5] + [1] + [2] + [3]=
    [4 5] + [1] + [2] + [3]
   */

  def map[B](f: A => B): MySet[B] = (tail map f) + f(head)

  def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)

  def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  // part 2

  def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail - elem + head

  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet.contains) // intercection = filtering!

  def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet(x))
}

object MySet {
  /*
    val s = MySet(1, 2, 3) = buildSet(seq(1, 2, 3), [])
    = buildSet(seq(2, 3), [] + 1)
    = buildSet(seq(3), [1] + 2)
    = buildSet(seq(), [1, 2] + 3)
    = [1, 2, 3]
   */
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values.toSeq, new EmptySet[A])
  }
}

object MySetPlayground extends App {
  val a = MySet(1, 2, 3, 4)
  a + 5 ++ MySet(-1, -2) + 3 flatMap (x => MySet(x, x * 10)) filter(_ % 2 == 0) foreach println
}