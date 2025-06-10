package lml.snir.gestioneau.metier.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author saturne
 */
@Entity
@DiscriminatorValue("ConsommationPluie")
public class ConsommationPluie extends Consommation{
    
}
