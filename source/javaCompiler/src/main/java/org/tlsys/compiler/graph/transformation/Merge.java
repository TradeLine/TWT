package org.tlsys.compiler.graph.transformation;


import org.tlsys.compiler.ast.Block;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.tlsys.compiler.graph.*;

public class Merge extends Transformation
{

	private Node tail;

	private Set inEdgesForTail;

	public Merge()
	{
	}

	public Merge(ControlFlowGraph theGraph)
	{
		graph= theGraph;
	}

	public boolean applies_()
	{
		Set<Node> headerSet= new LinkedHashSet<Node>();
		headerSet.add(header);

		for (Node child : header.getDomChildren())
		{
			if (EdgeCollections.getSources(child.getInEdges()).equals(headerSet))
			{
				tail= child;
				return true;
			}
		}

		return false;
	}

	public void apply_()
	{

		inEdgesForTail= graph.removeInEdges(tail);

		graph.rerootOutEdges(tail, newNode, false);

		graph.removeNode(tail);
	}

	void rollOut_(Block block)
	{
		Block labeledBlock= block;

		Iterator iter= inEdgesForTail.iterator();
		while (iter.hasNext())
		{
			Edge edge= (Edge) iter.next();
			if (!edge.isGlobal())
				continue;

			if (labeledBlock == block)
			{
				labeledBlock= new Block();
				block.appendChild(labeledBlock);
			}

			produceJump(edge, labeledBlock);
		}

		graph.rollOut(header, labeledBlock);
		graph.rollOut(tail, block);
		block.appendChildren(newNode.block);
	}

	public String toString()
	{
		return super.toString() + "(" + header + ", " + tail + ")";
	}
}
