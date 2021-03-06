//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.13 at 02:54:56 PM CEST 
//


package it.unibo.cs.swarch.protocol.simplexml.classes;

import java.util.ArrayList;
import java.util.List;

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
 *         &lt;element name="user" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="uid">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;minLength value="6"/>
 *                         &lt;maxLength value="15"/>
 *                         &lt;pattern value="\w+"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="status">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;enumeration value="LOGGED_IN"/>
 *                         &lt;enumeration value="TABLECREATOR"/>
 *                         &lt;enumeration value="TABLECREATOR_WAITING"/>
 *                         &lt;enumeration value="PLAYER"/>
 *                         &lt;enumeration value="PLAYER_WAITING"/>
 *                         &lt;enumeration value="OBSERVER"/>
 *                         &lt;enumeration value="OBSERVER_WAITING"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="score" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
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
    "user"
})
@Root(name = "usersList")
public class UsersList {

	@ElementList(required=false)
    protected List<UsersList.User> user;

    /**
     * Gets the value of the user property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the user property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUser().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UsersList.User }
     * 
     * 
     */
    public List<UsersList.User> getUser() {
        if (user == null) {
            user = new ArrayList<UsersList.User>();
        }
        return this.user;
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
     *         &lt;element name="uid">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;minLength value="6"/>
     *               &lt;maxLength value="15"/>
     *               &lt;pattern value="\w+"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="status">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;enumeration value="LOGGED_IN"/>
     *               &lt;enumeration value="TABLECREATOR"/>
     *               &lt;enumeration value="TABLECREATOR_WAITING"/>
     *               &lt;enumeration value="PLAYER"/>
     *               &lt;enumeration value="PLAYER_WAITING"/>
     *               &lt;enumeration value="OBSERVER"/>
     *               &lt;enumeration value="OBSERVER_WAITING"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="score" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
        "uid",
        "status",
        "score"
    })
    public static class User {

        @Element(required = true)
        protected String uid;
        @Element(required = true)
        protected String status;
        protected int score;

        /**
         * Gets the value of the uid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUid() {
            return uid;
        }

        /**
         * Sets the value of the uid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUid(String value) {
            this.uid = value;
        }

        /**
         * Gets the value of the status property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStatus() {
            return status;
        }

        /**
         * Sets the value of the status property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStatus(String value) {
            this.status = value;
        }

        /**
         * Gets the value of the score property.
         * 
         */
        public int getScore() {
            return score;
        }

        /**
         * Sets the value of the score property.
         * 
         */
        public void setScore(int value) {
            this.score = value;
        }

    }

}
