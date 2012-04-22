package portingle.kmtronic

class WindowsComPort(comPortNumber: Int) extends ComPort {

  override def setMode(baud: Int, parity: Boolean, dataBits: Int, stopBits: Int) {

    val command = Array[String](
      "cmd.exe", "/c",
      "start", "/min",
      "mode.com", portName,
      "baud=" + baud,
      "parity=" + (if (parity) 'y' else 'n'),
      "data=" + dataBits,
      "stop=" + stopBits
    )
    println("exec : "  + command.mkString(" "))

    try {
      val p = Runtime.getRuntime().exec(command)

      val exitCode = p.waitFor()
      if (exitCode != 0) {
        throw new RuntimeException("Error executing command non-zero return value " + exitCode + ": " + command.mkString(" "))
      }
    } catch {
      case e: Exception => throw new RuntimeException("Error executing command: " + command.mkString(" "), e)
    }
    
    Thread.sleep(1000)
  }

  override def portName = "com" + comPortNumber
}
