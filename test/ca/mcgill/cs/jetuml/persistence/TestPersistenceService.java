/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.persistence;

import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.build;
import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.findRootNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectCollaborationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectReferenceEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ActorNode;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.diagram.nodes.StateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.UseCaseNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestPersistenceService
{
	private static final String TEST_FILE_NAME = "testdata/tmp";
	
	/**
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
	}
	
	private int numberOfRootNodes(Diagram pDiagram)
	{
		int sum = 0;
		for( @SuppressWarnings("unused") Node node : pDiagram.rootNodes() )
		{
			sum++;
		}
		return sum;
	}
	
	private int numberOfEdges(Diagram pDiagram)
	{
		int sum = 0;
		for( @SuppressWarnings("unused") Edge edge : pDiagram.edges() )
		{
			sum++;
		}
		return sum;
	}
	
	@Test
	public void testClassDiagram() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService.class.jet"));
		verifyClassDiagram(graph);
		
		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp);
		verifyClassDiagram(graph);
		tmp.delete();
	}
	
	@Test
	public void testClassDiagramContainment() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService2.class.jet"));
		verifyClassDiagram2(graph);
		
		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp);
		verifyClassDiagram2(graph);
		tmp.delete();
	}
	
	@Test
	public void testSequenceDiagram() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService.sequence.jet"));
		verifySequenceDiagram(graph);
		
		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp);
		verifySequenceDiagram(graph);
		tmp.delete();
	}
	
	@Test
	public void testStateDiagram() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService.state.jet"));
		verifyStateDiagram(graph);

		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp);
		verifyStateDiagram(graph);
		tmp.delete();
	}
	
	@Test
	public void testObjectDiagram() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService.object.jet"));
		verifyObjectDiagram(graph);

		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp);
		verifyObjectDiagram(graph);
		tmp.delete();
	}
	
	@Test
	public void testUseCaseDiagram() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService.usecase.jet"));
		verifyUseCaseDiagram(graph);

		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp);
		verifyUseCaseDiagram(graph);
		tmp.delete();
	}
	
	private void verifyUseCaseDiagram(Diagram pDiagram)
	{
		assertEquals(9, numberOfRootNodes(pDiagram));
		UseCaseNode u1 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build("name", "Use case 1"));
		UseCaseNode u2 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build("name", "Use case 2"));
		UseCaseNode u3 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build("name", "Use case 3"));
		ActorNode a1 = (ActorNode) findRootNode(pDiagram, ActorNode.class, build("name", "Actor"));
		ActorNode a2 = (ActorNode) findRootNode(pDiagram, ActorNode.class, build("name", "Actor2"));
		NoteNode n1 = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		PointNode p1 = (PointNode) findRootNode(pDiagram, PointNode.class, build());
		UseCaseNode u4 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build("name", "Use case 4"));
		ActorNode a3 = (ActorNode) findRootNode(pDiagram, ActorNode.class, build("name", "Actor3"));
		
		assertEquals(new Rectangle(440, 40, 110, 40), u1.view().getBounds());
		assertEquals("Use case 1", u1.getName().toString());
		
		assertEquals(new Rectangle(460, 130, 110, 40), u2.view().getBounds());
		assertEquals("Use case 2", u2.getName().toString());
		
		assertEquals(new Rectangle(460, 230, 110, 40), u3.view().getBounds());
		assertEquals("Use case 3", u3.getName().toString());
		
		assertTrue(new Rectangle(270, 50, 48, 88).equals(a1.view().getBounds()) || new Rectangle(270, 50, 48, 87).equals(a1.view().getBounds()));
		assertEquals("Actor", a1.getName().toString());
		
		assertTrue(new Rectangle(280, 230, 48, 88).equals(a2.view().getBounds()) || new Rectangle(280, 230, 48, 87).equals(a2.view().getBounds()));
		assertEquals("Actor2", a2.getName().toString());
		
		assertEquals("A note", n1.getName());
		assertEquals(new Rectangle(700, 50, 60, 40), n1.view().getBounds());
		
		assertEquals(new Rectangle(567, 56, 0, 0), p1.view().getBounds());
		
		assertEquals(new Rectangle(650, 150, 110, 40), u4.view().getBounds());
		assertEquals("Use case 4", u4.getName().toString());
		
		assertTrue(new Rectangle(190, 140, 48, 88).equals(a3.view().getBounds()) || new Rectangle(190, 140, 48, 87).equals(a3.view().getBounds()));
		assertEquals("Actor3", a3.getName().toString());
		
		assertEquals(10,  numberOfEdges(pDiagram));
		Iterator<Edge> eIt = pDiagram.edges().iterator();
		
		NoteEdge cr1 = (NoteEdge) eIt.next();
		UseCaseGeneralizationEdge cr2 = (UseCaseGeneralizationEdge) eIt.next();
		UseCaseDependencyEdge cr3 = (UseCaseDependencyEdge) eIt.next();
		UseCaseAssociationEdge cr4 = (UseCaseAssociationEdge) eIt.next();
		UseCaseAssociationEdge cr5 = (UseCaseAssociationEdge) eIt.next();
		UseCaseAssociationEdge cr6 = (UseCaseAssociationEdge) eIt.next();
		UseCaseGeneralizationEdge cr7 = (UseCaseGeneralizationEdge) eIt.next();
		UseCaseDependencyEdge cr8 = (UseCaseDependencyEdge) eIt.next();
		UseCaseDependencyEdge cr9 = (UseCaseDependencyEdge) eIt.next();
		UseCaseAssociationEdge cr10 = (UseCaseAssociationEdge) eIt.next();	
		
		assertEquals(new Rectangle(565,54,135,13),cr1.view().getBounds());
		assertTrue(cr1.getStart() == n1);
		assertTrue(cr1.getEnd() == p1);
		
		assertTrue(new Rectangle(207,87,63,53).equals(cr2.view().getBounds()) || new Rectangle(236,135,53,43).equals(cr2.view().getBounds()));
		assertTrue(cr2.getStart() == a3);
		assertTrue(cr2.getEnd() == a1);
		
		assertTrue(207 == cr3.view().getBounds().getX() || 236 == cr3.view().getBounds().getX());
		assertTrue(225 == cr3.view().getBounds().getY() || 177 == cr3.view().getBounds().getY());
		assertTrue(89 == cr3.view().getBounds().getWidth() || 74 == cr3.view().getBounds().getWidth());
		assertTrue(43 == cr3.view().getBounds().getHeight() || 92 == cr3.view().getBounds().getHeight());
		assertTrue( cr3.getStart() == a3);
		assertTrue( cr3.getEnd() == a2);
		assertTrue( cr3.getType() == UseCaseDependencyEdge.Type.Extend);
		
		assertTrue(new Rectangle(316,53,124,35).equals(cr4.view().getBounds()) || new Rectangle(316,53,124,36).equals(cr4.view().getBounds()));
		assertTrue( cr4.getStart() == a1 );
		assertTrue( cr4.getEnd() == u1 );
		
		assertTrue(new Rectangle(298,143,162,87).equals(cr5.view().getBounds()) || new Rectangle(297,143,163,87).equals(cr5.view().getBounds()));
		assertTrue( cr5.getStart() == a2 );
		assertTrue( cr5.getEnd() == u2 );
		
		assertTrue(new Rectangle(326,243,134,26).equals(cr6.view().getBounds()) || new Rectangle(326,243,134,25).equals(cr6.view().getBounds()));
		assertTrue( cr6.getStart() == a2 );
		assertTrue( cr6.getEnd() == u3 );
		
		assertEquals(new Rectangle(487,78,23,52),cr7.view().getBounds());
		assertTrue( cr7.getStart() == u2 );
		assertTrue( cr7.getEnd() == u1 );

		assertEquals(new Rectangle(503,169,60,62),cr8.view().getBounds());
		assertTrue( cr8.getStart() == u2 );
		assertTrue( cr8.getEnd() == u3 );
		assertTrue( cr8.getType() == UseCaseDependencyEdge.Type.Include);
		
		assertEquals(new Rectangle(568,136,93,31),cr9.view().getBounds());
		assertTrue( cr9.getStart() == u2 );
		assertTrue( cr9.getEnd() == u4 );
		assertTrue( cr9.getType() == UseCaseDependencyEdge.Type.Extend);
		
		assertEquals(new Rectangle(548,53,102,112),cr10.view().getBounds());
		assertTrue( cr10.getStart() == u1 );
		assertTrue( cr10.getEnd() == u4 );
 	}
	
	private void verifyClassDiagram2(Diagram pDiagram)
	{
		assertEquals(4, numberOfRootNodes(pDiagram));
		
		PackageNode p1 = (PackageNode) findRootNode(pDiagram, PackageNode.class, build("name", "p1"));
		PackageNode p2 = (PackageNode) findRootNode(pDiagram, PackageNode.class, build("name", "p2"));
		PackageNode p3 = (PackageNode) findRootNode(pDiagram, PackageNode.class, build("name", "p3"));
		
		assertEquals(new Rectangle(310, 230, 120, 100), p1.view().getBounds());
		assertEquals("p1", p1.getName().toString());
		
		List<ChildNode> children = p1.getChildren();
		assertEquals(1, children.size());
		ClassNode c1 = (ClassNode) children.get(0);
		assertEquals(new Rectangle(320, 260, 100, 60), c1.view().getBounds());
		assertEquals(p1, c1.getParent());
		assertEquals("C1", c1.getName().toString());

		assertEquals("p2", p2.getName().toString());
		assertEquals(new Rectangle(477, 130, 100, 80), p2.view().getBounds());
		children = p2.getChildren();
		assertEquals(0, children.size());

		assertEquals("p3", p3.getName().toString());
		assertEquals(new Rectangle(620, 270, 310, 140), p3.view().getBounds());
		children = p3.getChildren();
		assertEquals(1,children.size());
		PackageNode p4 = (PackageNode) children.get(0);
		assertEquals("p4", p4.getName().toString());
		assertEquals(new Rectangle(630, 300, 290, 100), p4.view().getBounds());
		
		children = p4.getChildren();
		assertEquals(2,children.size());
		InterfaceNode i1 = (InterfaceNode) children.get(0);
		assertEquals(new Rectangle(640, 330, 100, 60), i1.view().getBounds());
		ClassNode c2 = (ClassNode) children.get(1);
		assertEquals(new Rectangle(810, 330, 100, 60), c2.view().getBounds());
		assertEquals("C2", c2.getName().toString());
		
		NoteNode n1 = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		assertEquals(new Rectangle(490, 160, 60, 40), n1.view().getBounds());
		assertEquals("n1", n1.getName().toString());

		assertEquals(3, numberOfEdges(pDiagram));
		Iterator<Edge> eIterator = pDiagram.edges().iterator();
		
		DependencyEdge e1 = (DependencyEdge) eIterator.next();
		DependencyEdge e2 = (DependencyEdge) eIterator.next();
		DependencyEdge e3 = (DependencyEdge) eIterator.next();
		
		assertEquals("e1", e1.getMiddleLabel().toString());
		assertEquals("e2", e2.getMiddleLabel().toString());
		assertEquals("e3", e3.getMiddleLabel().toString());
		
		assertEquals( c1, e1.getStart());
		assertEquals( i1, e1.getEnd());
		
		assertEquals( c2, e2.getStart());
		assertEquals( i1, e2.getEnd());
		
		assertEquals( p3, e3.getStart());
		assertEquals( p2, e3.getEnd());
	}
	
	private void verifyClassDiagram(Diagram pDiagram)
	{
		assertEquals(7, numberOfRootNodes(pDiagram));
		
		ClassNode node1 = (ClassNode) findRootNode(pDiagram, ClassNode.class, build("name", "Class1"));
		InterfaceNode node2 = (InterfaceNode) findRootNode(pDiagram, InterfaceNode.class, build("name", "\u00ABinterface\u00BB\n"));
		ClassNode node3 = (ClassNode) (ClassNode) findRootNode(pDiagram, ClassNode.class, build("name", "Class2"));
		ClassNode node4 = (ClassNode) findRootNode(pDiagram, ClassNode.class, build("name", "Class3"));
		PackageNode node6 = (PackageNode) findRootNode(pDiagram, PackageNode.class, build("name", "Package"));
		NoteNode node5 = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		PointNode node8 = (PointNode) findRootNode(pDiagram, PointNode.class, build());
		
		assertEquals("", node1.getAttributes());
		assertEquals("", node1.getMethods());
		assertEquals("Class1", node1.getName());
		assertNull(node1.getParent());
		assertEquals(new Rectangle(460, 370, 100, 60), node1.view().getBounds());
		
		assertEquals("", node2.getMethods());
		assertEquals("\u00ABinterface\u00BB\n", node2.getName());
		assertNull(node2.getParent());
		assertEquals(new Rectangle(460, 250, 100, 60), node2.view().getBounds());
		
		assertEquals("foo", node3.getAttributes());
		assertEquals("bar", node3.getMethods());
		assertEquals("Class2", node3.getName());
		assertNull(node3.getParent());
		assertEquals(new Rectangle(460, 520, 100, 69), node3.view().getBounds());
		
		assertEquals("", node4.getAttributes());
		assertEquals("", node4.getMethods());
		assertEquals("Class3", node4.getName());
		assertNull(node4.getParent());
		assertEquals(new Rectangle(630, 370, 100, 60), node4.view().getBounds());
		
		assertEquals("A note", node5.getName());
		assertEquals(new Rectangle(700, 530, 60, 40), node5.view().getBounds());
		
		List<ChildNode> children = node6.getChildren();
		assertEquals(1, children.size());
		ClassNode node7 = (ClassNode) children.get(0);
		assertEquals("", node6.getContents());
		assertEquals("Package", node6.getName());
		assertNull(node6.getParent());
		assertEquals(new Rectangle(270, 340, 120, 100), node6.view().getBounds());

		assertEquals("", node7.getAttributes());
		assertEquals("", node7.getMethods());
		assertEquals("Class", node7.getName());
		assertEquals(node6,node7.getParent());
		assertEquals(new Rectangle(280, 370, 100, 60), node7.view().getBounds());
		
		assertEquals(new Rectangle(694, 409, 0, 0), node8.view().getBounds());
		
		Iterator<Edge> eIterator = pDiagram.edges().iterator();
		
		NoteEdge edge5 = (NoteEdge) eIterator.next();
		assertEquals(new Rectangle(692, 407, 32, 123), edge5.view().getBounds());
		assertEquals(node5, edge5.getStart());
		assertEquals(node8, edge5.getEnd());
		
		DependencyEdge edge6 = (DependencyEdge) eIterator.next();
		assertEquals(new Rectangle(378, 381, 82, 24), edge6.view().getBounds());
		assertEquals(node7, edge6.getEnd());
		assertEquals("", edge6.getEndLabel());
		assertEquals("e1", edge6.getMiddleLabel());
		assertEquals(node1, edge6.getStart());
		assertEquals("", edge6.getStartLabel());
		
		GeneralizationEdge edge1 = (GeneralizationEdge) eIterator.next();
		assertEquals(new Rectangle(503, 308, 23, 62), edge1.view().getBounds());
		assertEquals(node2, edge1.getEnd());
		assertEquals("", edge1.getEndLabel());
		assertEquals("e2", edge1.getMiddleLabel());
		assertEquals(node1, edge1.getStart());
		assertEquals("", edge1.getStartLabel());
		
		GeneralizationEdge edge2 = (GeneralizationEdge) eIterator.next();
		assertEquals(new Rectangle(503, 428, 23, 92), edge2.view().getBounds());
		assertEquals(node1, edge2.getEnd());
		assertEquals("", edge2.getEndLabel());
		assertEquals("e3", edge2.getMiddleLabel());
		assertEquals(node3, edge2.getStart());
		assertEquals("", edge2.getStartLabel());
		
		AggregationEdge edge3 = (AggregationEdge) eIterator.next();
		assertEquals(new Rectangle(558, 376, 72, 24), edge3.view().getBounds());
		assertEquals(node4, edge3.getEnd());
		assertEquals("*", edge3.getEndLabel());
		assertEquals("e4", edge3.getMiddleLabel());
		assertEquals(node1, edge3.getStart());
		assertEquals("1", edge3.getStartLabel());
		
		AggregationEdge edge4 = (AggregationEdge) eIterator.next();
		assertEquals(new Rectangle(559, 399, 72, 155), edge4.view().getBounds());
		assertEquals(node3, edge4.getEnd());
		assertEquals("", edge4.getEndLabel());
		assertEquals("e5", edge4.getMiddleLabel());
		assertEquals(node4, edge4.getStart());
		assertEquals("", edge4.getStartLabel());
	}
	
	private void verifySequenceDiagram(Diagram pDiagram)
	{
		assertEquals(5, numberOfRootNodes(pDiagram));
		
		ImplicitParameterNode object1 = (ImplicitParameterNode) findRootNode(pDiagram, ImplicitParameterNode.class, build("name", "object1:Type1"));
		ImplicitParameterNode object2 = (ImplicitParameterNode) findRootNode(pDiagram, ImplicitParameterNode.class, build("name", ":Type2"));
		ImplicitParameterNode object3 = (ImplicitParameterNode) findRootNode(pDiagram, ImplicitParameterNode.class, build("name", "object3:"));
		NoteNode note = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		PointNode point = (PointNode) findRootNode(pDiagram, PointNode.class, build());
		
		assertEquals(new Rectangle(160,0,90,250), object1.view().getBounds());
		List<ChildNode> o1children = object1.getChildren();
		assertEquals(2, o1children.size());
		assertEquals("object1:Type1", object1.getName().toString());
		CallNode init = (CallNode) o1children.get(0);
		CallNode selfCall = (CallNode) o1children.get(1);
		
		assertEquals(new Rectangle(370,0,80,210), object2.view().getBounds());
		List<ChildNode> o2children = object2.getChildren();
		assertEquals(1, o2children.size());
		assertEquals(":Type2", object2.getName().toString());
		CallNode o2Call = (CallNode) o2children.get(0);
		
		assertEquals(new Rectangle(590,0,80,190), object3.view().getBounds());
		List<ChildNode> o3children = object3.getChildren();
		assertEquals(1, o3children.size());
		assertEquals("object3:", object3.getName().toString());
		CallNode o3Call = (CallNode) o3children.get(0);
		
		assertEquals(new Rectangle(197,80,16,150), init.view().getBounds());
		assertEquals(object1, init.getParent());
		assertFalse(init.isOpenBottom());
		
		assertEquals(new Rectangle(205,100,16,110), selfCall.view().getBounds());
		assertEquals(object1, selfCall.getParent());
		assertFalse(selfCall.isOpenBottom());
		
		assertEquals(new Rectangle(402,120,16,70), o2Call.view().getBounds());
		assertEquals(object2, o2Call.getParent());
		assertFalse(o2Call.isOpenBottom());
		
		assertEquals(new Rectangle(622,140,16,30), o3Call.view().getBounds());
		assertEquals(object3, o3Call.getParent());
		assertFalse(o3Call.isOpenBottom());
		
		assertEquals(new Rectangle(440,200,60,40), note.view().getBounds());
		assertEquals("A note", note.getName().toString());
		
		assertEquals(new Rectangle(409,189,0,0), point.view().getBounds());
	
		assertEquals(6, numberOfEdges(pDiagram));
		Iterator<Edge> eIterator = pDiagram.edges().iterator();
		
		CallEdge self = (CallEdge) eIterator.next(); 
		CallEdge signal = (CallEdge) eIterator.next(); 
		CallEdge call1 = (CallEdge) eIterator.next(); 
		ReturnEdge ret1 = (ReturnEdge) eIterator.next(); 
		ReturnEdge retC = (ReturnEdge) eIterator.next(); 
		NoteEdge nedge = (NoteEdge) eIterator.next(); 
		
		assertEquals(new Rectangle(212, 76, 73, 30), self.view().getBounds());
		assertEquals(selfCall, self.getEnd());
		assertEquals("selfCall()", self.getMiddleLabel());
		assertEquals(init, self.getStart());
		assertFalse(self.isSignal());
		
		assertEquals(new Rectangle(220, 101, 183, 19), signal.view().getBounds());
		assertEquals(o2Call, signal.getEnd());
		assertEquals("signal", signal.getMiddleLabel());
		assertEquals(selfCall, signal.getStart());
		assertTrue(signal.isSignal());
		
		assertEquals(new Rectangle(417, 121, 206, 24), call1.view().getBounds());
		assertEquals(o3Call, call1.getEnd());
		assertEquals("call1()", call1.getMiddleLabel());
		assertEquals(o2Call, call1.getStart());
		assertFalse(call1.isSignal());
		
		assertEquals(new Rectangle(416, 151, 206, 24), ret1.view().getBounds());
		assertEquals(o2Call, ret1.getEnd());
		assertEquals("r1", ret1.getMiddleLabel());
		assertEquals(o3Call, ret1.getStart());
		
		assertEquals(new Rectangle(219, 183, 183, 12), retC.view().getBounds());
		assertEquals(selfCall, retC.getEnd());
		assertEquals("", retC.getMiddleLabel());
		assertEquals(o2Call, retC.getStart());
		
		assertEquals(new Rectangle(407, 187, 33, 17), nedge.view().getBounds());
		assertEquals(point, nedge.getEnd());
		assertEquals(note, nedge.getStart());
	}
	
	private void verifyStateDiagram(Diagram pDiagram)
	{
		assertEquals(7, numberOfRootNodes(pDiagram));
		
		StateNode s1 = (StateNode) findRootNode(pDiagram, StateNode.class, build("name", "S1"));
		StateNode s2 = (StateNode) findRootNode(pDiagram, StateNode.class, build("name", "S2"));
		StateNode s3 = (StateNode) findRootNode(pDiagram, StateNode.class, build("name", "S3"));
		InitialStateNode start = (InitialStateNode) findRootNode(pDiagram, InitialStateNode.class, build());
		FinalStateNode end = (FinalStateNode) findRootNode(pDiagram, FinalStateNode.class, build());
		NoteNode note = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		PointNode point = (PointNode) findRootNode(pDiagram, PointNode.class, build());
		
		assertEquals(new Rectangle(250, 100, 80, 60), s1.view().getBounds());
		assertEquals("S1", s1.getName().toString());
		
		assertEquals(new Rectangle(510, 100, 80, 60), s2.view().getBounds());
		assertEquals("S2", s2.getName().toString());
		
		assertEquals(new Rectangle(520, 310, 80, 60), s3.view().getBounds());
		assertEquals("S3", s3.getName().toString());
		
		assertEquals(new Rectangle(150, 70, 20, 20), start.view().getBounds());
		
		assertEquals(new Rectangle(640, 230, 20, 20), end.view().getBounds());
		
		assertEquals("A note\non two lines", note.getName());
		assertEquals(new Rectangle(690, 320, 78, 40), note.view().getBounds());
		
		assertEquals(new Rectangle(576, 339, 0, 0), point.view().getBounds());
		
		assertEquals(7,  numberOfEdges(pDiagram));
		Iterator<Edge> eIterator = pDiagram.edges().iterator();
		
		NoteEdge ne = (NoteEdge) eIterator.next();
		StateTransitionEdge fromStart = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge e1 = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge e2 = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge self = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge toEnd = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge toS3 = (StateTransitionEdge) eIterator.next(); 
		
		assertEquals(new Rectangle(575, 338, 116, 2), ne.view().getBounds());
		assertEquals(note, ne.getStart());
		assertEquals(point, ne.getEnd());
		
		assertEquals(new Rectangle(168, 81, 82, 29), fromStart.view().getBounds());
		assertEquals(start, fromStart.getStart());
		assertEquals(s1, fromStart.getEnd());
		assertEquals("start", fromStart.getMiddleLabel().toString());
		
		assertEquals(new Rectangle(328, 114, 182, 13), e1.view().getBounds());
		assertEquals(s1, e1.getStart());
		assertEquals(s2, e1.getEnd());
		assertEquals("e1", e1.getMiddleLabel().toString());
		
		assertEquals(new Rectangle(328, 131, 182, 12), e2.view().getBounds());
		assertEquals(s2, e2.getStart());
		assertEquals(s1, e2.getEnd());
		assertEquals("e2", e2.getMiddleLabel().toString());
		
		assertEquals(new Rectangle(545, 55, 60, 60), self.view().getBounds());
		assertEquals(s2, self.getStart());
		assertEquals(s2, self.getEnd());
		assertEquals("self", self.getMiddleLabel().toString());
		
		assertEquals(new Rectangle(580, 245, 63, 65), toEnd.view().getBounds());
		assertEquals(s3, toEnd.getStart());
		assertEquals(end, toEnd.getEnd());
		assertEquals("", toEnd.getMiddleLabel().toString());
		assertEquals(new Rectangle(552, 158, 14, 152), toS3.view().getBounds());
		assertEquals(s2, toS3.getStart());
		assertEquals(s3, toS3.getEnd());
		assertEquals("", toS3.getMiddleLabel().toString());
	}
	
	private void verifyObjectDiagram(Diagram pDiagram)
	{
		assertEquals(7, numberOfRootNodes(pDiagram));
		
		ObjectNode type1 = (ObjectNode) findRootNode(pDiagram, ObjectNode.class, build("name", ":Type1"));
		ObjectNode blank = (ObjectNode) findRootNode(pDiagram, ObjectNode.class, build("name", ""));
		ObjectNode object2 = (ObjectNode) findRootNode(pDiagram, ObjectNode.class, build("name", "object2:"));
		ObjectNode type3 = (ObjectNode) findRootNode(pDiagram, ObjectNode.class, build("name", ":Type3"));

		NoteNode note = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		PointNode p1 = (PointNode) findRootNode(pDiagram, PointNode.class, build("x", 281));
		PointNode p2 = (PointNode) findRootNode(pDiagram, PointNode.class, build("x", 474));
		
		assertEquals(new Rectangle(240, 130, 90, 90), type1.view().getBounds());
		List<ChildNode> children = type1.getChildren();
		assertEquals(1, children.size());
		assertEquals(":Type1", type1.getName().toString());
		
		FieldNode name = (FieldNode) children.get(0);
		assertEquals(new Rectangle(245, 200, 80, 20), name.view().getBounds());
		assertEquals("name", name.getName().toString());
		assertEquals(type1, name.getParent());
		assertEquals("", name.getValue().toString());

		assertEquals(new Rectangle(440, 290, 100, 150), blank.view().getBounds());
		children = blank.getChildren();
		assertEquals(3, children.size());
		assertEquals("", blank.getName().toString());
		FieldNode name2 = (FieldNode) children.get(0);
		FieldNode name3 = (FieldNode) children.get(1);
		FieldNode name4 = (FieldNode) children.get(2);
		
		assertEquals(new Rectangle(445, 360, 90, 23), name2.view().getBounds());
		assertEquals("name2", name2.getName().toString());
		assertEquals(blank, name2.getParent());
		assertEquals("value", name2.getValue().toString());
		
		assertEquals(new Rectangle(445, 388, 90, 23), name3.view().getBounds());
		assertEquals("name3", name3.getName().toString());
		assertEquals(blank, name3.getParent());
		assertEquals("value", name3.getValue().toString());
		
		assertEquals(new Rectangle(445, 416, 90, 23), name4.view().getBounds());
		assertEquals("name4", name4.getName().toString());
		assertEquals(blank, name4.getParent());
		assertEquals("", name4.getValue().toString());

		assertEquals(new Rectangle(540, 150, 80, 60), object2.view().getBounds());
		children = object2.getChildren();
		assertEquals(0, children.size());
		assertEquals("object2:", object2.getName().toString());
		
		assertEquals(new Rectangle(610, 300, 80, 60), type3.view().getBounds());
		children = type3.getChildren();
		assertEquals(0, children.size());
		assertEquals(":Type3", type3.getName().toString());

		assertEquals("A note", note.getName());
		assertEquals(new Rectangle(280, 330, 60, 40), note.view().getBounds());
		
		assertEquals(new Rectangle(281, 216, 0, 0), p1.view().getBounds());
		
		assertEquals(new Rectangle(474, 339, 0, 0), p2.view().getBounds());
		
		Iterator<Edge> eIt = pDiagram.edges().iterator();
		
		ObjectReferenceEdge o1 = (ObjectReferenceEdge) eIt.next();
		ObjectReferenceEdge o2 = (ObjectReferenceEdge) eIt.next();
		ObjectReferenceEdge o3 = (ObjectReferenceEdge) eIt.next();
		NoteEdge ne1 = (NoteEdge) eIt.next();
		NoteEdge ne2 = (NoteEdge) eIt.next();
		ObjectCollaborationEdge cr1 = (ObjectCollaborationEdge) eIt.next();
		
		assertEquals(new Rectangle(319, 174, 32, 37),o1.view().getBounds());
		assertEquals(name, o1.getStart());
		assertEquals(type1, o1.getEnd());
		
		assertEquals(new Rectangle(319, 209, 122, 157),o2.view().getBounds());
		assertEquals(name, o2.getStart());
		assertEquals(blank, o2.getEnd());
		
		assertEquals(new Rectangle(488, 208, 92, 82), cr1.view().getBounds());
		assertEquals(object2, cr1.getEnd());
		assertEquals("", cr1.getEndLabel());
		assertEquals("e1", cr1.getMiddleLabel().toString());
		assertEquals(blank, cr1.getStart());
		assertEquals("", cr1.getStartLabel().toString());
		
		assertEquals(new Rectangle(529, 329, 82, 99), o3.view().getBounds());
		assertEquals(name4, o3.getStart());
		assertEquals(type3, o3.getEnd());
		
		assertEquals(new Rectangle(279, 214, 26, 116), ne1.view().getBounds());
		assertEquals(note, ne1.getStart());
		assertEquals(p1, ne1.getEnd());
		
		assertEquals(new Rectangle(338, 337, 136, 10), ne2.view().getBounds());
		assertEquals(note, ne2.getStart());
		assertEquals(p2, ne2.getEnd());
	}
}
