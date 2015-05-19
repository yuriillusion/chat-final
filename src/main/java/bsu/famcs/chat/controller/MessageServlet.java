package bsu.famcs.chat.controller;

import static bsu.famcs.chat.util.MessageUtil.TOKEN;
import static bsu.famcs.chat.util.MessageUtil.getIndex;
import static bsu.famcs.chat.util.MessageUtil.getToken;
import static bsu.famcs.chat.util.MessageUtil.jsonToMessage;
import static bsu.famcs.chat.util.MessageUtil.stringToJson;

import bsu.famcs.chat.model.Message;
import bsu.famcs.chat.storage.xml.XMLHistoryUtil;
import bsu.famcs.chat.util.ServletUtil;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

@WebServlet("/chat")
public class MessageServlet extends HttpServlet{
    private static Logger logger = Logger.getLogger(MessageServlet.class);

    @Override
    public void init() throws ServletException{
        try{
            logger.info("Initializing");
            loadHistory();
        } catch(SAXException | IOException | ParserConfigurationException | TransformerException e){
            logger.error(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException{
        logger.info("doGet");
        String token = request.getParameter(TOKEN);
        //logger.info("Token " + token);
        try{
            if (token != null && !"".equals(token)) {
                int index = getIndex(token);
                //logger.info("Index: " + index);
                String jsonResponse = formGetResponse(index);
                logger.info("Response: " + jsonResponse);
                response.setCharacterEncoding(ServletUtil.UTF_8);
                response.setContentType(ServletUtil.APPLICATION_JSON);
                PrintWriter writer = response.getWriter();
                writer.print(jsonResponse);
                writer.flush();
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
            }
        } catch(SAXException | IOException | ParserConfigurationException | JSONException | ParseException e){
            logger.error(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doPost");
        String data = ServletUtil.getMessageBody(request);
        logger.info("Data: " + data);
        try{
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            XMLHistoryUtil.addData(message);
            response.setStatus(HttpServletResponse.SC_OK);
            logger.info("Message has been stored");
        } catch (SAXException | ParserConfigurationException | IOException | JSONException | TransformerException | ParseException e){
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @SuppressWarnings("unchecked")
    private String formGetResponse(int index) throws SAXException, IOException, ParserConfigurationException, JSONException, ParseException {
        List<Message> messages = XMLHistoryUtil.getSubMessagesByIndex(index);
        return "{\"messages\":" + messages + ",\"token\":\"" + getToken(XMLHistoryUtil.getStorageSize()) + "\"}";
    }

    private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
        if(!XMLHistoryUtil.doesStorageExist()){
            XMLHistoryUtil.createStorage();
        }
    }
}
