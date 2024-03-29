package portingle.kmtronic


/**this code assumes you have a KMTronic device hooked up as the virtual port com5:

If you don't have the device plugged in then you'll get a runtime error relating to com5: not being found.

Cycles through a binary count up from 0-3 using the two relays as bit 1 and 2.
 */
object RelayDemo extends App {

  val relayInstance = 1
  val comPortNumber = 5
  val numberOfRelays = 2

  val relays = new TwoPortKMtronicRelay(new WindowsComPort("com5:"))

  try {
    relays.debug(true)
    relays.open()

    relays(1).powerOff()
    relays(2).powerOff()
  } catch {
    case e => {
      println("->" + e)
      throw e
    }
  }
  var count = 0

  while (true) {
    val relayNum = 1 + (count % 2)
    try {
      val relay = relays(relayNum)

      println("current state : powered = " + relay.isPowered)

      if ((count / 2) % 2 == 0) {
        println("turn relay " + relayNum + " on")
        relay.powerOn()
        assert(true == relay.isPowered, "expected relay to be on")
      } else {
        println("turn relay " + relayNum + " off")
        relay.powerOff()
        assert(false == relay.isPowered, "expected relay to be off")
      }
    }
    catch {
      case e => {
        println("error ->" + e)
        try {
          relays.close()
          relays.open()
        } catch {
          case _ =>
            println("error during close/reopen -> " + e)
        }
      }
    }
    Thread.sleep(500)

    count += 1
  }
}
