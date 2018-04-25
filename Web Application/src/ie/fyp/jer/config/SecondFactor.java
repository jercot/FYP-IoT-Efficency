package ie.fyp.jer.config;

import java.util.Random;

public class SecondFactor {
	public static int generate() {
		return 100000 + new Random().nextInt(900000);
	}
}