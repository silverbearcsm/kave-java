package cc.kave.commons.model.groum;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import cc.kave.commons.model.groum.nodes.ActionNode;
import cc.kave.commons.model.pattexplore.PattExplorer;
import static cc.kave.commons.model.groum.PatternAssert.assertContainsPatterns;
import static cc.kave.commons.model.groum.PatternAssert.filterBySize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static cc.kave.commons.model.groum.GroumTestUtils.*;

public class PattExplorerGraphTest {

	@Test
	public void distinguishesPatternsByStructure() {
		//      1          1     1
		//    /   \        |     | \
		//   2     3  =>   2  ,  2  3
		//   |             |
		//   3             3
		
		Groum subject = new Groum();
		ActionNode node1 = new ActionNode("1", "1");
		subject.addNode(node1);
		ActionNode node2 = new ActionNode("2", "2");
		subject.addNode(node2);
		ActionNode node3a = new ActionNode("3", "3");
		subject.addNode(node3a);
		ActionNode node3b = new ActionNode("3", "3");
		subject.addNode(node3b);

		subject.addEdge(node1, node2);
		subject.addEdge(node1, node3a);
		subject.addEdge(node2, node3b);

		PattExplorer uut = new PattExplorer(1);
		List<SubGroum> actuals = uut.explorePatterns(Arrays.asList(subject));

		Groum patternA = new Groum();
		patternA.addNode(node1);
		patternA.addNode(node2);
		patternA.addNode(node3a);
		patternA.addEdge(node1, node2);
		patternA.addEdge(node1, node3a);

		Groum patternB = new Groum();
		patternB.addNode(node1);
		patternB.addNode(node2);
		patternB.addNode(node3b);
		patternB.addEdge(node1, node2);
		patternB.addEdge(node2, node3b);
		List<SubGroum> patternsOfSize = filterBySize(actuals, 3);
		assertContainsPatterns(patternsOfSize, patternA, patternB);
	}

	/*
	 * 			1    			1			1
	 * 		 /     \ 			|		   / \
	 *      2       3 (!)		2		  2   3
	 *    /   \   /     		|		
	 *   3  ->  4      			3			   
	 *           \				|	
	 *            5				4	
	 * 							|
	 * 							5
	 */
	@Test
	public void findsTwoNodesPattern() {
		PattExplorer uut = new PattExplorer(2);
		Groum listGroum = Fixture.createConnectedGroumOfSize(5);

		Groum complexGroum = new Groum();
		INode node1 = new ActionNode("1", "1");
		INode node2 = new ActionNode("2", "2");
		INode node3a = new ActionNode("3", "3");
		INode node3b = new ActionNode("3", "3");
		INode node4 = new ActionNode("4", "4");
		INode node5 = new ActionNode("5", "5");
		complexGroum.addNode(node1);
		complexGroum.addNode(node2);
		complexGroum.addNode(node3a);
		complexGroum.addNode(node3b);
		complexGroum.addNode(node4);
		complexGroum.addNode(node5);
		complexGroum.addEdge(node1, node2);
		complexGroum.addEdge(node1, node3a);
		complexGroum.addEdge(node2, node4);
		complexGroum.addEdge(node2, node3b);
		complexGroum.addEdge(node3a, node4);
		complexGroum.addEdge(node3b, node4);
		complexGroum.addEdge(node4, node5);

		Groum structuredGroum = new Groum();
		structuredGroum.addNode(node1);
		structuredGroum.addNode(node2);
		structuredGroum.addNode(node3a);
		structuredGroum.addEdge(node1, node2);
		structuredGroum.addEdge(node1, node3a);

		Groum patternA = Fixture.createConnectedGroumOfSize(3);
		Groum patternB = Fixture.createConnectedGroumOfSize(3, 5);

		List<SubGroum> actuals = uut.explorePatterns(Arrays.asList(complexGroum, structuredGroum, listGroum));
		List<SubGroum> patternsOfSize = filterBySize(actuals, 3);
		assertContainsPatterns(patternsOfSize, patternA, patternB, structuredGroum);
	}

	/*
	 * 							1			1
	 * 						   / \			|
	 * 					      2   3			2
	 *                       / \   \		|
	 *                      4   3   4		3
	 *                         /			|
	 *                        4				4
	 *                       /				|
	 *                      5				5
	 *                       
	 */
	@Test
	public void findsPatternsInTwoGroums() {
		PattExplorer uut = new PattExplorer(3);
		Groum listGroum = Fixture.createConnectedGroumOfSize(5);
		Groum complexGroum = new Groum();
		INode node1 = new ActionNode("1", "1");
		INode node2 = new ActionNode("2", "2");
		INode node3a = new ActionNode("3", "3");
		INode node3b = new ActionNode("3", "3");
		INode node4a = new ActionNode("4", "4");
		INode node4b = new ActionNode("4", "4");
		INode node5 = new ActionNode("5", "5");
		complexGroum.addNode(node1);
		complexGroum.addNode(node2);
		complexGroum.addNode(node3a);
		complexGroum.addNode(node3b);
		complexGroum.addNode(node4a);
		complexGroum.addNode(node4b);
		complexGroum.addNode(node5);

		complexGroum.addEdge(node1, node2);
		complexGroum.addEdge(node1, node3a);
		complexGroum.addEdge(node2, node4a);
		complexGroum.addEdge(node2, node3b);
		complexGroum.addEdge(node3b, node4a);
		complexGroum.addEdge(node3a, node4b);
		complexGroum.addEdge(node4a, node5);

		List<SubGroum> patterns = uut.explorePatterns(Arrays.asList(listGroum, complexGroum));
		assertTrue(patterns.size() == 3);
	}

	/*
	 * 							1		  		   1				 1		
	 * 						   / \			  	  / \		        / \		
	 * 					     2     2		    2     2	          2     2	
	 *                      / \   / \		   / \   / \	     / \   / \	
	 *                     3   3 3 	 3	      3   3 3 	3	    3   3 3   3	
	 *                         	  \	             /						   \
	 *                        	   4            4		                    4         				
	 */
	@Test
	public void findsPatternsInThreeGroums() {
		PattExplorer uut = new PattExplorer(6);
		Groum groumA = new Groum();
		Groum groumB = new Groum();
		Groum groumC = new Groum();

		INode nodeA_1 = new ActionNode("1", "1");
		INode nodeA_2a = new ActionNode("2", "2");
		INode nodeA_2b = new ActionNode("2", "2");
		INode nodeA_3a = new ActionNode("3", "3");
		INode nodeA_3b = new ActionNode("3", "3");
		INode nodeA_3c = new ActionNode("3", "3");
		INode nodeA_3d = new ActionNode("3", "3");
		INode nodeA_4 = new ActionNode("4", "4");
		groumA.addNode(nodeA_1);
		groumA.addNode(nodeA_2a);
		groumA.addNode(nodeA_2b);
		groumA.addNode(nodeA_3a);
		groumA.addNode(nodeA_3b);
		groumA.addNode(nodeA_3c);
		groumA.addNode(nodeA_3d);
		groumA.addNode(nodeA_4);
		groumA.addEdge(nodeA_1, nodeA_2a);
		groumA.addEdge(nodeA_1, nodeA_2a);
		groumA.addEdge(nodeA_2a, nodeA_3a);
		groumA.addEdge(nodeA_2a, nodeA_3b);
		groumA.addEdge(nodeA_2b, nodeA_3c);
		groumA.addEdge(nodeA_2b, nodeA_3d);
		groumA.addEdge(nodeA_3c, nodeA_4);

		INode nodeB_1 = new ActionNode("1", "1");
		INode nodeB_2a = new ActionNode("2", "2");
		INode nodeB_2b = new ActionNode("2", "2");
		INode nodeB_3a = new ActionNode("3", "3");
		INode nodeB_3b = new ActionNode("3", "3");
		INode nodeB_3c = new ActionNode("3", "3");
		INode nodeB_3d = new ActionNode("3", "3");
		INode nodeB_4 = new ActionNode("4", "4");
		groumB.addNode(nodeB_1);
		groumB.addNode(nodeB_2a);
		groumB.addNode(nodeB_2b);
		groumB.addNode(nodeB_3a);
		groumB.addNode(nodeB_3b);
		groumB.addNode(nodeB_3c);
		groumB.addNode(nodeB_3d);
		groumB.addNode(nodeB_4);
		groumB.addEdge(nodeB_1, nodeB_2a);
		groumB.addEdge(nodeB_1, nodeB_2a);
		groumB.addEdge(nodeB_2a, nodeB_3a);
		groumB.addEdge(nodeB_2a, nodeB_3b);
		groumB.addEdge(nodeB_2b, nodeB_3c);
		groumB.addEdge(nodeB_2b, nodeB_3d);
		groumB.addEdge(nodeB_3b, nodeB_4);

		INode nodeC_1 = new ActionNode("1", "1");
		INode nodeC_2a = new ActionNode("2", "2");
		INode nodeC_2b = new ActionNode("2", "2");
		INode nodeC_3a = new ActionNode("3", "3");
		INode nodeC_3b = new ActionNode("3", "3");
		INode nodeC_3c = new ActionNode("3", "3");
		INode nodeC_3d = new ActionNode("3", "3");
		INode nodeC_4 = new ActionNode("4", "4");
		groumC.addNode(nodeC_1);
		groumC.addNode(nodeC_2a);
		groumC.addNode(nodeC_2b);
		groumC.addNode(nodeC_3a);
		groumC.addNode(nodeC_3b);
		groumC.addNode(nodeC_3c);
		groumC.addNode(nodeC_3d);
		groumC.addNode(nodeC_4);
		groumC.addEdge(nodeC_1, nodeC_2a);
		groumC.addEdge(nodeC_1, nodeC_2a);
		groumC.addEdge(nodeC_2a, nodeC_3a);
		groumC.addEdge(nodeC_2a, nodeC_3b);
		groumC.addEdge(nodeC_2b, nodeC_3c);
		groumC.addEdge(nodeC_2b, nodeC_3d);
		groumC.addEdge(nodeC_3d, nodeC_4);

		List<SubGroum> patterns = uut.explorePatterns(Arrays.asList(groumA, groumB, groumC));
		assertEquals(3, patterns.size());
	}

	@Test
	public void countsOverlappingInstanesOnlyOnce1() {
		INode node1 = new ActionNode("1", "1");
		INode node2a = new ActionNode("2", "2");
		INode node2b = new ActionNode("2", "2");
		Groum overlappingGroum = createGroum(node1, node2a, node2b);
		overlappingGroum.addEdge(node1, node2a);
		overlappingGroum.addEdge(node2a, node2b);

		List<SubGroum> patterns = findPatternsWithMinFrequency(2, overlappingGroum);

		Groum pattern1 = createGroum(node2a);
		assertContainsPatterns(patterns, pattern1);
	}

	@Test
	public void countsOverlappingInstanesOnlyOnce2() {
		INode node1 = new ActionNode("1", "1");
		INode node2a = new ActionNode("2", "2");
		INode node2b = new ActionNode("2", "2");
		Groum overlappingGroum = createGroum(node1, node2a, node2b);
		overlappingGroum.addEdge(node1, node2a);
		overlappingGroum.addEdge(node1, node2b);

		List<SubGroum> patterns = findPatternsWithMinFrequency(2, overlappingGroum);

		Groum pattern1 = createGroum(node2a);
		assertContainsPatterns(patterns, pattern1);
	}

	@Test
	public void findsMultipleInstanceInOneGraph() {
		INode node1a = new ActionNode("1", "1");
		INode node1b = new ActionNode("1", "1");
		INode node2a = new ActionNode("2", "2");
		INode node2b = new ActionNode("2", "2");
		Groum overlappingGroum = createGroum(node1a, node1b, node2a, node2b);
		overlappingGroum.addEdge(node1a, node2a);
		overlappingGroum.addEdge(node1a, node1b);
		overlappingGroum.addEdge(node1b, node2b);

		List<SubGroum> patterns = findPatternsWithMinFrequency(2, overlappingGroum);

		Groum pattern1 = createGroum(node1a, node2a);
		pattern1.addEdge(node1a, node2a);
		patterns = filterBySize(patterns, 2);
		assertContainsPatterns(patterns, pattern1);
	}

	@Test
	public void findsGraphIsomorphism() {
		INode node1 = new ActionNode("1", "1");
		INode node2 = new ActionNode("2", "2");
		INode node3a = new ActionNode("3", "3");
		INode node3b = new ActionNode("3", "3");
		Groum groum1 = createGroum(node1, node2, node3a);
		groum1.addEdge(node1, node2);
		groum1.addEdge(node1, node3a);
		groum1.addEdge(node2, node3a);
		Groum groum2 = createGroum(node1, node2, node3a, node3b);
		groum2.addEdge(node1, node2);
		groum2.addEdge(node1, node3a);
		groum2.addEdge(node2, node3b);

		List<SubGroum> patterns = findPatternsWithMinFrequency(2, groum1, groum2);

		Groum pattern1 = createGroum(node1, node2, node3a);
		pattern1.addEdge(node1, node2);
		pattern1.addEdge(node1, node3a);
		pattern1.addEdge(node2, node3a);
		patterns = filterBySize(patterns, 3);
		assertContainsPatterns(patterns, pattern1);
	}

	@Test
	public void includesAllEdgesBetweenAllIncludedNodes() {
		INode node1 = new ActionNode("1", "1");
		INode node2 = new ActionNode("2", "2");
		INode node3 = new ActionNode("3", "3");
		Groum groum = createGroum(node1, node2, node3);
		groum.addEdge(node1, node2);
		groum.addEdge(node1, node3);
		groum.addEdge(node2, node3);

		List<SubGroum> patterns = findPatternsWithMinFrequency(1, groum);

		patterns = filterBySize(patterns, 3);
		assertContainsPatterns(patterns, groum);
	}

}