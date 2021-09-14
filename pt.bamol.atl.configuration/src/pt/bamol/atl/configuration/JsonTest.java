package pt.bamol.atl.configuration;

import com.google.gson.Gson;

public class JsonTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String payload = new Gson().toJson("Hello \"World\"");
		
		String java1 = new Gson().fromJson(payload, String.class);

		String payload2 = new Gson().toJson("Hello \'World\'");

		String java2 = new Gson().fromJson(payload2, String.class);
		
		String payload3 = payload + payload2;

		
	}

}
