package br.ufpi.ppgcc.mi.core.util;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;

import br.ufpi.ppgcc.mi.core.model.ExtractionPath;
import br.ufpi.ppgcc.mi.core.model.OperationFile;
import br.ufpi.ppgcc.mi.core.model.Repository;
import br.ufpi.ppgcc.mi.core.model.Revision;
import br.ufpi.ppgcc.mi.core.repository.GitUtil;

public class Test {

	public static void main(String[] args) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		Repository repository = new Repository();
		//para repositorios do git lab, deve ser adicionado o .git no final. PAra git hub nao eh necessario
		repository.setUrl("https://gitlab.com/Admin_Siasar/SIASAR-2.0.git");
		ExtractionPath path = new ExtractionPath();
		path.setPath("master");
		repository.getExtractionPaths().add(path);
		
		//GitUtil util = new GitUtil(repository.getUrl(),path.getPath(), "irvaynematheus@yahoo.com","ronaldo920021995");
		GitUtil util = new GitUtil(repository.getUrl(),path.getPath());
		
		repository.setRevisions(util.getRevisions());
		
		for(Revision aux: repository.getRevisions()){
			System.out.println(aux.getExternalId());
			System.out.println(aux.getAuthor());
			System.out.println(aux.getDate());
			System.out.println("Total de arquivos = "+aux.getTotalFiles());
			
			System.out.println("Arquivos na revisao");
			for(OperationFile file : aux.getFiles()){
				System.out.println("Nome do arquivo = "+file.getPath());
				System.out.println("Linhas add = "+file.getLineAdd());
				System.out.println("Linhas mod = "+file.getLineMod());
				System.out.println("Linhas rem = "+file.getLineDel());
				System.out.println("Linhas cond = "+file.getLineCondition());
				
			}
			
			System.out.println();
		}
		System.out.println();
	}

}
