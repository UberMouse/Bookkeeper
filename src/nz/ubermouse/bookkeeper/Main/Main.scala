package nz.ubermouse.bookkeeper.Main

import java.io.{PrintWriter, FileWriter, File}

import scala.io.Source

object Main extends App {
  val actions = Map(
    "add" -> add _,
    "create" -> create _,
    "balance" -> balance _,
    "balances" -> balances _
  )

  actions(args.head)(args.drop(1))

  def create(args: Array[String]): Unit = {
    val name = args.head

    val balanceFile = new File(s"records/$name.txt")
    balanceFile.getParentFile.mkdirs()
    balanceFile.createNewFile()
  }

  def balance(args: Array[String]): Unit = {
    val target = args.head

    println(s"Balance for $target is ${calculateBalance(target)}")
  }

  def balances(args: Array[String]): Unit = {
    val balanceNames = getAllBalanceNames
    val balancesWithName = balanceNames.map(name => (name, calculateBalance(name)))

    balancesWithName.foreach{case(name, balance) => println(s"$name: $balance")}
  }

  def add(args: Array[String]): Unit = {
    val target = args.head
    val amount = args(1)
    val description = args.drop(2).mkString(" ")

    val balanceFile = new File(s"records/$target.txt")
    if(!balanceFile.exists())
      throw new Exception("Supplied target does not exist")

    val writer = new FileWriter(balanceFile, true)
    writer.write(List(amount, description).mkString(";:;") + System.lineSeparator())
    writer.close()
  }

  def calculateBalance(target: String) = {
    case class Transaction(amount: Double, description: String)

    val balanceFile = new File(s"records/$target.txt")
    val lines = Source.fromFile(balanceFile).getLines()
    val transactions = lines.map{line =>
      val split = line.split(";:;")
      Transaction(split(0).toDouble, split(1))
    }

    transactions.foldLeft(0.0){case(sum, transaction) => sum + transaction.amount}
  }

  def getAllBalanceNames: Array[String] = {
    val balanceDirectory = new File("records")
    val balances = balanceDirectory.listFiles()

    balances.map(_.getName.split("\\.")(0))
  }
}
