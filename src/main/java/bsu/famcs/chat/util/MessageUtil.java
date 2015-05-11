package bsu.famcs.chat.util;

public final class MessageUtil {
    public static final String TOKEN = "token";
    public static final String MESSAGES = "messages";

    public static int getIndex(String token){
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }
}
