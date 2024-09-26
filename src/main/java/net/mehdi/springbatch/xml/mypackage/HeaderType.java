
package net.mehdi.springbatch.xml.mypackage;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Classe Java pour HeaderType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="HeaderType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InquiryCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ProcessCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="OrganizationCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HeaderType", propOrder = {
    "inquiryCode",
    "processCode",
    "organizationCode"
})
public class HeaderType {

    @XmlElement(name = "InquiryCode", required = true)
    protected String inquiryCode;
    @XmlElement(name = "ProcessCode", required = true)
    protected String processCode;
    @XmlElement(name = "OrganizationCode", required = true)
    protected String organizationCode;

    /**
     * Obtient la valeur de la propriété inquiryCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInquiryCode() {
        return inquiryCode;
    }

    /**
     * Définit la valeur de la propriété inquiryCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInquiryCode(String value) {
        this.inquiryCode = value;
    }

    /**
     * Obtient la valeur de la propriété processCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessCode() {
        return processCode;
    }

    /**
     * Définit la valeur de la propriété processCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessCode(String value) {
        this.processCode = value;
    }

    /**
     * Obtient la valeur de la propriété organizationCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Définit la valeur de la propriété organizationCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganizationCode(String value) {
        this.organizationCode = value;
    }

}
