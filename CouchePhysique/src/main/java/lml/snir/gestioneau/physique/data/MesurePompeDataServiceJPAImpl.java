package lml.snir.gestioneau.physique.data;

import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import lml.snir.gestioneau.metier.entity.MesurePompe;
import lml.snir.persistence.jpa.AbstracCrudServiceJPA;
import lml.snir.tools.DateConverter;

/**
 *
 * @author joris
 */
public class MesurePompeDataServiceJPAImpl extends AbstracCrudServiceJPA<MesurePompe> implements MesurePompeDataService {

    MesurePompeDataServiceJPAImpl(String PU) {
        super(PU);
    }

    @Override
    public List<MesurePompe> getByDate(Date date) throws Exception {
        String strDate = DateConverter.formatDate(date)+"%";
        List<MesurePompe> mesurePompe = null;

        try {
            this.open();
            Query query = em.createQuery("SELECT m FROM MesurePompe m WHERE m.date LIKE :fdate");
            query.setParameter("fdate", strDate);
            mesurePompe = query.getResultList();
        } catch (NoResultException ex) {
            return null;
        } finally {
            this.close();
        }
        return mesurePompe;
    }

    @Override
    public MesurePompe getLastTemperature() throws Exception {
        MesurePompe temperature = null;

        try {
            this.open();
            Query query = em.createQuery(
                    "SELECT mp FROM MesurePompe mp "
                    + "JOIN mp.grandeurPhysique gp "
                    + "WHERE TYPE(gp) = Temperature "
                    + "ORDER BY mp.date DESC"
            );
            query.setMaxResults(1);

            temperature = (MesurePompe) query.getSingleResult();

            System.out.println("Dernière Température de la Pompe: " + temperature);
        } catch (NoResultException ex) {
            System.out.println("Aucun résultat trouvé pour Température de la Pompe : " + ex.getMessage());
            return temperature;
        } finally {
            this.close();
        }

        return temperature;
    }

    @Override
    public MesurePompe getLastPression() throws Exception {
        MesurePompe pression = null;

        try {
            this.open();
            Query query = em.createQuery(
                    "SELECT mp FROM MesurePompe mp "
                    + "JOIN mp.grandeurPhysique gp "
                    + "WHERE TYPE(gp) = Pression "
                    + "ORDER BY mp.date DESC"
            );
            query.setMaxResults(1);

            pression = (MesurePompe) query.getSingleResult();

            System.out.println("Dernière Pression de la Pompe : " + pression);
        } catch (NoResultException ex) {
            System.out.println("Aucun résultat trouvé pour Pression de la Pompe : " + ex.getMessage());
            return pression;
        } finally {
            this.close();
        }

        return pression;
    }

    @Override
    public MesurePompe getLastDebit() throws Exception {
        MesurePompe debit = null;

        try {
            this.open();
            Query query = em.createQuery(
                    "SELECT mp FROM MesurePompe mp "
                    + "JOIN mp.grandeurPhysique gp "
                    + "WHERE TYPE(gp) = Debit "
                    + "ORDER BY mp.date DESC"
            );
            query.setMaxResults(1);

            debit = (MesurePompe) query.getSingleResult();

            System.out.println("Dernier Débit de la Pompe : " + debit);
        } catch (NoResultException ex) {
            System.out.println("Aucun résultat trouvé pour Débit de la Pompe : " + ex.getMessage());
            return debit;
        } finally {
            this.close();
        }

        return debit;
    }

}
