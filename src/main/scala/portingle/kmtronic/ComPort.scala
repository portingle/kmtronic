package portingle.kmtronic

trait ComPort {
  /** parity = [N]one / [O]dd / [E]ven */
  def setMode(baud: Int, parity: Char, dataBits: Int, stopBits: Int)

  def portName: String
}
