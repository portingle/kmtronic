package portingle.kmtronic

trait RelayConsts {
  protected val CommandSwitchOff: Byte = 0x00
  protected val CommandSwitchOn: Byte = 0x01
  protected val CommandStatusRequest: Byte = 0x03
  protected val SizeOfStatusResponse = 3
  protected val PrefixByte: Byte = 0xff.asInstanceOf[Byte]
}
