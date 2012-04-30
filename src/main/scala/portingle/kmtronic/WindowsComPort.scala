package portingle.kmtronic

import com.sun.jna.win32.W32APIOptions
import java.lang.System
import java.io.IOException
import com.sun.jna.{Pointer, Native}
import java.nio.{CharBuffer, Buffer}
import scala.Array
import com.sun.jna.platform.win32.{WinBase, WinDef, WinNT}
import com.sun.jna.platform.win32.WinNT._

class WindowsComPort(val portName: String) extends ComPort {

  private final var EXCLUSIVE_ACCESS: Int = 0
  private final val GENERIC_READ: Int = 0x80000000
  private final val GENERIC_WRITE: Int = 0x40000000
  private final val FILE_FLAG_OVERLAPPED: Int = 0x40000000

  private val kernel32: MyKernel32 = Native.loadLibrary("kernel32", classOf[MyKernel32], W32APIOptions.UNICODE_OPTIONS).asInstanceOf[MyKernel32]

  def openPort() = {
    val comPortHandle = kernel32.CreateFile(portName, GENERIC_READ | GENERIC_WRITE, EXCLUSIVE_ACCESS, null, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, null)
    if (comPortHandle eq WinBase.INVALID_HANDLE_VALUE) {
      raiseError("opening com port " + portName)
    }
    comPortHandle
  }

  /**parity = [N]one / [O]dd / [E]ven */
  def setMode(baud: Int, parity: Char, dataBits: Int, stopBits: Int) {
    var command = "baud=%d parity=%c data=%d stop=%d".format(baud, parity, dataBits, stopBits)

    val dcb = new DCB
    checkError("BuildCommDCB", kernel32.BuildCommDCB(command, dcb))
    val comPortHandle = openPort()
    try {
      checkError("SetCommState", kernel32.SetCommState(comPortHandle, dcb))
    } finally {
      kernel32.CloseHandle(comPortHandle)
    }
  }

  def printComState() {
    val dcb = new DCB
    dcb.DCBlength = new WinDef.DWORD(dcb.size)

    val comPortHandle = openPort()
    try {
      checkError("GetCommState", kernel32.GetCommState(comPortHandle, dcb))
    } finally {
      kernel32.CloseHandle(comPortHandle)
    }

    System.out.println("baud " + dcb.BaudRate)
    System.out.println("parity " + dcb.Parity)
    System.out.println("stopbits " + dcb.StopBits)
    System.out.println("bytesize " + dcb.ByteSize)
  }

  private def checkError(desc: String, x: Int) {
    if (x != 1) {
      raiseError(desc)
    }
  }

  private def raiseError(desc: String) {
    val str = "failed " + desc + " : " + errorMessage()
    throw new IOException(str)
  }

  private def GetWindowsSystemErrorMessage(iError: Int): String = {
    val LANGUAGE_DEFAULT = 0
    val buf = new Array[Char](255)

    val bb = CharBuffer.wrap(buf);
    val charCount = kernel32.FormatMessage(WinBase.FORMAT_MESSAGE_FROM_SYSTEM
      , null, iError, LANGUAGE_DEFAULT, bb, buf.length, null);
    bb.limit(charCount)
    bb.toString()
  }

  private def errorMessage(): String = {
    val lastError: Int = kernel32.GetLastError
    val r = GetWindowsSystemErrorMessage(lastError)
    if (r.length == 0) {
      "error code " + lastError + " (cannot determine message)"
    } else {
      r
    }
  }

  private trait MyKernel32 extends com.sun.jna.win32.StdCallLibrary {
    def BuildCommDCB(defn: String, lpDCB: DCB): Int

    def GetCommState(hFile: WinNT.HANDLE, lpDCB: DCB): Int

    def SetCommState(hFile: WinNT.HANDLE, lpDCB: DCB): Int

    def GetLastError: Int

    def CreateFile(s: String, i: Int, i1: Int, security_attributes: WinBase.SECURITY_ATTRIBUTES, i2: Int, i3: Int, handle: WinNT.HANDLE): WinNT.HANDLE

    def FormatMessage(dwFlags: Int, lpSource: Pointer, dwMessageId: Int, dwLanguageId: Int, lpBuffer: Buffer, nSize: Int, arguments: Pointer): Int

    def CloseHandle(hObject: WinNT.HANDLE)
  }

}

object WindowsComPortTest extends App {

  val port = new WindowsComPort("com5:")

  port.setMode(9600, 'N', 8, 1)
  port.printComState()

}




