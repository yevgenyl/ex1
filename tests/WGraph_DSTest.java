package ex1.tests;

import ex1.src.WGraph_DS;
import ex1.src.weighted_graph;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WGraph_DSTest {
    static long start,end; // Runtime variables.

    @BeforeAll
    static void beforeAll(){
        System.out.println("--- Starting test for WGraph_DS class ---");
        start = new Date().getTime();
    }

    /**
     * Test simple empty graph
     */
    @Test
    void testEmptyGraph(){
        weighted_graph g = new WGraph_DS();
        assertEquals(0,g.nodeSize());
        assertEquals(0,g.edgeSize());
        assertEquals(0,g.getMC());
    }

    /**
     * Test addNode method by creating one node graph.
     */
    @Test
    void testOneNodeGraph(){
        weighted_graph g = new WGraph_DS();
        g.addNode(3);
        assertEquals(1,g.nodeSize());
        assertEquals(0,g.edgeSize());
        assertEquals(1,g.getMC());
        assertEquals(3,g.getNode(3).getKey());
    }

    /**
     * Test connect method.
     */
    @Test
    void testConnectTwoNodes(){
        weighted_graph g = new WGraph_DS();
        g.addNode(4);
        g.addNode(7);
        g.connect(4,7,30);
        assertTrue(g.hasEdge(4,7));
        assertTrue(g.hasEdge(7,4));
        assertEquals(1,g.edgeSize());
        assertEquals(3,g.getMC());
        assertEquals(30,g.getEdge(4,7));
        assertEquals(30,g.getEdge(7,4));
    }

    /**
     * Test remove method
     */
    @Test
    void testRemoveNode(){
        weighted_graph g = new WGraph_DS();
        g.addNode(4);
        g.addNode(0);
        g.addNode(-1);
        assertEquals(null,g.getNode(-1));
        g.connect(4,0,14);
        assertEquals(1,g.edgeSize());
        g.removeNode(4);
        g.removeNode(4);
        assertNull(g.getNode(4));
        assertEquals(0,g.edgeSize());
    }

    /**
     * Runtime test for graph creation.
     * Creates random graph with 100,000 vertices and edge number <= 1,000,000.
     * This test should run no more than 10 seconds.
     */
    @Test
    void testRuntime(){
        long startTime = new Date().getTime();
        int v = 100000, e = v*10;
        weighted_graph g = graphCreator(v,e);
        long endTime = new Date().getTime();
        double dt = (endTime-startTime)/1000.0;
        assertTrue(dt < 10);
    }

    /**
     * Test graph's copy constructor
     */
    @Test
    void testCopyConstructor(){
        weighted_graph g1 = new WGraph_DS();
        g1.addNode(1);
        weighted_graph g2 = new WGraph_DS(g1);
        assertEquals(g1,g2);
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                weighted_graph g3 = new WGraph_DS(null);
            }
        });
        g1.removeNode(1);
        g1.addNode(1);
        assertEquals(g1,g2);
        g1.removeNode(1);
        assertNotEquals(g1,g2);
    }

    /**
     * Test hashEdge() graph method.
     */
    @Test
    void testHasEdge(){
        weighted_graph g = new WGraph_DS();
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.addNode(4);
        g.addNode(4);
        g.addNode(5);
        g.connect(1,4,20);
        g.connect(1,4,15);
        assertTrue(g.hasEdge(1,4));
        assertFalse(g.hasEdge(-4,-10));
        assertFalse(g.hasEdge(1,1));
    }

    /**
     * Test getEdge() graph method.
     */
    @Test
    void testGetEdge(){
        weighted_graph g = new WGraph_DS();
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.addNode(4);
        g.addNode(4);
        g.addNode(5);
        g.connect(1,4,20);
        g.connect(1,4,15);
        assertEquals(15,g.getEdge(1,4));
    }

    /**
     * Test adding negative nodes.
     * Test adding nodes wich already exist.
     */
    @Test
    void testAddIllegalNode(){
        weighted_graph g = new WGraph_DS();
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                g.addNode(-1);
            }
        });
        g.addNode(2);
        g.addNode(2);
        assertEquals(1,g.nodeSize());
    }

    /**
     * Test the graph's connect method for illegal values.
     *
     */
    @Test
    void testConnectIllegalValues(){
        weighted_graph g = new WGraph_DS();
        g.addNode(1);
        g.connect(1,1,50);
        assertNotEquals(1,g.edgeSize());
    }

    /**
     * Test graph's getV methods.
     */
    @Test
    void testGetV(){
        weighted_graph g = new WGraph_DS();
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.addNode(4);
        g.addNode(4);
        g.addNode(5);
        g.connect(1,4,20);
        g.connect(1,4,15);
        g.connect(1,5,0);
        g.connect(3,4,13.5);
        g.connect(4,5,1.111);
        assertEquals(2,g.getV(1).size());
        g.removeEdge(1,5);
        assertEquals(1,g.getV(1).size());
        assertEquals(5,g.getV().size());
    }

    /**
     * Test graph's removeEdge() method.
     */
    @Test
    void testRemoveEdge(){
        weighted_graph g = new WGraph_DS();
        g.removeEdge(1,3);
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.addNode(4);
        g.addNode(4);
        g.addNode(5);
        g.connect(1,4,15);
        g.connect(1,5,0);
        g.connect(1,3,13.5);
        g.connect(1,2,1.111);
        assertEquals(4,g.edgeSize());
        g.removeEdge(1,3);
        assertEquals(3,g.edgeSize());
        g.removeEdge(1,1);
        assertEquals(3,g.edgeSize());
    }

    /**
     * Test graph's nodeSize() method.
     */
    @Test
    void testNodeSize(){
        weighted_graph g = graphCreator(30,0);
        assertEquals(30,g.nodeSize());
        g.connect(0,1,30);
        assertEquals(30,g.nodeSize());
        g.removeNode(1);
        assertEquals(29,g.nodeSize());
    }

    /**
     * Test graph's edgeSize() method.
     */
    @Test
    void testEdgeSize(){
        weighted_graph g = graphCreator(30,0);
        assertEquals(30,g.nodeSize());
        g.connect(0,1,30);
        assertEquals(1,g.edgeSize());
        g.removeNode(1);
        assertEquals(0,g.edgeSize());
    }

    /////////////////////////// Private methods ///////////////////////////

    /**
     * Private function for creating random graph with seed.
     * @param v - number of vertices.
     * @param e - number of edges.
     * @return
     */
    private weighted_graph graphCreator(int v, int e){
        weighted_graph g = new WGraph_DS();
        Random r = new Random(1);
        for(int i = 0; i < v; i++)
            g.addNode(i);
        for(int i = 0; i < e; i++) {
            int a = r.nextInt(v);
            int b = r.nextInt(v);
            g.connect(a,b,r.nextDouble());
        }
        return g;
    }

    @AfterAll
    static void afterAll(){
        System.out.println("--- End of WGraph_DS class test ---");
        end = new Date().getTime();
        double dt = (end-start)/1000.0;
        System.out.println("--- Finished in "+dt+" seconds ---");
    }
}
