import com.ibm.jvm.Trace;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

public class logGen {
	public static String execCmd(String cmd) throws java.io.IOException {
		java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getErrorStream()).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
	
	public static void main(String[] args) {
		try {
			int size = Integer.valueOf(args[0]);
			// Compile logTemplate
			execCmd("./javac logTemplate.java");
			
			for (int i = 1; i <= size; i++) {
				System.out.println("Generating method trace log for logGen " + i + ".fmt");
				ArrayList<Integer> mItr = new ArrayList<>();
				ArrayList<Integer> mDelay = new ArrayList<>();

				String arguments = "";
				for (int j = 0; j < 5; j++) {
					int randInt = ThreadLocalRandom.current().nextInt(1, 50);
					mItr.add(randInt);
					arguments +=  randInt + " ";
				}
				
				for (int j = 5; j < 10; j++) {
					int randInt = ThreadLocalRandom.current().nextInt(1, 50);
					mDelay.add(randInt + 1);
					arguments +=  randInt + " ";
				}

				for (int j = 1; j <= 5; j++) {
					int t = mItr.get(j-1)*mDelay.get(j-1);
 					System.out.println(" logGen" + i + ": Generating method " + j + " with " + mItr.get(j-1) + " iterations and " + t + "ms total.");
				}
								
				execCmd("./java \"-Xtrace:none\" \"-Xtrace:output=logGen" + i + ".trc,maximal=mt,methods={logTemplate.*,jstacktrace},trigger=method{logTemplate.*,jstacktrace}\" logTemplate " + arguments);
				execCmd("./java com.ibm.jvm.TraceFormat logGen" + i + ".trc logGen" + i + ".fmt");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}