
package net.mehdi.springbatch.xml.mypackage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Classe Java pour rootType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="rootType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="StrategyOneRequest" type="{}StrategyOneRequestType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rootType", propOrder = {
    "strategyOneRequest"
})
public class RootType {

    @XmlElement(name = "StrategyOneRequest")
    protected List<StrategyOneRequestType> strategyOneRequest;

    /**
     * Gets the value of the strategyOneRequest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the strategyOneRequest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStrategyOneRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StrategyOneRequestType }
     * 
     * 
     */
    public List<StrategyOneRequestType> getStrategyOneRequest() {
        if (strategyOneRequest == null) {
            strategyOneRequest = new ArrayList<StrategyOneRequestType>();
        }
        return this.strategyOneRequest;
    }

}
