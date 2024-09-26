
package net.mehdi.springbatch.xml.mypackage;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Classe Java pour BodyType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="BodyType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CLIENT" type="{}CLIENTType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BodyType", propOrder = {
    "client"
})
public class BodyType {

    @XmlElement(name = "CLIENT", required = true)
    protected CLIENTType client;

    /**
     * Obtient la valeur de la propriété client.
     * 
     * @return
     *     possible object is
     *     {@link CLIENTType }
     *     
     */
    public CLIENTType getCLIENT() {
        return client;
    }

    /**
     * Définit la valeur de la propriété client.
     * 
     * @param value
     *     allowed object is
     *     {@link CLIENTType }
     *     
     */
    public void setCLIENT(CLIENTType value) {
        this.client = value;
    }

}
