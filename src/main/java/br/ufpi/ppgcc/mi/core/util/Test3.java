package br.ufpi.ppgcc.mi.core.util;

import java.io.File;

public class Test3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		deleteDir((new File("C:\\Users\\Irvayne Matheus\\Desktop\\Computação\\Iniciação Científica\\Iniciação2016-2017\\Extração InfoWay 07012017\\teste")));
		
	}
	
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) { 
               boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // Agora o diretório está vazio, restando apenas deletá-lo.
        return dir.delete();
    }

}
