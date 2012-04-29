package portingle.kmtronic;

// http://www.codeproject.com/Articles/3061/Creating-a-Serial-communication-on-Win32
//        if (WriteFile(handlePort_,   // handle to file to write to
//                outputData,              // pointer to data to write to file
//                sizeBuffer,              // number of bytes to write
//                &length,NULL) == 0)      // pointer to number of bytes written
//        {
//            AfxMessageBox("Reading of serial communication has problem.");
//            return FALSE;
//        }

import com.sun.deploy.security.BadCertificateDialog;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;

import static com.sun.jna.platform.win32.WinNT.GENERIC_ALL;
import static com.sun.jna.platform.win32.WinNT.OPEN_EXISTING;

public class WindowsJNAComPort {

    // http://www.haskell.org/ghc/docs/latest/html/libraries/Win32-2.2.2.0/src/System-Win32-File.html
    static int EXCLUSIVE_ACCESS = 0;
    public static final int GENERIC_READ = 0x80000000;
    public static final int GENERIC_WRITE = 0x40000000;
    static private String comPort = "COM5";
    public static final int FILE_FLAG_OVERLAPPED = 0x40000000;

    public static void d() {

        MyKernel32 kernel32 = (MyKernel32) Native.loadLibrary("kernel32", MyKernel32.class, W32APIOptions.UNICODE_OPTIONS);

        WinNT.HANDLE comPortHandle = kernel32.CreateFile("com5:", GENERIC_READ | GENERIC_WRITE, EXCLUSIVE_ACCESS, null, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, null);
        if (comPortHandle == WinBase.INVALID_HANDLE_VALUE) {
            System.out.println("FAILED COM OPEN");
            printLastError(kernel32);
        }

        printComState(kernel32, comPortHandle);

        setComPortMode(kernel32, comPortHandle);

    }

    private static void setComPortMode(MyKernel32 kernel32, WinNT.HANDLE comPortHandle) {
        DCB dcb = new DCB();
        int x = 0;

        x = kernel32.BuildCommDCB("baud=9600 parity=N data=8 stop=1", dcb);
        checkError(kernel32, x);

        x = kernel32.SetCommState(comPortHandle, dcb);
        checkError(kernel32, x);
    }

    // doesn't report correctly ??
    private static void printComState(MyKernel32 kernel32, WinNT.HANDLE comPortHandle) {
        DCB dcb = new DCB();
        dcb.DCBlength = new WinDef.DWORD(dcb.size());
        int x;

        x = kernel32.BuildCommDCB("baud=9600 parity=N data=8 stop=1", dcb);
        checkError(kernel32, x);

        dcb.DCBlength = new WinDef.DWORD(dcb.size());
        x = kernel32.GetCommState(comPortHandle, dcb);
        checkError(kernel32, x);

        System.out.println("baud " + dcb.BaudRate);
        System.out.println("parity " + dcb.Parity);
        System.out.println("stopbits " + dcb.StopBits);
        System.out.println("bytesize " + dcb.ByteSize);
    }

    private static void checkError(MyKernel32 kernel32, int x) {
        if (x != 1) {
            System.out.println("RET CODE = " + x);
            printLastError(kernel32);
        }
    }

    private static void printLastError(MyKernel32 kernel32) {
        int errno = kernel32.GetLastError();
        System.out.println("win error " + errno);
    }

    public static void main(String[] a) {
        d();
    }


    public static class DCB extends com.sun.jna.Structure {
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
        public byte Parity;
        public byte StopBits;
        public char XonChar;
        public char XoffChar;
        public char ErrorChar;
        public char EofChar;
        public char EvtChar;
        public com.sun.jna.platform.win32.WinDef.WORD wReserved1;
    }


    interface MyKernel32 extends com.sun.jna.win32.StdCallLibrary {
        int BuildCommDCB(
                String defn,
                DCB lpDCB
        );

        int GetCommState(
                com.sun.jna.platform.win32.WinNT.HANDLE hFile,
                DCB lpDCB
        );

        int SetCommState(
                com.sun.jna.platform.win32.WinNT.HANDLE hFile,
                DCB lpDCB
        );

        com.sun.jna.Pointer LocalFree(com.sun.jna.Pointer pointer);

        com.sun.jna.Pointer GlobalFree(com.sun.jna.Pointer pointer);

        com.sun.jna.platform.win32.WinDef.HMODULE GetModuleHandle(java.lang.String s);

        void GetSystemTime(com.sun.jna.platform.win32.WinBase.SYSTEMTIME systemtime);

        int GetTickCount();

        int GetCurrentThreadId();

        com.sun.jna.platform.win32.WinNT.HANDLE GetCurrentThread();

        int GetCurrentProcessId();

        com.sun.jna.platform.win32.WinNT.HANDLE GetCurrentProcess();

        int GetProcessId(com.sun.jna.platform.win32.WinNT.HANDLE handle);

        int GetProcessVersion(int i);

        boolean GetExitCodeProcess(com.sun.jna.platform.win32.WinNT.HANDLE handle, com.sun.jna.ptr.IntByReference intByReference);

        boolean TerminateProcess(com.sun.jna.platform.win32.WinNT.HANDLE handle, int i);

        int GetLastError();

        void SetLastError(int i);

        int GetDriveType(java.lang.String s);

        int FormatMessage(int i, com.sun.jna.Pointer pointer, int i1, int i2, com.sun.jna.ptr.PointerByReference pointerByReference, int i3, com.sun.jna.Pointer pointer1);

        int FormatMessage(int i, com.sun.jna.Pointer pointer, int i1, int i2, java.nio.Buffer buffer, int i3, com.sun.jna.Pointer pointer1);

        com.sun.jna.platform.win32.WinNT.HANDLE CreateFile(java.lang.String s, int i, int i1, com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES security_attributes, int i2, int i3, com.sun.jna.platform.win32.WinNT.HANDLE handle);

        boolean CreateDirectory(java.lang.String s, com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES security_attributes);

        boolean ReadFile(com.sun.jna.platform.win32.WinNT.HANDLE handle, java.nio.Buffer buffer, int i, com.sun.jna.ptr.IntByReference intByReference, com.sun.jna.platform.win32.WinBase.OVERLAPPED overlapped);

        com.sun.jna.platform.win32.WinNT.HANDLE CreateIoCompletionPort(com.sun.jna.platform.win32.WinNT.HANDLE handle, com.sun.jna.platform.win32.WinNT.HANDLE handle1, com.sun.jna.Pointer pointer, int i);

        boolean GetQueuedCompletionStatus(com.sun.jna.platform.win32.WinNT.HANDLE handle, com.sun.jna.ptr.IntByReference intByReference, com.sun.jna.ptr.ByReference byReference, com.sun.jna.ptr.PointerByReference pointerByReference, int i);

        boolean PostQueuedCompletionStatus(com.sun.jna.platform.win32.WinNT.HANDLE handle, int i, com.sun.jna.Pointer pointer, com.sun.jna.platform.win32.WinBase.OVERLAPPED overlapped);

        int WaitForSingleObject(com.sun.jna.platform.win32.WinNT.HANDLE handle, int i);

        int WaitForMultipleObjects(int i, com.sun.jna.platform.win32.WinNT.HANDLE[] handles, boolean b, int i1);

        boolean DuplicateHandle(com.sun.jna.platform.win32.WinNT.HANDLE handle, com.sun.jna.platform.win32.WinNT.HANDLE handle1, com.sun.jna.platform.win32.WinNT.HANDLE handle2, com.sun.jna.platform.win32.WinNT.HANDLEByReference handleByReference, int i, boolean b, int i1);

        boolean CloseHandle(com.sun.jna.platform.win32.WinNT.HANDLE handle);

        boolean ReadDirectoryChangesW(com.sun.jna.platform.win32.WinNT.HANDLE handle, com.sun.jna.platform.win32.WinNT.FILE_NOTIFY_INFORMATION file_notify_information, int i, boolean b, int i1, com.sun.jna.ptr.IntByReference intByReference, com.sun.jna.platform.win32.WinBase.OVERLAPPED overlapped, com.sun.jna.platform.win32.Kernel32.OVERLAPPED_COMPLETION_ROUTINE overlapped_completion_routine);

        int GetShortPathName(java.lang.String s, char[] chars, int i);

        com.sun.jna.Pointer LocalAlloc(int i, int i1);

        boolean WriteFile(com.sun.jna.platform.win32.WinNT.HANDLE handle, byte[] bytes, int i, com.sun.jna.ptr.IntByReference intByReference, com.sun.jna.platform.win32.WinBase.OVERLAPPED overlapped);

        com.sun.jna.platform.win32.WinNT.HANDLE CreateEvent(com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES security_attributes, boolean b, boolean b1, java.lang.String s);

        boolean SetEvent(com.sun.jna.platform.win32.WinNT.HANDLE handle);

        boolean PulseEvent(com.sun.jna.platform.win32.WinNT.HANDLE handle);

        com.sun.jna.platform.win32.WinNT.HANDLE CreateFileMapping(com.sun.jna.platform.win32.WinNT.HANDLE handle, com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES security_attributes, int i, int i1, int i2, java.lang.String s);

        com.sun.jna.Pointer MapViewOfFile(com.sun.jna.platform.win32.WinNT.HANDLE handle, int i, int i1, int i2, int i3);

        boolean UnmapViewOfFile(com.sun.jna.Pointer pointer);

        boolean GetComputerName(char[] chars, com.sun.jna.ptr.IntByReference intByReference);

        com.sun.jna.platform.win32.WinNT.HANDLE OpenThread(int i, boolean b, int i1);

        com.sun.jna.platform.win32.WinNT.HANDLE OpenProcess(int i, boolean b, int i1);

        com.sun.jna.platform.win32.WinDef.DWORD GetTempPath(com.sun.jna.platform.win32.WinDef.DWORD dword, char[] chars);

        com.sun.jna.platform.win32.WinDef.DWORD GetVersion();

        boolean GetVersionEx(com.sun.jna.platform.win32.WinNT.OSVERSIONINFO osversioninfo);

        boolean GetVersionEx(com.sun.jna.platform.win32.WinNT.OSVERSIONINFOEX osversioninfoex);

        void GetSystemInfo(com.sun.jna.platform.win32.WinBase.SYSTEM_INFO system_info);

        void GetNativeSystemInfo(com.sun.jna.platform.win32.WinBase.SYSTEM_INFO system_info);

        boolean IsWow64Process(com.sun.jna.platform.win32.WinNT.HANDLE handle, com.sun.jna.ptr.IntByReference intByReference);

        boolean GlobalMemoryStatusEx(com.sun.jna.platform.win32.WinBase.MEMORYSTATUSEX memorystatusex);

        com.sun.jna.platform.win32.WinDef.DWORD GetLogicalDriveStrings(com.sun.jna.platform.win32.WinDef.DWORD dword, char[] chars);

        boolean GetDiskFreeSpaceEx(java.lang.String s, com.sun.jna.platform.win32.WinNT.LARGE_INTEGER.ByReference byReference, com.sun.jna.platform.win32.WinNT.LARGE_INTEGER.ByReference byReference1, com.sun.jna.platform.win32.WinNT.LARGE_INTEGER.ByReference byReference2);

        boolean DeleteFile(java.lang.String s);

        boolean CreatePipe(com.sun.jna.platform.win32.WinNT.HANDLEByReference handleByReference, com.sun.jna.platform.win32.WinNT.HANDLEByReference handleByReference1, com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES security_attributes, int i);

        boolean SetHandleInformation(com.sun.jna.platform.win32.WinNT.HANDLE handle, int i, int i1);

        int GetFileAttributes(java.lang.String s);

        static interface OVERLAPPED_COMPLETION_ROUTINE extends com.sun.jna.win32.StdCallLibrary.StdCallCallback {

            void callback(int i, int i1, com.sun.jna.platform.win32.WinBase.OVERLAPPED overlapped);
        }
    }


}