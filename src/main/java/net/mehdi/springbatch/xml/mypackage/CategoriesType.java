
package net.mehdi.springbatch.xml.mypackage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Classe Java pour CategoriesType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="CategoriesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OBSERVATION" type="{}OBSERVATIONType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="AFFAIRE" type="{}AFFAIREType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CategoriesType", propOrder = {
    "observation",
    "affaire"
})
public class CategoriesType {

    @XmlElement(name = "OBSERVATION")
    protected List<OBSERVATIONType> observation;
    @XmlElement(name = "AFFAIRE")
    protected List<AFFAIREType> affaire;

    /**
     * Gets the value of the observation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the observation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOBSERVATION().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OBSERVATIONType }
     * 
     * 
     */
    public List<OBSERVATIONType> getOBSERVATION() {
        if (observation == null) {
            observation = new ArrayList<OBSERVATIONType>();
        }
        return this.observation;
    }

    /**
     * Gets the value of the affaire property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the affaire property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAFFAIRE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AFFAIREType }
     * 
     * 
     */
    public List<AFFAIREType> getAFFAIRE() {
        if (affaire == null) {
            affaire = new ArrayList<AFFAIREType>();
        }
        return this.affaire;
    }

}
