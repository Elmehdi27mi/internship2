package net.mehdi.springbatch.entities;

import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ICT_ENCOURS_BRUT")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString
public class IctEncoursBrut {

    @Id
    @Column(name = "CODE_EXERCICE")
    private String codeExercice;

    @Column(name = "CODE_PERIODE")
    private String codePeriode;

    @Column(name = "IE_AFFAIRE")
    private String ieAffaire;

    @Column(name = "PERIODE")
    private Date periode;

    @Column(name = "TIERS_CLIENT")
    private String tiersClient;

    @Column(name = "CODE_PRODUIT")
    private String codeProduit;

    @Column(name = "CATEGORIE_AFFAIRE_CG")
    private String categorieAffaireCG;

    @Column(name = "CODE_TYPE_PERSONNE")
    private String codeTypePersonne;

    @Column(name = "CODE_PROFESSION")
    private String codeProfession;

    @Column(name = "INDICE_ACTIVITE")
    private String indiceActivite;

    @Column(name = "CODE_ASSIETTE_THEORIQUE")
    private String codeAssietteTheorique;

    @Column(name = "CSP_CLIENT")
    private String cspClient;

    @Column(name = "SECTEUR_ACTIVITE")
    private String secteurActivite;

    @Column(name = "CODE_REGLEMENT")
    private String codeReglement;

    @Column(name = "CODE_RESEAU")
    private String codeReseau;

    @Column(name = "CODE_MARQUE")
    private String codeMarque;

    @Column(name = "LIBELLE_MARQUE")
    private String libelleMarque;

    @Column(name = "DATE_DEBUT_AFFAIRE")
    private Date dateDebutAffaire;

    @Column(name = "DATE_FIN_AFFAIRE")
    private Date dateFinAffaire;

    @Column(name = "NB_IMPAYE")
    private Integer nbImpaye;

    @Column(name = "DUREE_IMPAYE")
    private Integer dureeImpaye;

    @Column(name = "FLAG_CTX")
    private String flagCtx;

    @Column(name = "FLAG_DEFAUT")
    private String flagDefaut;

    @Column(name = "FLAG_ADI")
    private String flagAdi;

    @Column(name = "SOLD_COMPTABLE")
    private BigDecimal soldComptable;

    @Column(name = "ENCOURS")
    private BigDecimal encours;

    @Column(name = "ECHEANCE_TTC")
    private BigDecimal echeanceTtc;

    @Column(name = "DUREE_INITIAL")
    private Integer dureeInitial;

    @Column(name = "TAUX_NOMINAL")
    private BigDecimal tauxNominal;

    @Column(name = "BASE_LOCATIVE")
    private BigDecimal baseLocative;

    @Column(name = "MT_BIEN_FIN_HT")
    private BigDecimal mtBienFinHt;

    @Column(name = "DEPOT_DE_GARANTIE")
    private BigDecimal depotDeGarantie;

    @Column(name = "VALEUR_RESIDUELLE")
    private BigDecimal valeurResiduelle;

    @Column(name = "PREMIER_LOYER_MAJORED")
    private BigDecimal premierLoyerMajore;

    @Column(name = "MT_APPORT")
    private BigDecimal mtApport;

    @Column(name = "PCT_APPORT")
    private BigDecimal pctApport;

    @Column(name = "PCT_VR")
    private BigDecimal pctVr;

    @Column(name = "PCT_PLM")
    private BigDecimal pctPlm;

    @Column(name = "PCT_ENDETTEMENT")
    private BigDecimal pctEndettement;

    @Column(name = "MT_ASSURANCE_DECES")
    private BigDecimal mtAssuranceDeces;

    @Column(name = "MARGE_ASSUR_VIE")
    private BigDecimal margeAssurVie;

    @Column(name = "MT_ASSU_PERT_AUTO")
    private BigDecimal mtAssuPertAuto;

    @Column(name = "MT_ASSU_PERTE")
    private BigDecimal mtAssuPerte;

    @Column(name = "FRAIS_DOSSIER")
    private BigDecimal fraisDossier;

    @Column(name = "COM_APPORT")
    private BigDecimal comApport;

    @Column(name = "FLAG_RPAT")
    private Boolean flagRpat;

    @Column(name = "CRD_RPAT")
    private BigDecimal crdRpat;

    @Column(name = "DUREE_RESIDUELLE")
    private Integer dureeResiduelle;

    @Column(name = "FLAG_RESILIATION")
    private Boolean flagResiliation;

    @Column(name = "CRD_RESI")
    private BigDecimal crdResi;

    @Column(name = "PHASE_DEBUT")
    private String phaseDebut;

    @Column(name = "ENCOURS_DEBUT")
    private BigDecimal encoursDebut;

    @Column(name = "IMPAYES_LOYE_DEBUT")
    private BigDecimal impayesLoyeDebut;

    @Column(name = "IMPAY_CESS_DEBUT")
    private BigDecimal impayCessDebut;

    @Column(name = "IMPAYES_FRAIS_DEBUT")
    private BigDecimal impayesFraisDebut;

    @Column(name = "EXTCPT_DEBUT")
    private BigDecimal extcptDebut;

    @Column(name = "REC_IMPAYES_LOYE")
    private BigDecimal recImpayesLoye;

    @Column(name = "REC_IMPAYES_CESS")
    private BigDecimal recImpayesCess;

    @Column(name = "REC_IMPAYES_FRAIS")
    private BigDecimal recImpayesFrais;

    @Column(name = "REC_IMPAYES_EXTCPT")
    private BigDecimal recImpayesExtcpt;

    @Column(name = "IMPAYES_BRUT_LOYE")
    private BigDecimal impayesBrutLoye;

    @Column(name = "IMPAYES_BRUT_CESS")
    private BigDecimal impayesBrutCess;

    @Column(name = "IMPAYES_BRUT_FRAIS")
    private BigDecimal impayesBrutFrais;

    @Column(name = "IMPAYES_BRUT_EXTCPT")
    private BigDecimal impayesBrutExtcpt;

    @Column(name = "PHASE_FIN")
    private String phaseFin;

    @Column(name = "ENCOURS_FIN")
    private BigDecimal encoursFin;

    @Column(name = "IMPAYES_LOYE_FIN")
    private BigDecimal impayesLoyeFin;

    @Column(name = "IMPAY_CESS_FIN")
    private BigDecimal impayCessFin;

    @Column(name = "IMPAYES_FRAIS_FIN")
    private BigDecimal impayesFraisFin;

    @Column(name = "EXTCPT_FIN")
    private BigDecimal extcptFin;

    @Column(name = "STATUT_DEBUT")
    private String statutDebut;

    @Column(name = "STATUT_FIN")
    private String statutFin;

    @Column(name = "CLASSE_CONTAGION")
    private String classeContagion;

    @Column(name = "CES_TTC_DEF")
    private BigDecimal cesTtcDef;

    @Column(name = "GARANTIE_DEF")
    private BigDecimal garantieDef;

    @Column(name = "AGIOS_RESERVES_DEF")
    private BigDecimal agiosReservesDef;

    @Column(name = "BASE_PROVISION_DEF")
    private BigDecimal baseProvisionDef;

    @Column(name = "PROVISION_DEF")
    private BigDecimal provisionDef;

    @Column(name = "DOTATION")
    private BigDecimal dotation;

    @Column(name = "RISQUE_DOTATION")
    private BigDecimal risqueDotation;

    @Column(name = "DATE_MEP")
    private Date dateMep;

    @Column(name = "VIN_IDENTIFIANT_CLIENT")
    private String vinIdentifiantClient;

    @Column(name = "VOUT_AGE_DUREE_PRET")
    private Integer voutAgeDureePret;

    @Column(name = "VOUT_ANCIENNETE_ACT_PART")
    private Integer voutAncienneteActPart;

    @Column(name = "VOUT_ANCI_ACTIVITE_ENTR")
    private Integer voutAnciActiviteEntr;

    @Column(name = "VOUT_NBR_INCIDENTS")
    private Integer voutNbrIncidents;

    @Column(name = "CATEGORIE")
    private String categorie;

    @Column(name = "CSP")
    private String csp;

    @Column(name = "TYPE_CLIENT")
    private String typeClient;

    @Column(name = "CODE_REGLEMENT_CG")
    private String codeReglementCg;

    @Column(name = "FLAG_SINISTRE")
    private Boolean flagSinistre;

}

