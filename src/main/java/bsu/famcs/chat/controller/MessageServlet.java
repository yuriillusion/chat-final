package bsu.famcs.chat.controller;

import static bsu.famcs.chat.util.MessageUtil.TOKEN;
import static bsu.famcs.chat.util.MessageUtil.MESSAGES;
import static bsu.famcs.chat.util.MessageUtil.getIndex;

import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import org.apache.log4j.Logger;

@WebServlet("/chat")
public class MessageServlet extends HttpServlet{
    private static Logger logger = Logger.getLogger(MessageServlet.class);

    @Override
    public void init() throws ServletException{
        try{
            loadHistory();
        } catch(SAXException | IOException | ParserConfigurationException | TransformerException e){
            logger.error(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException{
        try{
            logger.info("doGet");
            String token = request.getParameter(TOKEN);
            logger.info("Token " + token);
            if (token != null && !"".equals(token)) {
                int index = getIndex(token);
                logger.info("Index " + index);
                String message = formResponse(index);

            }
        } catch(){

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @SuppressWarnings("unchecked")
    private String formGetResponse(int index) throws SAXException, IOException, ParserConfigurationException {
        JSONObject jsonObject = new JSONObject();
        //Преобразование в String
        jsonObject.put("messages", );
        jsonObject.put("token", getToken(messages.size()));
        return jsonObject.toJSONString();
    }

    private void loadHistory() throws SAXException, IOException, ParserConfigurationException {

    }
}
