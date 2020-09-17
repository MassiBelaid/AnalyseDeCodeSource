package analyse;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

public class SpoonMain {

	public static void main(String[] args) {
		
		System.out.println("Begin Analysis");

		// Parsing arguments using JCommander
		Arguments arguments = new Arguments();
		boolean isParsed = arguments.parseArguments(args);

		// if there was a problem parsing the arguments then the program is terminated.
		if(!isParsed)
			return;
		
		// Parsed Arguments
		String experiment_source_code = arguments.getSource();
		String experiment_output_filepath = arguments.getTarget();
		
		// Load project (APP_SOURCE only, no TEST_SOURCE for now)
		Launcher launcher = null;
		if(arguments.isMavenProject() ) {
			launcher = new MavenLauncher(experiment_source_code, MavenLauncher.SOURCE_TYPE.APP_SOURCE); // requires M2_HOME environment variable
		}else {
			launcher = new Launcher();
			launcher.addInputResource(experiment_source_code + "/src");
		}
		
		// Setting the environment for Spoon
		Environment environment = launcher.getEnvironment();
		environment.setCommentEnabled(true); // represent the comments from the source code in the AST
		environment.setAutoImports(true); // add the imports dynamically based on the typeReferences inside the AST nodes.
//		environment.setComplianceLevel(0); // sets the java compliance level.
		
		System.out.println("Run Launcher and fetch model.");
		launcher.run(); // creates model of project
		CtModel model = launcher.getModel(); // returns the model of the project

		
		
		//Nombre de classes de l'application
		List<CtClass> classList = model.getElements(new TypeFilter<CtClass>(CtClass.class));
		int nbClasses = classList.size();
		System.out.println("Nombre de classes de l'application : "+nbClasses);
		//Nombre de classes de l'application : 75
		
		System.out.println("Nombre de lignes de l'application : "+classList.toString().split("\n").length);
		//Nombre de lignes de l'application : 2959
		
		
		
		List<CtMethod> methodList = model.getElements(new TypeFilter<CtMethod>(CtMethod.class));
		int nbMethode = methodList.size();
		System.out.println("Nombre de méthodes de l'application : "+nbMethode);
		//Nombre de méthodes de l'application : 248
		
		
		List<CtPackage> packageList = model.getElements(new TypeFilter<CtPackage>((CtPackage.class)));
		System.out.println("Nombre de packages de l'application : "+packageList.size());
		//Nombre de packages de l'application : 40
		
		
		System.out.println("Nombre moyen de methodes par classe : "+((float)nbMethode/nbClasses));
		//Nombre moyen de methodes par classe : 3.3066666
		
		
		System.out.println("Nombre moyen de lignes de code par methode : "+(float)(methodList.toString().split("\n").length/nbMethode));
		//Nombre moyen de lignes de code par methode : 4.0
		
		
		int nbTotalAttr = 0;
		for(CtClass clss : classList) {
			nbTotalAttr += clss.getFields().size();
		}
		
		System.out.println("Nombre moyen d’attributs par classe : "+(float)nbTotalAttr/nbClasses);
		//Nombre moyen d’attributs par classe : 2.24
		
		
		
		
		List<CtClass> tabClassesMeth = new ArrayList<CtClass>();
		List<CtClass> tabClassesAttr = new ArrayList<CtClass>();
		for(CtClass clss : classList) {
			
			if(tabClassesMeth.size() < nbClasses * 0.1) {
				tabClassesMeth.add(clss);
			}else {
				
				int indexPetit = 0;
				
				for(int i = 1; i<tabClassesMeth.size(); i++) {
					if(tabClassesMeth.get(i).getMethods().size() <= tabClassesMeth.get(indexPetit).getMethods().size()) {
						indexPetit = i;
					}
				}
				
				if(tabClassesMeth.get(indexPetit).getMethods().size() <= clss.getMethods().size()) {
					tabClassesMeth.set(indexPetit, clss);
				}
			}
			
			
			if(tabClassesAttr.size() < nbClasses * 0.1) {
				tabClassesAttr.add(clss);
			}else {
				
				int indexPetit = 0;
				
				for(int i = 1; i<tabClassesAttr.size(); i++) {
					if(tabClassesAttr.get(i).getFields().size() <= tabClassesAttr.get(indexPetit).getFields().size()) {
						indexPetit = i;
					}
				}
				
				if(tabClassesAttr.get(indexPetit).getFields().size() <= clss.getFields().size()) {
					tabClassesAttr.set(indexPetit, clss);
				}
			}
		}
		
		
		//Les 10% des classes qui possedent le plus grand nombre de methodes.
		System.out.println("\n Les 10% des classes qui possèdent le plus grand nombre de méthodes.");
		for(CtClass clss : tabClassesMeth) {
			System.out.println("\t"+clss.getSimpleName()+" Avec : "+clss.getMethods().size()+" méthodes.");
			/*  Les 10% des classes qui possèdent le plus grand nombre de m ethodes.
					BodyRawTypeModel Avec : 10 méthodes.
					CollectionEntity Avec : 22 méthodes.
					HeadersFormInputPanel Avec : 9 méthodes.
					CollectionBodyEntity Avec : 10 méthodes.
					CollectionStructureFolderEntity Avec : 11 méthodes.
					CollectionHeaderEntity Avec : 8 méthodes.
					BodyFormInputPanel Avec : 15 méthodes.
					UrlParser Avec : 8 méthodes.
				*/
		}
		
		
		//Les 10% des classes qui poss`edent le plus grand nombre d’attributs..
				System.out.println("\n Les 10% des classes qui poss`edent le plus grand nombre d’attributs..");
				for(CtClass clss : tabClassesAttr) {
					System.out.println("\t"+clss.getSimpleName()+" Avec : "+clss.getFields().size()+" attributs.");
					/*   Les 10% des classes qui poss`edent le plus grand nombre d’attributs..
							RequestApiButton Avec : 7 attributs.
							ResponseBodyPanel Avec : 7 attributs.
							CollectionEntity Avec : 11 attributs.
							RestPanel Avec : 8 attributs.
							BodyPanel Avec : 7 attributs.
							ServerSentEventPanel Avec : 7 attributs.
							BodyFormInputPanel Avec : 7 attributs.
							SocketIoPanel Avec : 11 attributs.
						*/
				}
		
		for(CtClass clss : tabClassesMeth) {
			if(tabClassesAttr.contains(clss)) {
				System.out.println("Cette classe est contenu dans les deux liste précédentes : "+clss.getSimpleName());
				/*
				 *	Cette classe est contenu dans les deux liste précédentes : CollectionEntity
				 *	Cette classe est contenu dans les deux liste précédentes : BodyFormInputPanel 
				 */
			}
		}
		
		//Les classes qui possèdent plus de X méthodes (la valeur de X est donnée).
		Scanner sc = new Scanner(System.in);
		System.out.println("Entrez un nombre minimum de méthodes : \t");
		int nbMethMinimum = sc.nextInt();
		
		System.out.println("Les classes qui possèdent plus de "+nbMethMinimum+" méthodes : ");
		List<CtClass> listClassenbMethSup = new ArrayList<CtClass>();
		for(CtClass clss : classList) {
			if(clss.getMethods().size() >= nbMethMinimum) {
				listClassenbMethSup.add(clss);
				System.out.println("\t"+clss.getSimpleName()+" avec "+clss.getMethods().size()+" méthodes.");
			}
		}
		/*Resultat avec 15 comme entrée
			 CollectionEntity avec 22 méthodes.
			 BodyFormInputPanel avec 15 méthodes.
		 */
		
	
		
		
		//Nombre maximal de parametres pour une méthode
		int nbParametreMax = 0;
		for(CtMethod mtd : methodList) {
			if(mtd.getParameters().size() > nbParametreMax) {
				nbParametreMax = mtd.getParameters().size();
			}
		}
		System.out.println("Le nombre maximal de paramêtre pour une méthode dans le programme est : "+nbParametreMax);
		//Le nombre maximal de paramêtre pour une méthode dans le programme est : 7
		
		
	}
}
