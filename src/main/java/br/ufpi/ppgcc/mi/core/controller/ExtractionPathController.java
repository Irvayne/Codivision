/**
 * 
 */
package br.ufpi.ppgcc.mi.core.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;
import br.ufpi.ppgcc.mi.common.annotation.Permission;
import br.ufpi.ppgcc.mi.core.dao.ExtractionPathDAO;
import br.ufpi.ppgcc.mi.core.dao.RepositoryDAO;
import br.ufpi.ppgcc.mi.core.extractor.model.Extraction;
import br.ufpi.ppgcc.mi.core.extractor.model.ExtractionType;
import br.ufpi.ppgcc.mi.core.extractor.service.TaskService;
import br.ufpi.ppgcc.mi.core.model.Configuration;
import br.ufpi.ppgcc.mi.core.model.DirTree;
import br.ufpi.ppgcc.mi.core.model.ExtractionPath;
import br.ufpi.ppgcc.mi.core.model.OperationFile;
import br.ufpi.ppgcc.mi.core.model.Repository;
import br.ufpi.ppgcc.mi.core.model.Revision;
import br.ufpi.ppgcc.mi.core.model.enums.NodeType;
import br.ufpi.ppgcc.mi.core.model.enums.PermissionType;
import br.ufpi.ppgcc.mi.core.model.validator.ExtractionPathValidator;
import br.ufpi.ppgcc.mi.core.model.vo.AuthorPercentage;
import br.ufpi.ppgcc.mi.core.model.vo.Tree;

/**
 * @author Werney Ayala
 *
 */
@Controller
public class ExtractionPathController {
	
	@Inject private Result result;
	@Inject private RepositoryDAO repositoryDAO;
	@Inject private ExtractionPathDAO dao;
	@Inject private ExtractionPathValidator validator;
	
	@Inject private TaskService taskService;
	
	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}/path/{extractionPathId}/warningpaths")
	public void getWarningPaths(Long repositoryId, Long extractionPathId) {
		
		DirTree dirTree = dao.findById(repositoryId,extractionPathId).getDirTree();
		Configuration configuration = repositoryDAO.getConfiguration(repositoryId);
		double threshold = configuration.getAlertThreshold() / 100.0;
		List<DirTree> warningPaths = new ArrayList<>();
		
		for (DirTree temp : dirTree.getChildren()) {
			warningPaths.addAll(getWarningPaths(repositoryId, "/" + temp.getText(), temp, threshold));
		}
		
		result.use(Results.json()).withoutRoot().from(warningPaths).recursive().serialize();
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/path/{extractionPathId}/tree")
	public void getDirTree(Long repositoryId, Long extractionPathId){
		
		ExtractionPath path= dao.findById(repositoryId,extractionPathId);
		
		if(path.getDirTreeJson()!=null && !path.getDirTreeJson().equals("")){
			result.use(Results.json()).withoutRoot().from(path.getDirTreeJson()).recursive().serialize();
			return;
		}
		
		DirTree dirTree = path.getDirTree();
		DirTree tree = new DirTree();
		tree.setText(dirTree.getText());
		tree.setType(NodeType.FOLDER);
		tree.setChildren(dirTree.getChildren());
		
		result.use(Results.json()).withoutRoot().from(tree).recursive().serialize();
	}
	
	
	private List<DirTree> getWarningPaths(Long repositoryId, String path, DirTree dirTree, double threshold) {

		List<AuthorPercentage> result = repositoryDAO.getPercentage(repositoryId, path);
		List<DirTree> warningPaths = new ArrayList<>();
		
		double total = 0;
		for (AuthorPercentage percentage : result)
			total += percentage.getY();
		
		for (AuthorPercentage percentage : result) {
			if(percentage.getY()/total > threshold){
				warningPaths.add(dirTree);
				return warningPaths;
			}
		}
		
		for(DirTree temp : dirTree.getChildren()) {
			if (temp.getType().equals(NodeType.FOLDER))
				warningPaths.addAll(getWarningPaths(repositoryId, path + "/" + temp.getText(), temp, threshold));
		}
		
		return warningPaths;
	}
	
	@Permission(PermissionType.MEMBER)
	@Post("/repository/{repositoryId}/path/add")
	public void add(Long repositoryId, String path){
		
		ExtractionPath extractionPath = new ExtractionPath();
		extractionPath.setPath(path);
		
		Repository repository = repositoryDAO.findById(repositoryId);
		validator.validateAdd(extractionPath, repository);
		validator.onErrorRedirectTo(RepositoryController.class).show(repositoryId);
		
		DirTree dirTree = new DirTree();
		if(extractionPath.getPath().equals("/"))
			dirTree.setText(repository.getUrl().substring(repository.getRepositoryRoot().length()).substring(1));
		else	
			dirTree.setText(extractionPath.getPath().substring(1));
		dirTree.setType(NodeType.FOLDER);
		extractionPath.setDirTree(dirTree);
		
		repository.getExtractionPaths().add(extractionPath);
		repository = repositoryDAO.save(repository);
		 
		/* Verifica qual o ExtractionPath acabou de ser adicionado
		* isso deve ser feito pois o extractionPAth só recebe um Id
		* após ser salvo no banco */
		//Isso só ocorre se o repositorio for remoto
		if(!repository.isLocal())
			for(ExtractionPath extraction : repository.getExtractionPaths())
				if(extraction.getPath().equals(extractionPath.getPath())){
					taskService.addTask(new Extraction(extraction.getId(), ExtractionType.PATH));
					taskService.addTask(new Extraction(extraction.getId(), ExtractionType.TREE));
				}
		result.redirectTo(RepositoryController.class).show(repositoryId);
	}
	
	@Permission(PermissionType.MEMBER)
	@Get("/repository/{repositoryId}/path/{extractionPathId}/delete")
	public void remove(Long repositoryId, Long extractionPathId){

		validator.validateDelete(extractionPathId, repositoryDAO.findById(repositoryId));
		validator.onErrorRedirectTo(RepositoryController.class).show(repositoryId);
		
		dao.delete(extractionPathId);
		
		result.redirectTo(RepositoryController.class).show(repositoryId);
	}
	
//	@Permission(PermissionType.MEMBER)
//	@Post("/repository/{repositoryId}/treeTest")
//	public void getDirTreeTest(Long repositoryId){
//		
//		Repository repository = repositoryDAO.findById(repositoryId);
//		
//		List<Revision> revision = repository.getRevisions();
//		List<OperationFile> files = new ArrayList<>();
//		for(Revision aux: revision){
//			for(OperationFile aux1 : aux.getFiles()){
//				if(aux1.getPath().contains("Test") || aux1.getPath().contains("test")){
//					files.add(aux1);
//				}
//			}
//		}
//		List<Tree> arvore = new ArrayList<>();
//		for(int i = 0; i<files.size(); i++){
//			String[] tree = files.get(i).getPath().split("/");
//			for(int j = 1; j < tree.length; j++){
//				Tree t = new Tree();
//				t.id = tree[j];
//				t.text = tree[j];
//				if(t.text.contains(".")){
//					t.type = NodeType.FILE;
//				}else{
//					t.type = NodeType.FOLDER;
//				}
//				
//				if(j==1){
//					t.parent = "#";
//				}else{
//					t.parent = tree[j-1];
//				}
//				arvore.add(t);
//				
//			}
//			
//		}
//		
//		
//		
//		ArrayList<Tree> remover = new ArrayList<>();
//		
//		for(int i = 0; i<arvore.size();i++){
//			for(int j = i+1; j<arvore.size(); j++){
//				if(arvore.get(i).id.equals(arvore.get(j).id)){
//					remover.add(arvore.get(j));
//				}
//			}
//		}
//		
//		for(Tree aux:remover){
//			arvore.remove(aux);
//		}
//		
//		for(Tree aux:arvore)
//			System.out.println(aux.id + " -  pai: " +aux.parent );
//		
//		
//		result.use(Results.json()).withoutRoot().from(arvore).recursive().serialize();
//	}
		
}
