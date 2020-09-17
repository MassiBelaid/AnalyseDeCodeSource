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
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import static guru.nidi.graphviz.model.Factory.*;

public class GraphVizMain {

	public static void main(String[] args) {

		Graph g = graph("example1").directed()
				.graphAttr().with(Rank.dir(RankDir.LEFT_TO_RIGHT))
				.nodeAttr().with(Font.name("arial"))
				.linkAttr().with("class", "link-class")
				.with(
						node("a").with(Color.RED).link(node("b")),
						node("b").link(
								to(node("c"))//.with(attr("weight", 5), Style.DASHED)
								)
						);
		try {
			Graphviz.fromGraph(g).height(100).render(Format.PNG).toFile(new File("./ex1.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MutableGraph g2 = mutGraph("example1").setDirected(true).add(
		        mutNode("a").add(Color.RED).addLink(mutNode("b")));
		try {
			Graphviz.fromGraph(g2).width(200).render(Format.PNG).toFile(new File("./ex1m.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
