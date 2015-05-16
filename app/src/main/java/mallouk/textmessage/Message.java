package mallouk.textmessage;

/**
 * Created by Matthew Jallouk on 5/3/2015.
 */
public class Message {

    private String contactName;
    private String contactNum;
    private String messageDate;
    private String formattedDate;
    private String actualMessage;
    private String messageType;

    public Message(String contactName, String contactNum, String messageDate, String formattedDate, String actualMessage,
                   String messageType){
        this.contactName = contactName;
        this.contactNum = contactNum;
        this.messageDate = messageDate;
        this.formattedDate = formattedDate;
        this.actualMessage = actualMessage;
        this.messageType = messageType;
    }

    public void setName(String contactName){
        this.contactName = contactName;
    }

    public String getContactName(){
        return contactName;
    }

    public String getContactNum(){
        return contactNum;
    }

    public String getMessageDate(){
        return messageDate;
    }

    public String getFormattedDate(){
        return formattedDate;
    }

    public String getActualMessage(){
        return actualMessage;
    }

    public String getMessageType(){ return messageType; }
}
