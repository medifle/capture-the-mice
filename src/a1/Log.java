package a1;

public class Log {
  private static int ERROR = 1;
  private static int INFO = 2;
  private static int DEBUG = 3;

  private static int level = 2;

  public static void e(String tag, String content) {
    if (level >= ERROR) {
      System.out.println(tag + ": " + content);
    }
  }

  public static void i(String tag, String content) {
    if (level >= INFO) {
      System.out.println(tag + ": " + content);
    }
  }

  public static void d(String tag, String content) {
    if (level >= DEBUG) {
      System.out.println(tag + ": " + content);
    }
  }
}
