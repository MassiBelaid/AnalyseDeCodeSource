package analyse;

import java.io.File;
import java.io.IOException;
import java.util.List;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableGraph;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import static guru.nidi.graphviz.model.Factory.*;

public class GraphVizMain {

	public static void main(String[] args) {

		Graph g = graph("example1").directed()
				.graphAttr().with(Rank.dir(RankDir.LEFT_TO_RIGHT))
				//.nodeAttr().with(Font.name("arial"))
				.linkAttr().with("class", "link-class")
				.with(
						node("a").with(Color.RED).link(node("b")).link(node("c")),
						node("b").link(
								to(node("c"))//.with(attr("weight", 5), Style.DASHED)
								)
						);
		
				g.with(node("g"));
		try {
			Graphviz.fromGraph(g).height(100).render(Format.PNG).toFile(new File("./ex1.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MutableGraph g2 = mutGraph("example1").setDirected(true).add(
		        mutNode("a").add(Color.RED).addLink(mutNode("b")));
		g2.add(mutNode("TEST"));
		try {
			Graphviz.fromGraph(g2).width(200).render(Format.PNG).toFile(new File("./ex1m.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*------------------------------------------------------------------------*/
		MutableGraph graphDeDependances = mutGraph("graphe de dépendances").setDirected(true);
		//graphDeDependances.add(mutNode(name))
		
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
		System.out.println(classList.get(0).getSimpleName());
		
		
		
	/***************CONSTRUCTION DU GRAPH************************************/
		
		
		for(CtClass clsss : classList) {
			graphDeDependances.add(mutNode(clsss.getSimpleName()));
			clsss.getReference();
			//encienne = clsss;
			
			for(CtTypeReference ref : clsss.getElements(new TypeFilter<> (CtTypeReference.class))) {
				
				for(CtClass cls2 : classList){
					if(cls2.getSimpleName().equals(ref.toString())) {
						graphDeDependances.add(mutNode(clsss.getSimpleName()).addLink(mutNode(ref.toString())));
					}
				}
			}
		}
		
		
		try {
			Graphviz.fromGraph(graphDeDependances).width(7500)/*.height(5000)*/.render(Format.PNG).toFile(new File("./graphe_de_dependances.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
