package bsu.famcs.chat.util;

import bsu.famcs.chat.model.Message;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class MessageUtil {
    private static final String MESSAGE = "message";
    public static final String ID = "id";
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
        return new Message((String) author, (String) text, (String) id, (long) status);
    }

    public static JSONObject stringToJson(String data) throws JSONException, ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(data.trim());
    }
}
