import java.io.IOException;
import java.util.Base64;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

/**
 * @author rpecebou
 *
 *         Writeup for the Root-Me challenge "Encoded String"
 * 				 Uses pircbot library : http://www.jibble.org/pircbot.php
 */
public class EncodedString {
	public static void main(String[] args) {
		EncodedStringBot bot = new EncodedStringBot();
		bot.setVerbose(true);
		try {
			bot.connect("irc.root-me.org", 6667, "");
		} catch (IOException | IrcException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		bot.joinChannel("#root-me_challenge");
		bot.sendMessage("candy", "!ep2");
	}
}

class EncodedStringBot extends PircBot {

	private Base64.Decoder _decoder;

	boolean _responseSent;

	public EncodedStringBot() {
		setName("rpecebou");
		_decoder = Base64.getDecoder();
		_responseSent = false;
	}

	/*
	 * - Example string provided is "Um9vdE1l", could be Base64 encoding.
	 *
	 * - Indeed, Burp suite decoder tells us it is decoded as "RootMe"
	 */
	@Override
	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		if (_responseSent) {
			return;
		}
		String decodedMessage = new String(_decoder.decode(message));
		sendMessage(sender, "!ep2 -rep " + decodedMessage);
		_responseSent = true;
	}

}
