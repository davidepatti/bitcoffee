package Tests;

public class Test {
    String name;
    private static int testn = 0;
    private static int subtestn = 0;

    private static String current_test;

    public static void check(String subtest_name, String desc, Object target, Object result) {
        subtestn++;
        System.out.println("---------------------------------------------------");
        System.out.println("--> [ Starting Subtest "+testn+"."+subtestn+": "+subtest_name +" ]");
        System.out.println();
        System.out.println("[Description]\n "+desc);
        System.out.println("---------------------------------------------------");
        System.out.println("\tTarget: "+target);
        System.out.println("\tResult: "+result);

        boolean failed = !(target.equals(result));
        if (failed) {
            System.out.println("\t-> TEST FAILED! "+current_test+"/"+subtest_name);
            System.exit(-1);
            System.out.println("---------------------------------------------------");
        }
        else
            System.out.println("\t-> [ TEST OK: "+current_test+"/"+subtest_name+" ]");
    }

    public static void __BEGIN_TEST(String test) {
        current_test = test;
        subtestn = 0;
        testn++;
        System.out.println("********************************************************");
        System.out.println("  STARTING TEST SET n."+testn+": "+current_test);
        System.out.println("********************************************************");
    }
    public static void __END_TEST() {
        //System.out.println("\n ***************** END_TEST["+current_test+"]**********");
    }
}
