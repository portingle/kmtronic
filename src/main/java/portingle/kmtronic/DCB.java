package portingle.kmtronic;

public class DCB extends com.sun.jna.Structure {
    public com.sun.jna.platform.win32.WinDef.DWORD DCBlength;
    public com.sun.jna.platform.win32.WinDef.DWORD BaudRate;
    public int fFlags;

    // 26 bytes (13 word) of bit fields
    static public com.sun.jna.platform.win32.WinDef.DWORD fBinary = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fParity = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fOutxCtsFlow = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fOutxDsrFlow = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fDtrControl = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fDsrSensitivity = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fTXContinueOnXoff = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fOutX = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fInX = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fErrorChar = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fNull = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fRtsControl = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fAbortOnError = DWORD(0);
    static public com.sun.jna.platform.win32.WinDef.DWORD fDummy27; // padding

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

    private static com.sun.jna.platform.win32.WinDef.DWORD DWORD(int i) {
        return new com.sun.jna.platform.win32.WinDef.DWORD(i);
    }
}
