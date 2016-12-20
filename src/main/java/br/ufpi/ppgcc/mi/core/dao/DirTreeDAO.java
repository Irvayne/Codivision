/**
 * 
 */
package br.ufpi.ppgcc.mi.core.dao;

import br.ufpi.ppgcc.mi.common.dao.GenericDAO;
import br.ufpi.ppgcc.mi.core.model.DirTree;

/**
 * @author Werney Ayala
 *
 */
public class DirTreeDAO extends GenericDAO<DirTree>{

	/**
	 * @param entityClass
	 */
	public DirTreeDAO() {
		super(DirTree.class);
	}
	
}
