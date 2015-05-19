package bsu.famcs.chat.model;

public class Message{
    private String author;
    private String text;
    private String id;
    private long status;

    public Message(String author, String text, String id, long status) {
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

    public long getStatus() {
        return status;
    }
    public void setStatus(long status) {
        this.status = status;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "{\"author\":\"" + author + "\",\"text\":\"" + text + "\",\"id\":\"" + id + "\",\"statusCode\":" +
                status + "}";
    }
}
