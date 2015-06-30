package nz.ubermouse.bookkeeper.Main

import java.io.File

import sys.process._

object GitIntegration {
  def integrate() {
    Bookkeeper.after("add", postAdd)
    Bookkeeper.after("create", postCreate)
  }

  def postAdd(args: Array[String]): Unit = {
    val message = s"Transaction: Balance of account ${args.head} changed by ${args(1)}"
    commit(message)
  }

  def postCreate(args: Array[String]): Unit = {
    val message = s"New account created: ${args.head}"

    addUntrackedFiles
    commit(message)
  }

  private def gitRoot  = {
    val projectRoot = new File(".")
    new File(projectRoot.getAbsoluteFile, "records")
  }

  private def commit(message: String) = {
    println(Process(Seq("git", "commit", "-am", message), gitRoot) !)
  }

  private def addUntrackedFiles: Unit = {
    val command = Process(Seq("git", "add", "."), gitRoot)
    println(command !)
  }
}
