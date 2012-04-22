package portingle.kmtronic

trait RelayInstance {

  def powerOn()

  def powerOff()

  def isPowered: Boolean
}
