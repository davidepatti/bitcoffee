public class Test<T> {
    String name;
    private boolean failed = false;
    private static int testn = 0;

    public Test(String name) {
        this.name = name;
    }

    public void begin() {
        System.out.println("********************************************************");
        System.out.println("  STARTING TEST SET: "+name);
        System.out.println("********************************************************");

    }
    public void end() {
        //System.out.println(" <<<<<<<<<<< ENDED TEST SET: "+name+"\n");
        System.out.println("_");
    }

    public void check(String subtest_name, String desc, T target, T result) {
        System.out.println("---------------------------------------------------");
        System.out.println("-> Starting Subtest n."+(++testn)+": "+subtest_name);
        System.out.println("---------------------------------------------------");
        System.out.println("[Description]\n "+desc);
        System.out.println("---------------------------------------------------");
        System.out.println("\tTarget: "+target);
        System.out.println("\tResult: "+result);

        failed = !(target.equals(result));
        if (failed) {
            System.out.println("\t-> TEST FAILED: "+name+"/"+subtest_name);
            System.exit(-1);
            System.out.println("---------------------------------------------------");
        }
        else
            System.out.println("\t-> TEST OK: "+name+"/"+subtest_name);
    }

    public void check(String subtest_name, T target, T result) {
        check(subtest_name,"-",target,result);
    }

    public static void __BEGIN_NOTES(String zone_test) {
        System.out.println("---------------------------------------------------");
        System.out.println(" ___________BEGIN_NOTES: "+zone_test);
    }
    public static void __END_NOTES() {
        System.out.println("____________END NOTES__________________");
    }
}
