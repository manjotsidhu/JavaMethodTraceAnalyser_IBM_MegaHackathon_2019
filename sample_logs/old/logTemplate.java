import com.ibm.jvm.Trace;
import java.util.Arrays;

public class logTemplate {
	int method1Itr, method2Itr, method3Itr, method4Itr, method5Itr;
	int method1Delay, method2Delay, method3Delay, method4Delay, method5Delay;

    static int handle;
    static String[] templates;

	logTemplate (int method1Itr, int method2Itr, int method3Itr, int method4Itr, int method5Itr, int method1Delay, int method2Delay, int method3Delay, int method4Delay, int method5Delay) {
		this.method1Itr = method1Itr;
		this.method1Delay = method1Delay;
		this.method2Itr = method2Itr;
		this.method2Delay = method2Delay;
		this.method3Itr = method3Itr;
		this.method3Delay = method3Delay;
		this.method4Itr = method4Itr;
		this.method4Delay = method4Delay;
		this.method5Itr = method5Itr;
		this.method5Delay = method5Delay;
	}

	void run() {
		for (int i = 0; i < method1Itr; i++)
			method1();
	
		for (int i = 0; i < method2Itr; i++)
			method2();
		
		for (int i = 0; i < method3Itr; i++)
			method3();
		
		for (int i = 0; i < method4Itr; i++)
			method4();

		for (int i = 0; i < method5Itr; i++)
			method5();
	}

	void method1 () {
		Trace.trace(handle, 0, "method1");
		try {
            Thread.sleep(method1Delay);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("Greetings from method 1!");
		Trace.trace(handle, 1, "method1");
	}

	void method2 () {
		Trace.trace(handle, 0, "method2");
		try {
            Thread.sleep(method2Delay);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("Greetings from method 2!");
        Trace.trace(handle, 1, "method2");
	}

	void method3 () {
		Trace.trace(handle, 0, "method3");
		try {
            Thread.sleep(method3Delay);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("Greetings from method 3!");
        Trace.trace(handle, 1, "method3");
	}

	void method4() {
		Trace.trace(handle, 0, "method4");
		try {
            Thread.sleep(method4Delay);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("Greetings from method 4!");
		Trace.trace(handle, 1, "method4");
	}

	void method5() {
		Trace.trace(handle, 0, "method5");
		try {
            Thread.sleep(method5Delay);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("Greetings from method 5!");
		Trace.trace(handle, 1, "method5");
	}

	public static void main(String[] args) {
		templates = new String[5];
        templates[0] = Trace.ENTRY + "%s"; // method
        templates[1] = Trace.EXIT + "%s"; // method
        templates[2] = Trace.EVENT + "Event id %d, text = %s";
        templates[3] = Trace.EXCEPTION + "Exception: %s";
        templates[4] = Trace.EXCEPTION_EXIT + "Exception exit from %s method";

        // Register a trace application called logTemplate
        handle = Trace.registerApplication("logTemplate", templates);

        // trace points
        Trace.set("iprint=logTemplate");

        // Trace something....
        Trace.trace(handle, 2, 1, "Trace initialized");

		logTemplate myLog = new logTemplate(Integer.valueOf(args[0]),Integer.valueOf(args[1]),Integer.valueOf(args[2]),Integer.valueOf(args[3]),Integer.valueOf(args[4]),Integer.valueOf(args[5]),Integer.valueOf(args[6]),Integer.valueOf(args[7]),Integer.valueOf(args[8]),Integer.valueOf(args[9]));
		myLog.run();

	}
}