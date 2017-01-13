import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

/**
 * @author rpecebou
 *
 *         Writeup for the Root-Me challenge "Encoded String"
 * 
 *         Uses pircbot library : http://www.jibble.org/pircbot.php
 */
public class UncompressMe {
	public static void main(String[] args) {
		UncompressMeBot bot = new UncompressMeBot();
		bot.setVerbose(true);
		try {
			bot.connect("irc.root-me.org", 6667, "");
		} catch (IOException | IrcException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		bot.joinChannel("#root-me_challenge");
		bot.sendMessage("candy", "!ep4");
	}
}

class UncompressMeBot extends PircBot {

	private Base64.Decoder _decoder;

	private Inflater _inflater;

	boolean _responseSent;

	public UncompressMeBot() {
		setName("rpecebou");
		_decoder = Base64.getDecoder();
		_inflater = new Inflater();
		_responseSent = false;
	}

	/*
	 * - Example string provided is "eJxzrHItCqn0zC8AABBiA2g="
	 *
	 * - It has first been compressed using zlib, and then encoded with base64
	 */
	@Override
	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		if (_responseSent) {
			return;
		}
		byte[] decodedMessage = _decoder.decode(message);
		sendMessage(sender, "!ep4 -rep " + deflate(decodedMessage));
		_responseSent = true;
	}

	private String deflate(byte[] compressedMessage) {
		try {
			_inflater.setInput(compressedMessage);
			byte[] decompressedMessage = new byte[100];
			int resultLength = _inflater.inflate(decompressedMessage);
			_inflater.end();
			return new String(decompressedMessage, 0, resultLength, "UTF-8");
		} catch (DataFormatException | UnsupportedEncodingException e) {
			System.err.println(e.getMessage());
			return "";
		}
	}

}
