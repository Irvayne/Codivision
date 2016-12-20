/**
 * 
 */
package br.ufpi.ppgcc.mi.common.model;

import java.io.Serializable;

/**
 * @author Werney Ayala
 *
 */
public interface PersistenceEntity extends Serializable {

		/**
		 * @return id
		 */
		Long getId();
		
		/**
		 * @param id
		 */
		void setId(Long id);
	
}
