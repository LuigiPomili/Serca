package it.unibo.cs.swarch.protocol.simplexml.classes;

import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Default(DefaultType.FIELD)
@Order(elements={
    "value"
})
@Root(name = "loginReply")
public class LoginReply {

    @Element(required = true)
    protected String value;
	
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
}
