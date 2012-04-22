package portingle.kmtronic

private class TwoPortKMtronicRelayInstance(parent: TwoPortKMtronicRelay,
                              relayInstance: Int) extends RelayInstance with RelayConsts {
  def set(powerOn: Boolean) {
    powerOn match {
      case true => powerOn
      case _ => powerOff
    }
  }

  def powerOn() {
    parent.sendBytes(PrefixByte, relayInstance.asInstanceOf[Byte], CommandSwitchOn)
  }

  def powerOff() {
    parent.sendBytes(PrefixByte, relayInstance.asInstanceOf[Byte], CommandSwitchOff)
  }

  def isPowered: Boolean = {
    parent.sendBytes(PrefixByte, relayInstance.asInstanceOf[Byte], CommandStatusRequest)

    val buf = parent.readBytes(SizeOfStatusResponse)
    verifyLeadBytesAreValidStatusResponse(buf, relayInstance)

    buf(2) == CommandSwitchOn
  }

  private def verifyLeadBytesAreValidStatusResponse(buf: Array[Byte], relayInstance: Int) {
    if (buf(0) != PrefixByte) {
      throw new RuntimeException("invalid response from relay: expected first byte to be 0x%02X but got %s".format(PrefixByte, parent.toString(buf)))
    }
    if (buf(1) != relayInstance) {
      throw new RuntimeException("invalid response from relay: expected second byte to be 0x%02X but got %s".format(relayInstance, parent.toString(buf)))
    }
  }
}
