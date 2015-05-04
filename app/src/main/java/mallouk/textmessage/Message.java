package mallouk.textmessage;

/**
 * Created by Matthew Jallouk on 5/3/2015.
 */
public class Message {

    private String contactName;
    private String contactNum;
    private String messageDate;
    private String actualMessage;

    public Message(String contactName, String contactNum, String messageDate, String actualMessage){
        this.contactName = contactName;
        this.contactNum = contactNum;
        this.messageDate = messageDate;
        this.actualMessage = actualMessage;
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

    public String getActualMessage(){
        return actualMessage;
    }
}
