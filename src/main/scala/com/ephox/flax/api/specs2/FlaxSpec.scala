package com.ephox.flax
package api.specs2

import api.action.Action.noop
import api.action.Action
import api.elem.Driver.driverForBrowser
import api.elem.{Browser, Driver}
import RunAsResult.runAsResult
import com.ephox.flax.api.action.FlaxActions.close
import org.specs2.execute.Result
import org.specs2.specification.{AfterAll, BeforeAfterEach}

/**
  * specs2 mixin for Flax tests.
  *
  * Usage:
  *
  * {{{
  *   trait MyFlax extends FlaxSpec {
  *     override def curBrowser: Browser = Firefox
  *
  *     //optional
  *     override def beforeAllAction: Action[Unit] = ...
  *
  *     //optional
  *     override def afterAllAction: Action[Unit] = ...
  *   }
  * }}}
  *
  * {{{
  * class MySpec extends Specification with MyFlax {
  *   sequential
  *   ...
  * }
  * }}}
  *
  * This is mainly an example - you may wish to integrate into specs2 differently.
  */
trait FlaxSpec extends AfterAll with BeforeAfterEach {

  def curBrowser: Browser

  def beforeAllAction: Action[Unit] = noop

  def afterAllAction: Action[Unit] = close

  def beforeEachAction: Action[Unit] = noop

  def afterEachAction: Action[Unit] = noop

  override final def afterAll(): Unit =
    FlaxSpec.unload(afterAllAction)

  override final def before(): Unit = {
    val driver = FlaxSpec.load(curBrowser, beforeAllAction)
    beforeEachAction.runOrThrow(driver)
  }

  override final def after(): Unit = {
    val driver = FlaxSpec.load(curBrowser, afterAllAction)
    afterEachAction.runOrThrow(driver)
  }

  implicit final def runTest[A](action: Action[A]): Result = {
    implicit val driver: Driver = FlaxSpec.get
    runAsResult(action)
  }
}

private[flax] object FlaxSpec {
  // Argh! Mutation! I blame Specs2.

  private var driver: Option[Driver] = None

  def load[T](b: Browser, a: Action[T]): Driver =
    synchronized {
      if (driver.isEmpty) {
        val d = driverForBrowser(b)
        a runOrThrow d
        driver = Some(d)
      }
      driver.get
    }

  def get[T]: Driver = synchronized { driver.get }

  def unload[T](a: Action[T]): Unit =
    synchronized {
      driver foreach a.runOrThrow
      driver = None
    }
}