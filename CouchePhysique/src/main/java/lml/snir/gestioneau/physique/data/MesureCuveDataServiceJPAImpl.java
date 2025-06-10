package lml.snir.gestioneau.physique.data;

import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import lml.snir.gestioneau.metier.entity.MesureCuve;
import lml.snir.persistence.jpa.AbstracCrudServiceJPA;
import lml.snir.tools.DateConverter;

/**
 *
 * @author saturne
 */
public class MesureCuveDataServiceJPAImpl extends AbstracCrudServiceJPA<MesureCuve> implements MesureCuveDataService {

    MesureCuveDataServiceJPAImpl(String PU) {
        super(PU);
    }

    @Override
    public List<MesureCuve> getByDate(Date date) throws Exception {
        String strDate = DateConverter.formatDate(date)+"%";
        List<MesureCuve> mesureCuve = null;

        try {
            this.open();
            Query query = em.createQuery("SELECT m FROM MesureCuve m WHERE m.date LIKE :fdate");
            query.setParameter("fdate", strDate);
            mesureCuve = query.getResultList();
        } catch (NoResultException ex) {
            return null;
        } finally {
            this.close();
        }
        return mesureCuve;
    }

    @Override
    public MesureCuve getLastTemperature() throws Exception {
        MesureCuve mesureCuve = null;

        try {
            this.open();
            Query query = em.createQuery("SELECT m FROM MesureCuve m ORDER BY m.date DESC");
            query.setMaxResults(1);

            List<MesureCuve> resultList = query.getResultList();
            if (resultList.isEmpty()) {
                System.out.println("Aucun résultat trouvé");
            } else {
                System.out.println("Dernière Température de la Cuve : " + resultList.get(0));
            }

            if (!resultList.isEmpty()) {
                mesureCuve = resultList.get(0);
            }
        } catch (NoResultException ex) {
            System.out.println("Aucune Température de la Cuve trouvé : " + ex.getMessage());
            return null;
        } finally {
            this.close();
        }

        return mesureCuve;
    } 

}
