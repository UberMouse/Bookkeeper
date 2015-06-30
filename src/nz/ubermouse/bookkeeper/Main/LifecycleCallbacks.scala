package nz.ubermouse.bookkeeper.Main

/**
 * Created by taylor on 30/06/15.
 */
trait LifecycleCallbacks {
  trait CallbackType
  case class Before() extends CallbackType
  case class After() extends CallbackType

  type LifecycleCallback = (Array[String]) => Unit
  var lifecycleCallbacks = Map[String, LifecycleCallback]()

  def before(action: String, callback: LifecycleCallback): Unit = {
    lifecycleCallbacks += (s"before_$action" -> callback)
  }

  def after(action: String, callback: LifecycleCallback): Unit = {
    lifecycleCallbacks += (s"after_$action" -> callback)
  }

  def executeCallbacks(args: Array[String], actionName: String, `type`: CallbackType): Unit = {
    val actionTypeSignature = if(`type` == Before()) "before" else "after"
    val callbacksToExecute = lifecycleCallbacks
      .filter{case(action, callback) => action.startsWith(actionTypeSignature)}
      .filter{case(action, callback) => action.contains(actionName)}

    callbacksToExecute.foreach{case(_, callback) => callback(args)}
  }
}
