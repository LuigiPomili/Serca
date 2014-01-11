//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.10.06 at 12:29:35 PM CEST 
//


package it.unibo.cs.swarch.serca.protocol.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="tableId">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="6"/>
 *               &lt;maxLength value="15"/>
 *               &lt;pattern value="\w+"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="reason">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="creator has left the game"/>
 *               &lt;enumeration value="there is a winner"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="winnerIs">
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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "tableId",
    "reason",
    "winnerIs"
})
@XmlRootElement(name = "gameover")
public class Gameover {

    @XmlElement(required = true)
    protected String tableId;
    @XmlElement(required = true)
    protected String reason;
    @XmlElement(required = true)
    protected String winnerIs;

    /**
     * Gets the value of the tableId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTableId() {
        return tableId;
    }

    /**
     * Sets the value of the tableId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTableId(String value) {
        this.tableId = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

    /**
     * Gets the value of the winnerIs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWinnerIs() {
        return winnerIs;
    }

    /**
     * Sets the value of the winnerIs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWinnerIs(String value) {
        this.winnerIs = value;
    }

}
