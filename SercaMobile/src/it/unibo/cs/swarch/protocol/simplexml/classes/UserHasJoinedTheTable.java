//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.13 at 02:54:56 PM CEST 
//


package it.unibo.cs.swarch.protocol.simplexml.classes;

import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="userThatHasJoined">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="6"/>
 *               &lt;maxLength value="15"/>
 *               &lt;pattern value="\w+"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="gameStarted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="firstPlayer">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="6"/>
 *               &lt;maxLength value="15"/>
 *               &lt;pattern value="\w+"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Default(DefaultType.FIELD)
@Order (elements = {
    "userThatHasJoined",
    "gameStarted",
    "firstPlayer"
})
@Root(name = "userHasJoinedTheTable")
public class UserHasJoinedTheTable {

    @Element(required = true)
    protected String userThatHasJoined;
    protected boolean gameStarted;
    @Element(required = false)
    protected String firstPlayer;

    /**
     * Gets the value of the userThatHasJoined property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserThatHasJoined() {
        return userThatHasJoined;
    }

    /**
     * Sets the value of the userThatHasJoined property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserThatHasJoined(String value) {
        this.userThatHasJoined = value;
    }

    /**
     * Gets the value of the gameStarted property.
     * 
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Sets the value of the gameStarted property.
     * 
     */
    public void setGameStarted(boolean value) {
        this.gameStarted = value;
    }

    /**
     * Gets the value of the firstPlayer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * Sets the value of the firstPlayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstPlayer(String value) {
        this.firstPlayer = value;
    }

}
