
package net.mehdi.springbatch.xml.mypackage;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Classe Java pour CLIENTType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="CLIENTType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Variables" type="{}VariablesType"/&gt;
 *         &lt;element name="Categories" type="{}CategoriesType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CLIENTType", propOrder = {
    "variables",
    "categories"
})
public class CLIENTType {

    @XmlElement(name = "Variables", required = true)
    protected VariablesType variables;
    @XmlElement(name = "Categories", required = true)
    protected CategoriesType categories;

    /**
     * Obtient la valeur de la propriété variables.
     * 
     * @return
     *     possible object is
     *     {@link VariablesType }
     *     
     */
    public VariablesType getVariables() {
        return variables;
    }

    /**
     * Définit la valeur de la propriété variables.
     * 
     * @param value
     *     allowed object is
     *     {@link VariablesType }
     *     
     */
    public void setVariables(VariablesType value) {
        this.variables = value;
    }

    /**
     * Obtient la valeur de la propriété categories.
     * 
     * @return
     *     possible object is
     *     {@link CategoriesType }
     *     
     */
    public CategoriesType getCategories() {
        return categories;
    }

    /**
     * Définit la valeur de la propriété categories.
     * 
     * @param value
     *     allowed object is
     *     {@link CategoriesType }
     *     
     */
    public void setCategories(CategoriesType value) {
        this.categories = value;
    }

}
