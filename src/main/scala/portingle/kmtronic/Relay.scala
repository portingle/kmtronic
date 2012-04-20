package portingle.kmtronic

import java.io.RandomAccessFile
import java.nio.ByteBuffer

class Relay(comPort: ComPort, relayCount: Int) extends RelayConsts {

  private var rwFile: Option[RandomAccessFile] = None
  private val relays = ((1 to relayCount).map(new KMRelayInstance(comPort, _)))

  private class KMRelayInstance(comPort: ComPort, relayInstance: Int) extends RelayInstance {
    def set(powerOn: Boolean) {
      powerOn match {
        case true => powerOn
        case _ => powerOff
      }
    }

    def powerOn() {
      sendBytes(PrefixByte, relayInstance.asInstanceOf[Byte], CommandSwitchOn)
    }

    def powerOff() {
      sendBytes(PrefixByte, relayInstance.asInstanceOf[Byte], CommandSwitchOff)
    }

    def isPowered: Boolean = {
      sendBytes(PrefixByte, relayInstance.asInstanceOf[Byte], CommandStatusRequest)

      val buf = readBytes(SizeOfStatusResponse)
      verifyLeadBytesAreValidStatusResponse(buf, relayInstance)

      buf(2) == CommandSwitchOn
    }
  }

  def apply(relayInstance: Int): RelayInstance = {
    checkInstanceBounds(relayInstance)
    return relays(relayInstance)
  }

  def open() {
    rwFile = Some(new RandomAccessFile(comPort.portName, "rws"))
    comPort.setMode(9600, false, 8, 1)
  }

  def close() {
    if (rwFile.isDefined) rwFile.get.close()
    rwFile = None
  }

  private def verifyLeadBytesAreValidStatusResponse(buf: Array[Byte], relayInstance: Int) {
    if (buf(0) != PrefixByte) {
      throw new RuntimeException("invalid response from relay: expected first byte to be 0x%02X but got %s".format(PrefixByte, toString(buf)))
    }
    if (buf(1) != relayInstance) {
      throw new RuntimeException("invalid response from relay: expected second byte to be 0x%02X but got %s".format(relayInstance, toString(buf)))
    }
  }

  private def checkInstanceBounds(relayInstance: Int) {
    if (relayInstance < 1 || relayInstance > relays.length) {
      throw new IllegalArgumentException("invalid relay instance " + relayInstance + " given when " + relays.length + " configured")
    }
  }

  private def readBytes(expectedBytes: Int): Array[Byte] = {
    val readTimeoutMs = 100
    val timeoutPollIntervalMs = 1

    val f = rwFile.getOrElse(throw new RuntimeException("must call open before reading from relay")).getChannel()

    val buffer = ByteBuffer.allocate(expectedBytes)

    def read(remainingTimeoutMs: Int) {
      if (remainingTimeoutMs <= 0)
        throw new RuntimeException("timeout waiting for relay status : expected " + expectedBytes + " bytes but got " + buffer.position())

      f.read(buffer)

      if (buffer.position() < expectedBytes) {
        Thread.sleep(timeoutPollIntervalMs)
        read(remainingTimeoutMs - timeoutPollIntervalMs)
      }
    }
    read(readTimeoutMs)

    buffer.array()
  }

  private def toString(buf: Array[Byte]): String = "[" + buf.map("0x%02X".format(_)).mkString(",") + "]"

  private def sendBytes(data: Byte*) {
    rwFile.getOrElse(throw new RuntimeException("must call open before writing to relay")).write(data.toArray, 0, data.length)
  }
}
