package bsu.famcs.chat.model;

public class Message{
    public enum Status{
        GOOD(0), EDITED(1), DELETED(2);
        private int status;

        Status(int status){
            this.status = status;
        }
        public int getStatus() {
            return status;
        }
    }

    private String author;
    private String text;
    private String id;
    private Status status;

    public Message(String author, String text, String id, Status status) {
        this.author = author;
        this.text = text;
        this.id = id;
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = new String(id);
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
        //exception+
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "{\"author\":\"" + author + "\",\"text\":\"" + text + "\",\"id\":\"" + id + "\",\"statusCode\":" +
                status.getStatus() + "}";
    }
}
