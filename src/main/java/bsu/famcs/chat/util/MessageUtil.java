package bsu.famcs.chat.util;

import bsu.famcs.chat.model.Message;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class MessageUtil {
    public static final String TOKEN = "token";
    private static final String MESSAGE = "message";
    private static final String ID = "id";
    private static final String AUTHOR = "author";
    private static final String TEXT = "text";
    private static final String STATUS = "statusCode";
    private static Logger logger = Logger.getLogger(MessageUtil.class);

    public static Message jsonToMessage(JSONObject json) throws JSONException{
        JSONObject jsonMessage = (JSONObject)json.get(MESSAGE);
        Object id = jsonMessage.get(ID);
        Object author = jsonMessage.get(AUTHOR);
        Object text = jsonMessage.get(TEXT);
        Object status = jsonMessage.get(STATUS);
        try {
            Message message = new Message((String) author, (String) text, (String) id, (long) status);
            return message;
        } catch(Exception e){
            logger.info(e);
        }
        return null;
    }

    public static JSONObject stringToJson(String data) throws JSONException, ParseException {
        JSONParser parser = new JSONParser();
        Object o = parser.parse(data.trim());
        try {
            JSONObject json = (JSONObject) o;
            return json;
        } catch (Exception e){
            logger.info(e);
        }
        return null;
    }

    public static int getIndex(String token){
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    public static String getToken(int index){
        Integer number = index * 8 + 11;
        return "TN" + number + "EN";
    }
}
