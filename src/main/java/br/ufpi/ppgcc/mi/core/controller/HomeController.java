/**
 * 
 */
package br.ufpi.ppgcc.mi.core.controller;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.ufpi.ppgcc.mi.common.annotation.Public;

/**
 * @author Werney Ayala
 *
 */
@Controller
public class HomeController {
	
	
	@Public
	@Get("/")
	public void home() { }
	
}
