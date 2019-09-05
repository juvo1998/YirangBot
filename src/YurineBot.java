import java.awt.Color;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.IMentionable;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.cache.CacheFlag;

public class YurineBot extends ListenerAdapter {
	
	private HashMap<String, String> commands = new HashMap<String, String>();
	
	public static void main(String[] arguments) throws Exception {
	    JDA jda = new JDABuilder("NTQxNDkyMjM0NjkzMTE1OTE1.DzgPjQ.TIi9Z219BRd1m8EM9BsqfMMiUJo")
	    		.addEventListener(new YurineBot()).build();
	    jda.awaitReady();
	    System.out.println("Finished building JDA!");
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		if (event.getAuthor().isBot()) return;
		
		JDA jda = event.getJDA();
		Message message = event.getMessage();
		String content = message.getContentRaw();
		
		if (content.equals("!flipcoin")) {
			MessageChannel channel = event.getChannel();
			int randomNum = ThreadLocalRandom.current().nextInt(0, 2);			
			switch (randomNum) {
			case 0:
				channel.sendMessage("`Tails`").queue();
				break;
			case 1:
				channel.sendMessage("`Heads`").queue();
				break;
			}
		}
		
		if (content.equals("!ping")) {
			MessageChannel channel = event.getChannel();
			channel.sendMessage("`pong!`").queue();
		}
		
		if (content.startsWith("!roll")) {
			MessageChannel channel = event.getChannel();
			Pattern pattern = Pattern.compile("!roll ([0-9]+)");
			Matcher contentMatcher = pattern.matcher(content);
			if (contentMatcher.matches()) {
				int maxRoll = Integer.parseInt(contentMatcher.group(1));
				int randomNum = ThreadLocalRandom.current().nextInt(1, maxRoll + 1);
				channel.sendMessage("`" + String.valueOf(randomNum) + "`").queue();
			} else {
				channel.sendMessage("`Format: !roll X where X is a number`").queue();
			}
		}
		
		if (content.equals("!help")) {
			MessageChannel channel = event.getChannel();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setThumbnail("https://cdn.discordapp.com/avatars/541492234693115915/cea99a0b52340e53d11436057d382c1f.png");
			eb.setAuthor("The Great Yurine Commands", null, "https://cdn.discordapp.com/avatars/541492234693115915/cea99a0b52340e53d11436057d382c1f.png");
			eb.setColor(new Color(117, 238, 54));
			eb.addField("!ping", "`pong!`", false);
			eb.addField("!flipcoin", "`Either heads or tails`", false);
			eb.addField("!roll X", "`Rolls X-sided dice`", false);
			eb.addField("!avatar X", "`Gets profile picture of user X`", false);
			eb.addField("!command add \"X\" \"Y\"", "`Creates command X with output Y`", false);
			eb.addField("!command del \"X\"", "`Deletes command X`", false);
			channel.sendMessage(eb.build()).queue();
		}
		
		if (content.startsWith("!avatar")) {
			MessageChannel channel = event.getChannel();
			Pattern pattern = Pattern.compile("!avatar ([a-zA-Z0-9 ]*)");
			Matcher contentMatcher = pattern.matcher(content);
			if (contentMatcher.matches()) {
				String username = contentMatcher.group(1);
				List<User> userList = jda.getUsersByName(username, true);
				if (!userList.isEmpty()) {
					User user = userList.get(0);
					channel.sendMessage(user.getAvatarUrl()).queue();
				} else {
					channel.sendMessage("`Cannot find user`").queue();
				}
			}
		}
		
		if (content.contains("xd") || content.contains("Xd") || content.contains("xD") || content.contains("XD")) {
			MessageChannel xdWall = jda.getTextChannelById("541747604300627998");
			String avatarUrl = event.getAuthor().getAvatarUrl();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(new Color(248, 231, 28));
			eb.setAuthor(event.getAuthor().getName(), null, avatarUrl);
			eb.setTitle(content);
			eb.setTimestamp(Instant.now());
			xdWall.sendMessage(eb.build()).queue();
		}
		
		if (content.startsWith("!command add")) {
			MessageChannel channel = event.getChannel();
			Pattern pattern = Pattern.compile("\\!command add \"([ a-zA-Z0-9\\!]+)\" \"([ a-zA-Z0-9\\!]+)\"");
			Matcher contentMatcher = pattern.matcher(content);
			if (contentMatcher.matches()) {
				String input = contentMatcher.group(1);
				String output = contentMatcher.group(2);
				commands.put(input, output);
				channel.sendMessage("`Command added successfully`").queue();
			} else {
				channel.sendMessage("`Could not add command; invalid format or characters`").queue();
			}
		}
		
		if (commands.containsKey(content)) {
			MessageChannel channel = event.getChannel();
			channel.sendMessage(commands.get(content)).queue();
		}
		
		if (content.startsWith("!command del")) {
			MessageChannel channel = event.getChannel();
			Pattern pattern = Pattern.compile("\\!command del \"([ a-zA-Z0-9\\!]+)\"");
			Matcher contentMatcher = pattern.matcher(content);
			if(contentMatcher.matches()) {
				String input = contentMatcher.group(1);
				if (commands.containsKey(input)) {
					commands.remove(input);
					channel.sendMessage("`Command deleted successfully`").queue();
				} else {
					channel.sendMessage("`Could not find command`").queue();
				}
			} else {
				channel.sendMessage("`Could not delete command; invalid format or characters`").queue();
			}
		}
		
		if (content.equals("!game")) {
			MessageChannel channel = event.getChannel();
			MessageBuilder mb = new MessageBuilder("test");
			channel.sendMessage(mb.build()).queue();
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		
		MessageChannel channel = event.getChannel();
		User user = event.getUser();
		String userMention = user.getAsMention();
		Emote emote = event.getReactionEmote().getEmote();
		System.out.println(emote.getId());
		
		if (emote.getName().equals("PepeHands")) {
			channel.sendMessage(userMention + " \":PepeHands:\"").queue();
			
		}
	}
}
