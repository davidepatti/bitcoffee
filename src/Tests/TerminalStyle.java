package Tests;

public final class TerminalStyle {
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String BOLD_CYAN = "\u001B[1;36m";
    private static final String BOLD_YELLOW = "\u001B[1;33m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String DIM = "\u001B[2m";

    private static final boolean ENABLED = detectAnsiSupport();

    private TerminalStyle() {
    }

    public static String bold(String text) {
        return wrap(BOLD, text);
    }

    public static String title(String text) {
        return wrap(BOLD_YELLOW, text);
    }

    public static String cyan(String text) {
        return wrap(CYAN, text);
    }

    public static String yellow(String text) {
        return wrap(YELLOW, text);
    }

    public static String green(String text) {
        return wrap(GREEN, text);
    }

    public static String red(String text) {
        return wrap(RED, text);
    }

    public static String dim(String text) {
        return wrap(DIM, text);
    }

    public static String emphasis(String text) {
        return wrap(BOLD_CYAN, text);
    }

    public static String number(String text) {
        return yellow(text);
    }

    private static String wrap(String prefix, String text) {
        if (!ENABLED) {
            return text;
        }
        return prefix + text + RESET;
    }

    private static boolean detectAnsiSupport() {
        var noColor = System.getenv("NO_COLOR");
        if (noColor != null) {
            return false;
        }

        var forceColor = System.getenv("CLICOLOR_FORCE");
        if (forceColor != null && !forceColor.equals("0")) {
            return true;
        }

        var explicit = System.getenv("BITCOFFEE_FORCE_COLOR");
        if (explicit != null) {
            return explicit.equalsIgnoreCase("true")
                    || explicit.equalsIgnoreCase("yes")
                    || explicit.equals("1");
        }

        var term = System.getenv("TERM");
        if (term == null || term.equalsIgnoreCase("dumb")) {
            return false;
        }

        var os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            return System.getenv("WT_SESSION") != null
                    || System.getenv("ANSICON") != null
                    || System.getenv("ConEmuANSI") != null
                    || "ON".equalsIgnoreCase(System.getenv("ConEmuANSI"));
        }

        return true;
    }
}
