package portingle.kmtronic

import java.io.RandomAccessFile
import java.nio.ByteBuffer

class TwoPortKMtronicRelay(comPort: ComPort) {

  private var rwFile: Option[RandomAccessFile] = None
  private val relays = ((1 to 2).map(new TwoPortKMtronicRelayInstance(this, _)))
  private var debug = false


  def apply(relayInstance: Int): RelayInstance = {
    checkInstanceBounds(relayInstance)
    relays(relayInstance-1)
  }

  def isOpen = rwFile.isDefined

  def open()  {
    setComParams
    fileOpen
  }

  private def fileOpen : RandomAccessFile = {
    val file: RandomAccessFile = new RandomAccessFile(comPort.portName, "rw")
    rwFile = Some(file)
    file
  }

  private def setComParams {
    comPort.setMode(9600, false, 8, 1)
  }

  def close() {
    if (rwFile.isDefined) {
      rwFile.get.close()
    }
    rwFile = None
  }

  def debug(isOn: Boolean) {
    debug = isOn
  }

  private def checkInstanceBounds(relayInstance: Int) {
    if (relayInstance < 1 || relayInstance > relays.length) {
      throw new IllegalArgumentException("invalid relay instance " + relayInstance + " given when " + relays.length + " configured")
    }
  }

  private[kmtronic] def readBytes(expectedBytes: Int): Array[Byte] = {
    val readTimeoutMs = 100
    val timeoutPollIntervalMs = 1

    val f = rwFile.getOrElse(throw new RuntimeException("must call open before reading from relay")).getChannel

    val buffer = ByteBuffer.allocate(expectedBytes)

    def read(remainingTimeoutMs: Int) {
      if (debug) println("Reading ... " )

      if (remainingTimeoutMs <= 0)
        throw new RuntimeException("timeout waiting for relay status : expected " + expectedBytes + " bytes but got " + buffer.position())

      f.read(buffer)

      if (buffer.position() < expectedBytes) {
        Thread.sleep(timeoutPollIntervalMs)
        read(remainingTimeoutMs - timeoutPollIntervalMs)
      }
    }
    read(readTimeoutMs)

    println("Received " + toString(buffer.array()))

    buffer.array()
  }

  private[kmtronic] def toString(buf: Array[Byte]): String = "[" + buf.map("0x%02X".format(_)).mkString(",") + "]"

  private[kmtronic] def sendBytes(data: Byte*) {
    if (debug) println("Sending " + toString(data.toArray))
    close
    val os = fileOpen
    os.write(data.toArray, 0, data.length)
  }
}


