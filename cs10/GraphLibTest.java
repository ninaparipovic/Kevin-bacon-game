package cs10;

public class GraphLibTest {

	public static void main(String[] args) {
		Graph<String,String> graph = new AdjacencyMapGraph<String,String>();
		
		
		graph.insertVertex("A");
		graph.insertVertex("B");
		graph.insertVertex("C");
		graph.insertVertex("D");
		graph.insertVertex("E");
		graph.insertUndirected("A", "B", "friend");
		graph.insertUndirected("A", "C", "friend");
		graph.insertUndirected("B", "C", "friend");
		graph.insertDirected("A", "D", "friend");
		graph.insertDirected("A", "E", "friend");
		graph.insertDirected("C", "D", "friend");
		graph.insertDirected("E", "B", "friend");
		graph.insertDirected("E", "C", "friend");

		System.out.println(GraphLib.randomWalk(graph, "A", 0));
		System.out.println(GraphLib.randomWalk(graph, "A", 1));
		System.out.println(GraphLib.randomWalk(graph, "A", 2));
		System.out.println(GraphLib.randomWalk(graph, "A", 3));
		System.out.println(GraphLib.randomWalk(graph, "A", 4));
		System.out.println(GraphLib.randomWalk(graph, "A", 5));
		System.out.println(GraphLib.randomWalk(graph, "B", 2));
		System.out.println(GraphLib.randomWalk(graph, "B", 3));
		System.out.println(GraphLib.randomWalk(graph, "E", 1));
		System.out.println(GraphLib.randomWalk(graph, "E", 5));


		
		System.out.println("InDegree vertices:");
		System.out.println("A: " +graph.inDegree("A"));
		System.out.println("B: " +graph.inDegree("B"));
		System.out.println("C: " +graph.inDegree("C"));
		System.out.println("D: " +graph.inDegree("D"));
		System.out.println("E: " +graph.inDegree("E"));

		
		
		System.out.println(graph);
		
		System.out.println("Vertices sorted in order of inDegree, with least first");
		System.out.println(GraphLib.verticesByInDegree(graph));
	}

}
