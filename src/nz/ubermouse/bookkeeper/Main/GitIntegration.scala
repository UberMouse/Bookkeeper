package nz.ubermouse.bookkeeper.Main

import java.io.File

import sys.process._

object GitIntegration {
  def integrate() {
    Bookkeeper.after("add", createCommitForTransaction)
    Bookkeeper.after("create", createCommitForNewAccount)
  }

  def createCommitForTransaction(args: Array[String]): Unit = {
    val message = s"Transaction: Balance of account ${args.head} changed by ${args(1)}"
    commit(message)
  }

  def createCommitForNewAccount(args: Array[String]): Unit = {
    val message = s"New account created: ${args.head}"

    addUntrackedFiles
    commit(message)
  }

  private def gitRoot  = {
    val projectRoot = new File(".")
    new File(projectRoot.getAbsoluteFile, "records")
  }

  private def commit(message: String) = {
    val command = Process(Seq("git", "commit", "-am", message), gitRoot)
    println(command !)
  }

  private def addUntrackedFiles: Unit = {
    val command = Process(Seq("git", "add", "."), gitRoot)
    println(command !)
  }
}
