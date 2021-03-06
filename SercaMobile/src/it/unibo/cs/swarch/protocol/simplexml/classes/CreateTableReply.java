//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.13 at 02:54:56 PM CEST 
//


package it.unibo.cs.swarch.protocol.simplexml.classes;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
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
 *         &lt;element name="created" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="cardsList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="card" maxOccurs="13" minOccurs="13">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;enumeration value="AH"/>
 *                         &lt;enumeration value="2H"/>
 *                         &lt;enumeration value="3H"/>
 *                         &lt;enumeration value="4H"/>
 *                         &lt;enumeration value="5H"/>
 *                         &lt;enumeration value="6H"/>
 *                         &lt;enumeration value="7H"/>
 *                         &lt;enumeration value="8H"/>
 *                         &lt;enumeration value="9H"/>
 *                         &lt;enumeration value="10H"/>
 *                         &lt;enumeration value="JH"/>
 *                         &lt;enumeration value="QH"/>
 *                         &lt;enumeration value="KH"/>
 *                         &lt;enumeration value="AC"/>
 *                         &lt;enumeration value="2C"/>
 *                         &lt;enumeration value="3C"/>
 *                         &lt;enumeration value="4C"/>
 *                         &lt;enumeration value="5C"/>
 *                         &lt;enumeration value="6C"/>
 *                         &lt;enumeration value="7C"/>
 *                         &lt;enumeration value="8C"/>
 *                         &lt;enumeration value="9C"/>
 *                         &lt;enumeration value="10C"/>
 *                         &lt;enumeration value="JC"/>
 *                         &lt;enumeration value="QC"/>
 *                         &lt;enumeration value="KC"/>
 *                         &lt;enumeration value="AD"/>
 *                         &lt;enumeration value="2D"/>
 *                         &lt;enumeration value="3D"/>
 *                         &lt;enumeration value="4D"/>
 *                         &lt;enumeration value="5D"/>
 *                         &lt;enumeration value="6D"/>
 *                         &lt;enumeration value="7D"/>
 *                         &lt;enumeration value="8D"/>
 *                         &lt;enumeration value="9D"/>
 *                         &lt;enumeration value="10D"/>
 *                         &lt;enumeration value="JD"/>
 *                         &lt;enumeration value="QD"/>
 *                         &lt;enumeration value="KD"/>
 *                         &lt;enumeration value="AS"/>
 *                         &lt;enumeration value="2S"/>
 *                         &lt;enumeration value="3S"/>
 *                         &lt;enumeration value="4S"/>
 *                         &lt;enumeration value="5S"/>
 *                         &lt;enumeration value="6S"/>
 *                         &lt;enumeration value="7S"/>
 *                         &lt;enumeration value="8S"/>
 *                         &lt;enumeration value="9S"/>
 *                         &lt;enumeration value="10S"/>
 *                         &lt;enumeration value="JS"/>
 *                         &lt;enumeration value="QS"/>
 *                         &lt;enumeration value="KS"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="gameIsStarted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="firstPlayer" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="4"/>
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
    "created",
    "cardsList",
    "gameIsStarted",
    "firstPlayer"
})
@Root(name = "createTableReply")
public class CreateTableReply {

    protected boolean created;
    @Element(required = true)
    protected CreateTableReply.CardsList cardsList;
    protected boolean gameIsStarted;
    @Element(required = false)
    protected String firstPlayer;

    /**
     * Gets the value of the created property.
     * 
     */
    public boolean isCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * 
     */
    public void setCreated(boolean value) {
        this.created = value;
    }

    /**
     * Gets the value of the cardsList property.
     * 
     * @return
     *     possible object is
     *     {@link CreateTableReply.CardsList }
     *     
     */
    public CreateTableReply.CardsList getCardsList() {
        return cardsList;
    }

    /**
     * Sets the value of the cardsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateTableReply.CardsList }
     *     
     */
    public void setCardsList(CreateTableReply.CardsList value) {
        this.cardsList = value;
    }

    /**
     * Gets the value of the gameIsStarted property.
     * 
     */
    public boolean isGameIsStarted() {
        return gameIsStarted;
    }

    /**
     * Sets the value of the gameIsStarted property.
     * 
     */
    public void setGameIsStarted(boolean value) {
        this.gameIsStarted = value;
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
     *         &lt;element name="card" maxOccurs="13" minOccurs="13">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;enumeration value="AH"/>
     *               &lt;enumeration value="2H"/>
     *               &lt;enumeration value="3H"/>
     *               &lt;enumeration value="4H"/>
     *               &lt;enumeration value="5H"/>
     *               &lt;enumeration value="6H"/>
     *               &lt;enumeration value="7H"/>
     *               &lt;enumeration value="8H"/>
     *               &lt;enumeration value="9H"/>
     *               &lt;enumeration value="10H"/>
     *               &lt;enumeration value="JH"/>
     *               &lt;enumeration value="QH"/>
     *               &lt;enumeration value="KH"/>
     *               &lt;enumeration value="AC"/>
     *               &lt;enumeration value="2C"/>
     *               &lt;enumeration value="3C"/>
     *               &lt;enumeration value="4C"/>
     *               &lt;enumeration value="5C"/>
     *               &lt;enumeration value="6C"/>
     *               &lt;enumeration value="7C"/>
     *               &lt;enumeration value="8C"/>
     *               &lt;enumeration value="9C"/>
     *               &lt;enumeration value="10C"/>
     *               &lt;enumeration value="JC"/>
     *               &lt;enumeration value="QC"/>
     *               &lt;enumeration value="KC"/>
     *               &lt;enumeration value="AD"/>
     *               &lt;enumeration value="2D"/>
     *               &lt;enumeration value="3D"/>
     *               &lt;enumeration value="4D"/>
     *               &lt;enumeration value="5D"/>
     *               &lt;enumeration value="6D"/>
     *               &lt;enumeration value="7D"/>
     *               &lt;enumeration value="8D"/>
     *               &lt;enumeration value="9D"/>
     *               &lt;enumeration value="10D"/>
     *               &lt;enumeration value="JD"/>
     *               &lt;enumeration value="QD"/>
     *               &lt;enumeration value="KD"/>
     *               &lt;enumeration value="AS"/>
     *               &lt;enumeration value="2S"/>
     *               &lt;enumeration value="3S"/>
     *               &lt;enumeration value="4S"/>
     *               &lt;enumeration value="5S"/>
     *               &lt;enumeration value="6S"/>
     *               &lt;enumeration value="7S"/>
     *               &lt;enumeration value="8S"/>
     *               &lt;enumeration value="9S"/>
     *               &lt;enumeration value="10S"/>
     *               &lt;enumeration value="JS"/>
     *               &lt;enumeration value="QS"/>
     *               &lt;enumeration value="KS"/>
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
        "card"
    })
    public static class CardsList {

        @ElementList(required = true)
        protected List<String> card;

        /**
         * Gets the value of the card property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the card property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCard().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getCard() {
            if (card == null) {
                card = new ArrayList<String>();
            }
            return this.card;
        }

    }

}
