package cs10;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Library for graph analysis
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2016
 * @author ninaparipovic
 * 
 */
public class GraphLib {
	/**
	 * Takes a random walk from a vertex, up to a given number of steps
	 * So a 0-step path only includes start, while a 1-step path includes start and one of its out-neighbors,
	 * and a 2-step path includes start, an out-neighbor, and one of the out-neighbor's out-neighbors
	 * Stops earlier if no step can be taken (i.e., reach a vertex with no out-edge)
	 * @param g		graph to walk on
	 * @param start	initial vertex (assumed to be in graph)
	 * @param steps	max number of steps
	 * @return		a list of vertices starting with start, each with an edge to the sequentially next in the list;
	 * 			    null if start isn't in graph
	 */
	public static <V,E> List<V> randomWalk(Graph<V,E> g, V start, int steps) { // check if no neighbors 
		if(!g.hasVertex(start)) return null;
		List<V> walk = new ArrayList<V>();
		// add the start vertex to the list
		walk.add(start);
		int i=0;
		V currentVertex = start;
		while (i<steps) {
			if (g.outDegree(currentVertex)==0){
				break;
			}
			Iterable<V> neighbors = g.outNeighbors(currentVertex);
			ArrayList<V> randomList = new ArrayList<V>();
			for (V neighbor: neighbors) {
				randomList.add(neighbor);
			}
			currentVertex = randomList.get((int)Math.random()*randomList.size());
			walk.add(currentVertex);
			i++;
		}
		return walk;	
	}
	
	
	/**
	 * Orders vertices in decreasing order by their in-degree
	 * @param g		graph
	 * @return		list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 */
	public static <V,E> List<V> verticesByInDegree(Graph<V,E> g) {
		class verticesComparator implements Comparator<V>{
			public int compare(V v1, V v2) {
				if(g.inDegree(v1)>g.inDegree(v2)) return 1;
				else if(g.inDegree(v1)<g.inDegree(v2)) return -1;
				else return 0;
			}
		}
		Iterable<V> vertices = g.vertices();
		List<V> vList = new ArrayList<V>();
		for (V v: vertices) vList.add(v);
		vList.sort(new verticesComparator());
		return vList;	
	}
	/**
	 * Orders vertices in decreasing order by their in-degree
	 * @param g		graph
	 * @param n		n number of items returned based on in-degree
	 * @return		list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 */
	public static <V,E> List<V> verticesByInDegree(Graph<V,E> g, int n){
		class verticesComparator implements Comparator<V>{
			public int compare(V v1, V v2) {
				if(g.inDegree(v1)>g.inDegree(v2)) return 1;
				else if(g.inDegree(v1)<g.inDegree(v2)) return -1;
				else return 0;
			}
		}
		Iterable<V> vertices = g.vertices();
		List<V> vList = new ArrayList<V>();
		for (V v: vertices) vList.add(v);
		vList.sort(new verticesComparator());
		return vList.subList(0, n);
	}
	
	
	/**
	 * Returns a map based on BFS search
	 * @param g				graph
	 * @param source		vertex that BFS starts from 
	 * @return				graph 
	 */
	public static <V,E> AdjacencyMapGraph<V,Set<E>> bfs(Graph<V,HashSet<E>> g, V source){
		AdjacencyMapGraph<V,Set<E>> pathTree = new AdjacencyMapGraph<V,Set<E>>();
		pathTree.insertVertex(source);
		Set<V> visited = new HashSet<V>();
		Queue<V> queue = new LinkedList<V>();
		
		queue.add(source);
		visited.add(source);
		while(!queue.isEmpty()) {
			V vertex = queue.remove();
			for (V v: g.outNeighbors(vertex)) { // loop over out neighbors 
				if(!visited.contains(v)) {
					visited.add(v);
					queue.add(v);
					pathTree.insertVertex(v);
					Set<E> movies = g.getLabel(v, vertex); 
					pathTree.insertDirected(v, vertex, movies);
				}
			}
		}
		return pathTree;
	}
	
	
	/**
	 * Orders vertices in decreasing order by their in-degree
	 * @param tree		graph
	 * @param v			end vertex
	 * @return			list of vertices that are in the path from the start vertex to the given vertex v
	 */
	public static <V,E> List<V> getPath(Graph<V,Set<E>> tree, V v){
		List<V> vertexList = new ArrayList(); 
		V current = v;
		
		while (tree.outDegree(current) > 0) {
			vertexList.add(0, current);
			Iterable<V> set = tree.outNeighbors(current); // can I just cast it to a (V) or do I have to remove from set or something
			for (V nextVertex: set) { // obtain the neighbor from the returned set of out neighbors 
				current = nextVertex;
			}
		}
		// add last thing to the list 
		vertexList.add(0, current);
		return vertexList;
	}
	
	/**
	 * Given a graph and a subgraph (here shortest path tree), determine which vertices are in the graph
	 * but not the subgraph (here, not reached by BFS).
	 * @param graph		graph with all the vertices
	 * @param subgraph	graph obtained from the BFS
	 * @return set		a set of all the missing vertices
	 */
	public static <V,E> Set<V> missingVertices(Graph<V,HashSet<E>> graph, Graph<V,Set<E>> subgraph){
		Set<V> missing = new HashSet<V>();
		for (V v:graph.vertices()) {
			if(!subgraph.hasVertex(v)) missing.add(v);
		}
		return missing;
	}
		
	
	/**
	 * 
	 * @param tree bfs tree
	 * @param root the starting vertex
	 * @return double average separation
	 */
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
		double total = avSepHelper(tree, root, 0);
		return total/(tree.numVertices());

	}
	// helper method for averageSeperation
	public static <V,E> double avSepHelper(Graph<V,E> tree, V currentV, double length) {
		double total = length;
		if (tree.inNeighbors(currentV) != null) {
			for (V vertex:tree.inNeighbors(currentV)) {
				total += avSepHelper(tree, vertex, length+1);
			}
		}
		return total;	
			
	}
	/**
	 * sorts all the vertices within a graph based on average path length 
	 * @param subgraph
	 * @param root
	 * @param n
	 * @return
	 */
	public static <V,E> List<V> averagePathLengthSorter(Graph<V,Set<E>> subgraph,  V root, Integer n){
		HashMap<V,Double> map = new HashMap<V,Double>();
		for (V v: subgraph.vertices()) {
			double sep = GraphLib.averageSeparation(subgraph, root);
			map.put(v, sep);
			
		}
		class avLengthComparator implements Comparator<V>{
			public int compare(V v1, V v2) {
				if(map.get(v1)>map.get(v2)) return 1;
				else if(map.get(v1)<map.get(v2)) return -1;
				else return 0;
			}
		}
		List<V> vList = new ArrayList<V>();
		for (V v: map.keySet())	{
			vList.add(v);
		}
		vList.sort(new avLengthComparator());
		return vList.subList(0, n);
		

		
		
		
	}
	
}
