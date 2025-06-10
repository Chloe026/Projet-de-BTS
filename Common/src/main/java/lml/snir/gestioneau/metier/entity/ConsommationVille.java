package lml.snir.gestioneau.metier.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author saturne
 */
@Entity
@DiscriminatorValue("ConsommationVille")
public class ConsommationVille extends Consommation{
    
}
