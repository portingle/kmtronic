package portingle.kmtronic;

public class DCB extends com.sun.jna.Structure {
    public com.sun.jna.platform.win32.WinDef.DWORD DCBlength;
    public com.sun.jna.platform.win32.WinDef.DWORD BaudRate;
    public int fFlags; // No bit field mapping in JNA so define a flags field and masks for fFlags
    static public com.sun.jna.platform.win32.WinDef.DWORD fBinary;
    static public com.sun.jna.platform.win32.WinDef.DWORD fParity;
    static public com.sun.jna.platform.win32.WinDef.DWORD fOutxCtsFlow;
    static public com.sun.jna.platform.win32.WinDef.DWORD fOutxDsrFlow;
    static public com.sun.jna.platform.win32.WinDef.DWORD fDtrControl;
    static public com.sun.jna.platform.win32.WinDef.DWORD fDsrSensitivity;
    static public com.sun.jna.platform.win32.WinDef.DWORD fTXContinueOnXoff;
    static public com.sun.jna.platform.win32.WinDef.DWORD fOutX;
    static public com.sun.jna.platform.win32.WinDef.DWORD fInX;
    static public com.sun.jna.platform.win32.WinDef.DWORD fErrorChar;
    static public com.sun.jna.platform.win32.WinDef.DWORD fNull;
    static public com.sun.jna.platform.win32.WinDef.DWORD fRtsControl;
    static public com.sun.jna.platform.win32.WinDef.DWORD fAbortOnError;
    static public com.sun.jna.platform.win32.WinDef.DWORD fDummy27;
    public com.sun.jna.platform.win32.WinDef.WORD wReserved;
    public com.sun.jna.platform.win32.WinDef.WORD XonLim;
    public com.sun.jna.platform.win32.WinDef.WORD XoffLim;
    public byte ByteSize;
    public byte Parity;  // 0=None / 1=Odd / 2=Even
    public byte StopBits;
    public char XonChar;
    public char XoffChar;
    public char ErrorChar;
    public char EofChar;
    public char EvtChar;
    public com.sun.jna.platform.win32.WinDef.WORD wReserved1;
}
