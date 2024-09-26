
package net.mehdi.springbatch.xml.mypackage;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Classe Java pour VariablesType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="VariablesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Snap_shot" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ID_Lancement" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Date_Calcul" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Age_Duree_Pret" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="660"/&gt;
 *               &lt;enumeration value="819"/&gt;
 *               &lt;enumeration value="866"/&gt;
 *               &lt;enumeration value="876"/&gt;
 *               &lt;enumeration value="902"/&gt;
 *               &lt;enumeration value="933"/&gt;
 *               &lt;enumeration value="882"/&gt;
 *               &lt;enumeration value="830"/&gt;
 *               &lt;enumeration value="898"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Anciennete_PM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Anciennete_PP" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="0"/&gt;
 *               &lt;enumeration value="86.8008213552361"/&gt;
 *               &lt;enumeration value="60.4188911704312"/&gt;
 *               &lt;enumeration value="121.659137577002"/&gt;
 *               &lt;enumeration value="124.878850102669"/&gt;
 *               &lt;enumeration value="144.29568788501"/&gt;
 *               &lt;enumeration value="99.2197125256673"/&gt;
 *               &lt;enumeration value="373.19"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Date_Debut" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="2022-01-05"/&gt;
 *               &lt;enumeration value="2023-08-05"/&gt;
 *               &lt;enumeration value="2021-04-06"/&gt;
 *               &lt;enumeration value="2022-01-06"/&gt;
 *               &lt;enumeration value="2024-03-06"/&gt;
 *               &lt;enumeration value="2023-06-05"/&gt;
 *               &lt;enumeration value="2024-02-06"/&gt;
 *               &lt;enumeration value="2022-03-05"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Date_Fin" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="2027-01-04"/&gt;
 *               &lt;enumeration value="2033-08-04"/&gt;
 *               &lt;enumeration value="2025-04-05"/&gt;
 *               &lt;enumeration value="2026-01-05"/&gt;
 *               &lt;enumeration value="2028-03-05"/&gt;
 *               &lt;enumeration value="2027-12-04"/&gt;
 *               &lt;enumeration value="2024-12-05"/&gt;
 *               &lt;enumeration value="2025-03-04"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Nbr_Incidents" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="0"/&gt;
 *               &lt;enumeration value="28"/&gt;
 *               &lt;enumeration value="20"/&gt;
 *               &lt;enumeration value="19"/&gt;
 *               &lt;enumeration value="24"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Taux_Apport" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="5.5"/&gt;
 *               &lt;enumeration value="1.4"/&gt;
 *               &lt;enumeration value="0"/&gt;
 *               &lt;enumeration value="40"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Categorie" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="LOA"/&gt;
 *               &lt;enumeration value="PP"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CSP" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="PM"/&gt;
 *               &lt;enumeration value="Retraite Bancaire"/&gt;
 *               &lt;enumeration value="Commerçant"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Type_Client" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="PM"/&gt;
 *               &lt;enumeration value="PP"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Code_Reglement_CG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Date_Observation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Encours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Solde" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Nb_Impaye" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="0"/&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Duree_Impaye" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="0"/&gt;
 *               &lt;enumeration value="26"/&gt;
 *               &lt;enumeration value="25"/&gt;
 *               &lt;enumeration value="24"/&gt;
 *               &lt;enumeration value="56"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Classe_Contagion" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="SENS"/&gt;
 *               &lt;enumeration value="SAIN"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Flag_Contentieux" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="O"/&gt;
 *               &lt;enumeration value="N"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Flag_Resilia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Flag_Sinistre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VariablesType", propOrder = {
    "id",
    "snapShot",
    "idLancement",
    "dateCalcul",
    "ageDureePret",
    "anciennetePM",
    "anciennetePP",
    "dateDebut",
    "dateFin",
    "nbrIncidents",
    "tauxApport",
    "categorie",
    "csp",
    "typeClient",
    "codeReglementCG",
    "dateObservation",
    "encours",
    "solde",
    "nbImpaye",
    "dureeImpaye",
    "classeContagion",
    "flagContentieux",
    "flagResilia",
    "flagSinistre"
})
public class VariablesType {

    @XmlElement(name = "ID")
    protected String id;
    @XmlElement(name = "Snap_shot")
    protected String snapShot;
    @XmlElement(name = "ID_Lancement")
    protected String idLancement;
    @XmlElement(name = "Date_Calcul")
    protected String dateCalcul;
    @XmlElement(name = "Age_Duree_Pret")
    protected String ageDureePret;
    @XmlElement(name = "Anciennete_PM")
    protected String anciennetePM;
    @XmlElement(name = "Anciennete_PP")
    protected String anciennetePP;
    @XmlElement(name = "Date_Debut")
    protected String dateDebut;
    @XmlElement(name = "Date_Fin")
    protected String dateFin;
    @XmlElement(name = "Nbr_Incidents")
    protected String nbrIncidents;
    @XmlElement(name = "Taux_Apport")
    protected String tauxApport;
    @XmlElement(name = "Categorie")
    protected String categorie;
    @XmlElement(name = "CSP")
    protected String csp;
    @XmlElement(name = "Type_Client")
    protected String typeClient;
    @XmlElement(name = "Code_Reglement_CG")
    protected String codeReglementCG;
    @XmlElement(name = "Date_Observation")
    protected String dateObservation;
    @XmlElement(name = "Encours")
    protected String encours;
    @XmlElement(name = "Solde")
    protected String solde;
    @XmlElement(name = "Nb_Impaye")
    protected String nbImpaye;
    @XmlElement(name = "Duree_Impaye")
    protected String dureeImpaye;
    @XmlElement(name = "Classe_Contagion")
    protected String classeContagion;
    @XmlElement(name = "Flag_Contentieux")
    protected String flagContentieux;
    @XmlElement(name = "Flag_Resilia")
    protected String flagResilia;
    @XmlElement(name = "Flag_Sinistre")
    protected String flagSinistre;

    /**
     * Obtient la valeur de la propriété id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété snapShot.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSnapShot() {
        return snapShot;
    }

    /**
     * Définit la valeur de la propriété snapShot.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSnapShot(String value) {
        this.snapShot = value;
    }

    /**
     * Obtient la valeur de la propriété idLancement.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDLancement() {
        return idLancement;
    }

    /**
     * Définit la valeur de la propriété idLancement.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDLancement(String value) {
        this.idLancement = value;
    }

    /**
     * Obtient la valeur de la propriété dateCalcul.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateCalcul() {
        return dateCalcul;
    }

    /**
     * Définit la valeur de la propriété dateCalcul.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateCalcul(String value) {
        this.dateCalcul = value;
    }

    /**
     * Obtient la valeur de la propriété ageDureePret.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgeDureePret() {
        return ageDureePret;
    }

    /**
     * Définit la valeur de la propriété ageDureePret.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgeDureePret(String value) {
        this.ageDureePret = value;
    }

    /**
     * Obtient la valeur de la propriété anciennetePM.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnciennetePM() {
        return anciennetePM;
    }

    /**
     * Définit la valeur de la propriété anciennetePM.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnciennetePM(String value) {
        this.anciennetePM = value;
    }

    /**
     * Obtient la valeur de la propriété anciennetePP.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnciennetePP() {
        return anciennetePP;
    }

    /**
     * Définit la valeur de la propriété anciennetePP.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnciennetePP(String value) {
        this.anciennetePP = value;
    }

    /**
     * Obtient la valeur de la propriété dateDebut.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateDebut() {
        return dateDebut;
    }

    /**
     * Définit la valeur de la propriété dateDebut.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateDebut(String value) {
        this.dateDebut = value;
    }

    /**
     * Obtient la valeur de la propriété dateFin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateFin() {
        return dateFin;
    }

    /**
     * Définit la valeur de la propriété dateFin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateFin(String value) {
        this.dateFin = value;
    }

    /**
     * Obtient la valeur de la propriété nbrIncidents.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNbrIncidents() {
        return nbrIncidents;
    }

    /**
     * Définit la valeur de la propriété nbrIncidents.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNbrIncidents(String value) {
        this.nbrIncidents = value;
    }

    /**
     * Obtient la valeur de la propriété tauxApport.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTauxApport() {
        return tauxApport;
    }

    /**
     * Définit la valeur de la propriété tauxApport.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTauxApport(String value) {
        this.tauxApport = value;
    }

    /**
     * Obtient la valeur de la propriété categorie.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategorie() {
        return categorie;
    }

    /**
     * Définit la valeur de la propriété categorie.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategorie(String value) {
        this.categorie = value;
    }

    /**
     * Obtient la valeur de la propriété csp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSP() {
        return csp;
    }

    /**
     * Définit la valeur de la propriété csp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSP(String value) {
        this.csp = value;
    }

    /**
     * Obtient la valeur de la propriété typeClient.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeClient() {
        return typeClient;
    }

    /**
     * Définit la valeur de la propriété typeClient.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeClient(String value) {
        this.typeClient = value;
    }

    /**
     * Obtient la valeur de la propriété codeReglementCG.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeReglementCG() {
        return codeReglementCG;
    }

    /**
     * Définit la valeur de la propriété codeReglementCG.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeReglementCG(String value) {
        this.codeReglementCG = value;
    }

    /**
     * Obtient la valeur de la propriété dateObservation.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateObservation() {
        return dateObservation;
    }

    /**
     * Définit la valeur de la propriété dateObservation.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateObservation(String value) {
        this.dateObservation = value;
    }

    /**
     * Obtient la valeur de la propriété encours.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncours() {
        return encours;
    }

    /**
     * Définit la valeur de la propriété encours.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncours(String value) {
        this.encours = value;
    }

    /**
     * Obtient la valeur de la propriété solde.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolde() {
        return solde;
    }

    /**
     * Définit la valeur de la propriété solde.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolde(String value) {
        this.solde = value;
    }

    /**
     * Obtient la valeur de la propriété nbImpaye.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNbImpaye() {
        return nbImpaye;
    }

    /**
     * Définit la valeur de la propriété nbImpaye.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNbImpaye(String value) {
        this.nbImpaye = value;
    }

    /**
     * Obtient la valeur de la propriété dureeImpaye.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDureeImpaye() {
        return dureeImpaye;
    }

    /**
     * Définit la valeur de la propriété dureeImpaye.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDureeImpaye(String value) {
        this.dureeImpaye = value;
    }

    /**
     * Obtient la valeur de la propriété classeContagion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClasseContagion() {
        return classeContagion;
    }

    /**
     * Définit la valeur de la propriété classeContagion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClasseContagion(String value) {
        this.classeContagion = value;
    }

    /**
     * Obtient la valeur de la propriété flagContentieux.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlagContentieux() {
        return flagContentieux;
    }

    /**
     * Définit la valeur de la propriété flagContentieux.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlagContentieux(String value) {
        this.flagContentieux = value;
    }

    /**
     * Obtient la valeur de la propriété flagResilia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlagResilia() {
        return flagResilia;
    }

    /**
     * Définit la valeur de la propriété flagResilia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlagResilia(String value) {
        this.flagResilia = value;
    }

    /**
     * Obtient la valeur de la propriété flagSinistre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlagSinistre() {
        return flagSinistre;
    }

    /**
     * Définit la valeur de la propriété flagSinistre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlagSinistre(String value) {
        this.flagSinistre = value;
    }

}
