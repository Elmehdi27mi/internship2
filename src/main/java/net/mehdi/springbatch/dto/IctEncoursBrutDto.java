package net.mehdi.springbatch.dto;

import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
@XmlRootElement(name = "IctEncoursBrut")
public class IctEncoursBrutDto  {

    private String codeExercice;
    private String codePeriode;
    private String ieAffaire;
    private Date periode;
    private String tiersClient;
    private String codeProduit;
    private String categorieAffaireCG;
    private String codeTypePersonne;
    private String codeProfession;
    private String indiceActivite;
    private String codeAssietteTheorique;
    private String cspClient;
    private String secteurActivite;
    private String codeReglement;
    private String codeReseau;
    private String codeMarque;
    private String libelleMarque;
    private Date dateDebutAffaire;
    private Date dateFinAffaire;
    private Integer nbImpaye;
    private Integer dureeImpaye;
    private String flagCtx;
    private String flagDefaut;
    private String flagAdi;
    private BigDecimal soldComptable;
    private BigDecimal encours;
    private BigDecimal echeanceTtc;
    private int dureeInitial;
    private BigDecimal tauxNominal;
    private BigDecimal baseLocative;
    private BigDecimal mtBienFinHt;
    private BigDecimal depotDeGarantie;
    private BigDecimal valeurResiduelle;
    private BigDecimal premierLoyerMajore;
    private BigDecimal mtApport;
    private BigDecimal pctApport;
    private BigDecimal pctVr;
    private BigDecimal pctPlm;
    private BigDecimal pctEndettement;
    private BigDecimal mtAssuranceDeces;
    private BigDecimal margeAssurVie;
    private BigDecimal mtAssuPertAuto;
    private BigDecimal mtAssuPerte;
    private BigDecimal fraisDossier;
    private BigDecimal comApport;
    private Boolean flagRpat;
    private BigDecimal crdRpat;
    private Integer dureeResiduelle;
    private Boolean flagResiliation;
    private BigDecimal crdResi;
    private Integer dureeResiduelle1;
    private Boolean flagCompensation;
    private BigDecimal crdComp;
    private Integer dureeResiduelle2;
    private String phaseDebut;
    private BigDecimal encoursDebut;
    private BigDecimal impayesLoyeDebut;
    private BigDecimal impayCessDebut;
    private BigDecimal impayesFraisDebut;
    private BigDecimal extcptDebut;
    private BigDecimal recImpayesLoye;
    private BigDecimal recImpayesCess;
    private BigDecimal recImpayesFrais;
    private BigDecimal recImpayesExtcpt;
    private BigDecimal impayesBrutLoye;
    private BigDecimal impayesBrutCess;
    private BigDecimal impayesBrutFrais;
    private BigDecimal impayesBrutExtcpt;
    private String phaseFin;
    private BigDecimal encoursFin;
    private BigDecimal impayesLoyeFin;
    private BigDecimal impayCessFin;
    private BigDecimal impayesFraisFin;
    private BigDecimal extcptFin;
    private String statutDebut;
    private String statutFin;
    private String classeContagion;
    private BigDecimal cesTtcDef;
    private BigDecimal garantieDef;
    private BigDecimal agiosReservesDef;
    private BigDecimal baseProvisionDef;
    private BigDecimal provisionDef;
    private BigDecimal dotation;
    private BigDecimal risqueDotation;
    private Date dateMep;
    private String vinIdentifiantClient;
    private Integer voutAgeDureePret;
    private Integer voutAncienneteActPart;
    private Integer voutAnciActiviteEntr;
    private Integer voutNbrIncidents;
    private String categorie;
    private String csp;
    private String typeClient;
    private String codeReglementCg;
    private Boolean flagSinistre;


}

