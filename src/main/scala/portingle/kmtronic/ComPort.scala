package portingle.kmtronic

trait ComPort {

  def setMode(baud: Int, parity: Boolean, dataBits: Int, stopBits: Int)

  def portName: String
}
