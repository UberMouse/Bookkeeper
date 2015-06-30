package nz.ubermouse.bookkeeper.Main
import sys.process._

object GitIntegration {
  def integrate() {
    Bookkeeper.after("add", postAdd)
    Bookkeeper.after("create", postCreate)
  }

  def postAdd(args: Array[String]): Unit = {
    val commitMessage = s"Transaction: Balance of account ${args.head} changed by ${args(1)}"
    Process(s"git commit -am \"$commitMessage\"")
  }

  def postCreate(args: Array[String]): Unit = {

  }
}
