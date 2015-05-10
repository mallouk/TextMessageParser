package mallouk.textmessage;

import java.util.ArrayList;

/**
 * Created by Matthew Jallouk on 4/28/2015.
 */
public class Conversation extends ArrayList {

    private String contactName;

    public Conversation(){

    }

    public void setContactName(String contactName){
        this.contactName = contactName;
    }

    public String getContactName(){
        return this.contactName;
    }
}
