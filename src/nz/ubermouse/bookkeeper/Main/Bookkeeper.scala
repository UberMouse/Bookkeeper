package nz.ubermouse.bookkeeper.Main

import java.io.{PrintWriter, FileWriter, File}

import scala.io.Source

object Bookkeeper extends App with LifecycleCallbacks {
  case class Transaction(amount: Double, description: String)
  GitIntegration.integrate()

  val actions = Map(
    "credit" -> credit _,
    "debit" -> debit _,
    "create" -> create _,
    "balance" -> balance _,
    "balances" -> balances _,
    "transactions" -> transactions _
  )

  val actionName = args.head
  val actionArguments = args.drop(1)

  executeCallbacks(actionArguments, actionName, Before())
  actions(actionName)(actionArguments)
  executeCallbacks(actionArguments, actionName, After())

  def create(args: Array[String]): Unit = {
    assert(args.length == 1, "Only one argument can be passed")
    val name = args.head

    val balanceFile = new File(s"records/$name.txt")
    balanceFile.getParentFile.mkdirs()
    balanceFile.createNewFile()
  }

  def balance(args: Array[String]): Unit = {
    assert(args.length == 1, "Only one argument can be passed")
    val target = args.head

    println(s"Balance for $target is ${calculateBalance(target)}")
  }

  def balances(args: Array[String]): Unit = {
    val balanceNames = getAllBalanceNames
    val balancesWithName = balanceNames.map(name => (name, calculateBalance(name)))

    balancesWithName.foreach{case(name, balance) => println(s"$name: $balance")}
  }

  def debit(args: Array[String]): Unit = {
    assert(args.length == 2, "Two arguments must be passed")
    val target = args.head
    val amount = args(1)
    val description = args.drop(2).mkString(" ")

    addTransactionForTarget(target, amount, description)
  }

  def credit(args: Array[String]): Unit = {
    assert(args.length == 2, "Two arguments must be passed")
    val target = args.head
    val amount = args(1)
    val description = args.drop(2).mkString(" ")

    val amountWithMinus = if(amount.indexOf('-') == -1) s"-$amount" else amount
    addTransactionForTarget(target, amountWithMinus, description)
  }

  def transactions(args: Array[String]): Unit = {
    assert(args.length == 1, "Only one argument can be passed")
    val target = args.head
    val limit = if(args.length > 1) args(1).toInt else 10

    val transactions = getTransactions(target).take(limit)

    println(s"Transactions for $target")
    transactions.foreach(t => println(s"${t.amount} - ${t.description}"))
  }

  def calculateBalance(target: String) = {
    val transactions = getTransactions(target)
    transactions.foldLeft(0.0){case(sum, transaction) => sum + transaction.amount}
  }

  def getTransactions(target: String) = {

    val balanceFile = new File(s"records/$target.txt")
    val lines = Source.fromFile(balanceFile).getLines()
    lines.filter(_.length != 0).map{line =>
      val split = line.split(";:;")
      Transaction(split(0).toDouble, split(1))
    }
  }

  def getAllBalanceNames: Array[String] = {
    val balanceDirectory = new File("records")
    val balances = balanceDirectory.listFiles().filter(_.isFile)

    balances.map(_.getName.split("\\.")(0))
  }

  def addTransactionForTarget(target: String, amount: String, description: String) = {
    val balanceFile = new File(s"records/$target.txt")
    if(!balanceFile.exists())
      throw new Exception("Supplied target does not exist")

    val descriptionWithDefault = if(description.isEmpty) "N/A" else description
    val writer = new FileWriter(balanceFile, true)
    writer.write(List(amount, descriptionWithDefault).mkString(";:;") + System.lineSeparator())
    writer.close()
  }
}
