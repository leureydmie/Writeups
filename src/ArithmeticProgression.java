
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author rpecebou
 * 
 *         Writeup for the Root-Me challenge Arithmetic Progression :
 *         https://www.root-me.org/en/Challenges/Programming/Arithmetic-progression-18
 *
 */

public class ArithmeticProgression {

	static final String CHALLENGE_URL = "http://challenge01.root-me.org/programmation/ch1/";

	static final String RES_URL = "http://challenge01.root-me.org/programmation/ch1/ep1_v.php?result=";

	public static void main(String[] args) {
		try {
			HttpContentGetter contentGetter = new HttpContentGetter();
			String content = contentGetter.get(CHALLENGE_URL);
			List<BigInteger> parsedContent = new Parser().parsePageContent(content);
			new ArithmeticProgressionCalculator();
			BigInteger result = ArithmeticProgressionCalculator.calculate(parsedContent);
			System.out.println(content);
			System.out.println(parsedContent);
			System.out.println(result);
			Thread.sleep(1000);
			System.out.println(contentGetter.get(RES_URL + result.toString()));
		} catch (Exception e) {

		}
	}
}

class HttpContentGetter {

	static final String COOKIES_HEADER = "Set-Cookie";

	CookieManager _cookieManager;

	public HttpContentGetter() {
		_cookieManager = new CookieManager();
	}

	public String get(String url) throws Exception {
		StringBuilder builder = new StringBuilder();
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("GET");
		/*
		 * Set cookie if any
		 */
		if (_cookieManager.getCookieStore().getCookies().size() > 0) {
			conn.setRequestProperty("Cookie", _cookieManager.getCookieStore().getCookies().get(0).toString());
		}
		/*
		 * Read content
		 */
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		for (String line; (line = rd.readLine()) != null;) {
			builder.append(line);
		}
		/*
		 * Get cookie update
		 */
		addCookie(conn);
		rd.close();
		return builder.toString();
	}

	public void addCookie(HttpURLConnection connection) {
		Map<String, List<String>> headerFields = connection.getHeaderFields();
		List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
		if (cookiesHeader != null) {
			for (String cookie : cookiesHeader) {
				_cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
			}
		}
	}

}

class Parser {

	static final String SEPARATOR = "sub";

	static final int[] INDEXES = new int[] { 2, 4, 6, 7 };

	public List<BigInteger> parsePageContent(String content) {
		List<String> splittedContent = Arrays.asList(content.split(SEPARATOR));
		List<String> sanitizedContent = new Sanitizer().sanitize(splittedContent);
		List<BigInteger> result = new LinkedList<>();
		for (int i : INDEXES) {
			result.add(new BigInteger(sanitizedContent.get(i)));
		}
		return result;
	}

}

class Sanitizer {

	static final String[] SANITIZED = new String[] { "<br />You must find U", "br", "n", " ", "\\+", "<", ">", "\\*",
			"=", "U", "/", "\\[", "\\]" };

	public String sanitize(String content) {
		String result = content;
		for (String s : SANITIZED) {
			result = result.replaceAll(s, "");
		}
		return result;
	}

	public List<String> sanitize(List<String> contents) {
		List<String> result = new LinkedList<>();
		for (String content : contents) {
			result.add(sanitize(content));
		}
		return result;
	}

}

class ArithmeticProgressionCalculator {

	/**
	 * 
	 * @param parameters
	 *            = (r, q, u0, index)
	 * @return
	 */
	public static BigInteger calculate(List<BigInteger> parameters) {
		BigInteger q = parameters.get(1);
		BigInteger r = parameters.get(0);
		BigInteger u0 = parameters.get(2);
		BigInteger index = parameters.get(3);
		BigInteger geometricPart = (index.subtract(BigInteger.ONE)).multiply(index).divide(new BigInteger("2"));
		BigInteger arithmeticPart = index.multiply(r);
		return u0.add(q.multiply(geometricPart)).add(arithmeticPart);
	}
}