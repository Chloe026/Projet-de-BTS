package lml.snir.gestioneau.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import lml.snir.gestioneau.metier.entity.*;
import lml.snir.gestioneau.physique.data.ConsommationDataService;
import lml.snir.gestioneau.physique.data.ConsommationPluieDataService;
import lml.snir.gestioneau.physique.data.ConsommationVilleDataService;
import lml.snir.gestioneau.physique.data.CuveDataService;
import lml.snir.gestioneau.physique.data.DebitDataService;
import lml.snir.gestioneau.physique.data.MesureCuveDataService;
import lml.snir.gestioneau.physique.data.MesurePompeDataService;
import lml.snir.gestioneau.physique.data.NiveauEauDataService;
import lml.snir.gestioneau.physique.data.PhysiqueDataFactory;
import lml.snir.gestioneau.physique.data.PompeDataService;
import lml.snir.gestioneau.physique.data.PressionDataService;
import lml.snir.gestioneau.physique.data.RandomValueGenerator;
import lml.snir.gestioneau.physique.data.TemperatureDataService;

public class TestJUnit {

    private final ConsommationPluieDataService consPluieSrv;
    private final ConsommationVilleDataService consVilleSrv;
    private final PressionDataService pressionSrv;
    private final TemperatureDataService tempSrv;
    private final DebitDataService debitSrv;
    private final CuveDataService cuveSrv;
    private final NiveauEauDataService niveaueauSrv;
    private final PompeDataService pompeSrv;
    private final MesureCuveDataService mesurecuveSrv;
    private final MesurePompeDataService mesurepompeSrv;
    private final ConsommationDataService consSrv;

    // Information de connexion à la base de données
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gestionEau";
    private static final String DB_USER = "gestionEau";
    private static final String DB_PASSWORD = "8Tqr8ARcWdpyP2w6";

    private Cuve cuve = new Cuve();
    private Pompe pompe = new Pompe();

    public TestJUnit() throws Exception {
        this.consPluieSrv = PhysiqueDataFactory.getConsommationPluieDataService();
        this.consVilleSrv = PhysiqueDataFactory.getConsommationVilleDataService();
        this.pressionSrv = PhysiqueDataFactory.getPressionDataService();
        this.tempSrv = PhysiqueDataFactory.getTemperatureDataService();
        this.debitSrv = PhysiqueDataFactory.getDebitDataService();
        this.cuveSrv = PhysiqueDataFactory.getCuveDataService();
        this.niveaueauSrv = PhysiqueDataFactory.getNiveauEauDataService();
        this.pompeSrv = PhysiqueDataFactory.getPompeDataService();
        this.mesurecuveSrv = PhysiqueDataFactory.getMesureCuveDataService();
        this.mesurepompeSrv = PhysiqueDataFactory.getMesurePompeDataService();
        this.consSrv = PhysiqueDataFactory.getConsommationDataService();
    }

    @Before
    public void setUp() throws Exception {
        System.out.println("setUp called");
        // Supprimer la base de données à chaque lancement
        PhysiqueDataFactory.getDrop().drop();

        // Peupler les données après suppression de la base
        populate();
    }

    @Test
    public void test() throws Exception {
        this.testGetConsommationPluie();
        this.testGetConsommationVille();
        this.testGetMesureCuve();
        this.testGetMesurePompe();
        this.testGetNiveauEau();
        // Test pour récupérer les dernières valeurs
        this.testGetLastConsommationPluie();
        this.testGetLastConsommationVille();
        this.testGetLastNiveauEau();
        this.testGetLastMesureCuve();
        this.testGetLastTemperature();
        this.testGetLastPression();
        this.testGetLastDebit();

    }

    private void populate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // Générer un mode aléatoire avec la classe RandomValueGenerator
        String modeAleatoire = RandomValueGenerator.generateRandomMode();

        // Appliquer ce mode à la cuve
        this.cuve.setMode(modeAleatoire);
        this.cuve.setId(1L);
        this.cuveSrv.update(cuve);

        // Remplissage des données dans la base
        for (int i = 0; i < 5; i++) {
            addMesureCuve();
            addMesurePompeTemperature();
            addMesurePompeDebit();
            addMesurePompePression();

        }
    }

    public void addMesurePompeGrandeurPhysique(GrandeurPhysique grandeurPhysique) throws Exception {
        Pompe p = this.pompe;

        if (p.getId() == null) {
            // Si la pompe n'a pas d'ID, on peut l'ajouter à la base de données
            p = this.pompeSrv.add(p);
        }

        // Créer une mesure pour la pompe avec la grandeur physique passée en paramètre
        MesurePompe mesurePompe = new MesurePompe();
        mesurePompe.setGrandeurPhysique(grandeurPhysique);
        mesurePompe.setPompe(p);
        mesurePompe.setDate(RandomValueGenerator.generateRandomDate());
        mesurePompe = this.mesurepompeSrv.add(mesurePompe);
    }

    public void addMesurePompeTemperature() throws Exception {
        Temperature t = new Temperature();
        t.setValue(RandomValueGenerator.generateRandomDouble(-20, 50)); // valeurs minimum : -20 et valeurs max : 50
        t = this.tempSrv.add(t);
        addMesurePompeGrandeurPhysique(t);
    }

    public void addMesurePompePression() throws Exception {
        Pression p = new Pression();
        p.setValue(RandomValueGenerator.generateRandomDouble(0, 3)); // valeurs minimum : 0 et valeurs max : 3
        p = this.pressionSrv.add(p);
        addMesurePompeGrandeurPhysique(p);
    }

    public void addMesurePompeDebit() throws Exception {
        Debit d = new Debit();
        d.setValue(RandomValueGenerator.generateRandomDouble(0, 30)); // valeurs minimum : 0L/minutes et valeurs max : 30L/minutes
        d = this.debitSrv.add(d);
        addMesurePompeGrandeurPhysique(d);
    }

    public void addMesureCuve() throws Exception {
        Cuve c = this.cuve;

        if (c.getId() == null) {
            // Si la cuve n'a pas d'ID, on peut l'ajouter à la base de données
            c = this.cuveSrv.add(c);
        }

        MesureCuve mesureCuve = new MesureCuve();
        Temperature t = new Temperature();
        t.setValue(RandomValueGenerator.generateRandomDouble(1, 17)); // valeurs minimum : 1 et valeurs max : 17
        t = this.tempSrv.add(t);

        ConsommationPluie consommationpluie;
        ConsommationVille consommationville;
        Random random = new Random();

        consommationville = new ConsommationVille();
        consommationpluie = new ConsommationPluie();

        consommationville.setDate(RandomValueGenerator.generateRandomDate());
        consommationville.setValue(RandomValueGenerator.generateRandomDouble(10, 300)); // valeurs minimum : 10 et valeurs max : 300
        consommationville = this.consVilleSrv.add(consommationville);

        consommationpluie.setDate(RandomValueGenerator.generateRandomDate());
        consommationpluie.setValue(RandomValueGenerator.generateRandomDouble(10, 300)); // valeurs minimum : 10 et valeurs max : 300
        consommationpluie = this.consPluieSrv.add(consommationpluie);

        NiveauEau niveauEau = new NiveauEau();
        niveauEau.setValue(RandomValueGenerator.generateRandomDouble(0, 20)); // valeurs minimum : 0 et valeurs max : 20
        niveauEau.setDate(RandomValueGenerator.generateRandomDate());
        niveauEau = this.niveaueauSrv.add(niveauEau);

        mesureCuve.setGrandeurPhysique(t);
        mesureCuve.setCuve(c);
        mesureCuve.setConsommationVille(consommationville);
        mesureCuve.setConsommationPluie(consommationpluie);
        mesureCuve.setNiveauEau(niveauEau);
        mesureCuve.setDate(RandomValueGenerator.generateRandomDate());
        mesureCuve = this.mesurecuveSrv.add(mesureCuve);
    }


    public void testGetConsommationPluie() throws Exception {
        
        Date currentDate = new Date();

        // Récupérer les consommations de pluie pour la date actuelle
        List<ConsommationPluie> consoPluies = consPluieSrv.getByDate(currentDate);

        // Vérifications
        assertNotNull("La liste des consommations de pluie ne doit pas être nulle", consoPluies);
        assertFalse("La liste des consommations de pluie ne doit pas être vide", consoPluies.isEmpty());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Date du jour : " + sdf.format(currentDate));

        for (ConsommationPluie consoPluie : consoPluies) {
            System.out.println("Consommation Pluie - Date: " + consoPluie.getDate() + ", Value: " + consoPluie.getValue());
        }
    }

    public void testGetConsommationVille() throws Exception {
        
        // Récupérer la date actuelle du système
        Date currentDate = new Date();

        // Récupérer les consommations de ville pour la date actuelle
        List<ConsommationVille> consoVilles = consVilleSrv.getByDate(currentDate);

        // Vérifications (assertions)
//        assertEquals("Il devrait y avoir une consommation de ville pour cette date", 1, consoVilles.size());
        assertNotNull("La liste des consommations de ville ne doit pas être nulle", consoVilles);
        assertFalse("La liste des consommations de ville ne doit pas être vide", consoVilles.isEmpty());

        // Affichage des valeurs des consommations récupérées
        for (ConsommationVille conso : consoVilles) {
            System.out.println("Consommation Ville - Date: " + conso.getDate() + ", Value: " + conso.getValue());
        }
    }

    public void testGetNiveauEau() throws Exception {
        
        Date currentDate = new Date();

        // Récupérer les consommations de niveaueau pour la date actuelle
        List<NiveauEau> niveauxEau = niveaueauSrv.getByDate(currentDate);
        
        // Vérifications
        assertNotNull("La liste des niveaux d'eau ne doit pas être nulle", niveauxEau);
        assertFalse("La liste des niveaux d'eau ne doit pas être vide", niveauxEau.isEmpty());

        for (NiveauEau niveau : niveauxEau) {
            System.out.println("Niveau d'Eau - Date: " + niveau.getDate() + ", Value: " + niveau.getValue());
        }
    }

    public void testGetMesureCuve() throws Exception {
        
        Date currentDate = new Date();

        // Récupérer les consommations de MesureCuve pour la date actuelle
        List<MesureCuve> mesuresCuve = mesurecuveSrv.getByDate(currentDate);

        // Vérifications
        assertNotNull("La liste des mesures de cuve ne doit pas être nulle", mesuresCuve);
        assertFalse("La liste des mesures de cuve ne doit pas être vide", mesuresCuve.isEmpty());

        for (MesureCuve mesure : mesuresCuve) {
            System.out.println("Mesure Cuve - Date: " + mesure.getDate() + ", Value: " + mesure.getGrandeurPhysique().getValue());
        }
    }

    public void testGetMesurePompe() throws Exception {
        
        Date currentDate = new Date();

        // Récupérer les consommations de MesureCuve pour la date actuelle
        List<MesurePompe> mesurespompe = mesurepompeSrv.getByDate(currentDate);

        // Vérifications
        assertNotNull("La liste des mesures de pompe ne doit pas être nulle", mesurespompe);
        assertFalse("La liste des mesures de pompe ne doit pas être vide", mesurespompe.isEmpty());

        for (MesurePompe mp : mesurespompe) {
            System.out.println("MesurePompe - Date: " + mp.getDate()
                    + ", Value: " + mp.getGrandeurPhysique().getValue()
                    + ", Pump: " + (mp.getPompe() != null ? mp.getPompe().toString() : "No pump"));
        }
    }

    
    
    
    // Méthode GetLast
    
    
    
    
    public void testGetLastConsommationPluie() throws Exception {
        // Récupérer la dernière valeur enregistrée dans la base de données
        Consommation consopluielast = consSrv.getLastConsommationPluie();

        // Vérifier que la valeur n'est pas nulle
        assertNotNull("La consommation de pluie ne doit pas être nulle", consopluielast);

        // Obtenir la valeur de la consommation
        double derniereValeur = consopluielast.getValue();

        // Afficher la valeur de la consommation
        System.out.println("La dernière valeur de consommation de pluie est : " + derniereValeur);

        // Vérifier que la valeur de la consommation est correcte (en fonction des valeurs dans la basse de données)
//        assertEquals("La consommation de pluie devrait être 177.6", 177.6, consopluielast.getValue(), 0.01);
    }

    
    public void testGetLastConsommationVille() throws Exception {
        
        Consommation consovillelast = consSrv.getLastConsommationVille();

        assertNotNull("La consommation de ville ne doit pas être nulle", consovillelast);

        double derniereValeur = consovillelast.getValue();

        System.out.println("La dernière valeur de consommation de ville est : " + derniereValeur);

//        assertEquals("La consommation de ville devrait être 174.6", 174.6, consovillelast.getValue(), 0.01);
    }

    
    public void testGetLastNiveauEau() throws Exception {
        
        NiveauEau niveaueaulast = niveaueauSrv.getLast();

        assertNotNull("Le niveau d'eau ne doit pas être nul", niveaueaulast);

        double derniereValeur = niveaueaulast.getValue();

        System.out.println("La dernière valeur du niveau d'eau est : " + derniereValeur);

//        assertEquals("Le niveau d'eau devrait être 15.8", 15.8, niveaueaulast.getValue(), 0.01);
    }

    
    public void testGetLastMesureCuve() throws Exception {
        
        MesureCuve mesurecuvelast = mesurecuveSrv.getLastTemperature();

        assertNotNull("La mesure de cuve ne doit pas être nulle", mesurecuvelast);

        Temperature temperature = (Temperature) mesurecuvelast.getGrandeurPhysique();
        double derniereValeurDebit = temperature.getValue();

        System.out.println("La dernière valeur de la tempéraure de la cuve est : " + derniereValeurDebit);

//        assertEquals("La valeur de la mesure de cuve devrait être 16.2", 16.2, mesurecuvelast.getGrandeurPhysique().getValue(), 0.01);
    }

    
    public void testGetLastTemperature() throws Exception {
        
        MesurePompe lastMesurePompe = mesurepompeSrv.getLastTemperature();

        assertNotNull("La mesure de pompe ne doit pas être nulle", lastMesurePompe);

        Temperature temeprature = (Temperature) lastMesurePompe.getGrandeurPhysique();
        double derniereValeurDebit = temeprature.getValue();

        System.out.println("La dernière valeur de la tempéraure de la pompe est : " + derniereValeurDebit);

//        assertEquals("La température de la pompe devrait être -17.0", -17.0, lastMesurePompe.getGrandeurPhysique().getValue(), 0.01);
    }

    
    public void testGetLastPression() throws Exception {
        
        MesurePompe mesurepompelastPression = mesurepompeSrv.getLastPression();

        assertNotNull("La mesure de pression ne doit pas être nulle", mesurepompelastPression);

        Pression pression = (Pression) mesurepompelastPression.getGrandeurPhysique();
        double derniereValeurDebit = pression.getValue();

        System.out.println("La dernière valeur de la pression de la pompe est : " + derniereValeurDebit);

//        assertEquals("La valeur de la pression devrait être 1.3", 1.3, mesurepompelastPression.getGrandeurPhysique().getValue(), 0.01);
    }

    
    public void testGetLastDebit() throws Exception {
        
        MesurePompe mesurepompelastDebit = mesurepompeSrv.getLastDebit();

        assertNotNull("La mesure de débit ne doit pas être nulle", mesurepompelastDebit);

        Debit debit = (Debit) mesurepompelastDebit.getGrandeurPhysique();
        double derniereValeurDebit = debit.getValue();

        System.out.println("La dernière valeur du débit de la pompe est : " + derniereValeurDebit);

//        assertEquals("La valeur du débit devrait être 9.3", 9.3, mesurepompelastDebit.getGrandeurPhysique().getValue(), 0.01);
    }

}
