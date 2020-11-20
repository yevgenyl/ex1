package ex1.tests;

import ex1.src.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class WGraph_AlgoTest {
    static long start,end; // Runtime test variables.

    @BeforeAll
    static void beforeAll(){
        System.out.println("--- Starting test for WGraph_Algo class ---");
        start = new Date().getTime();
    }

    /**
     * Test graph's copy constructor.
     */
    @Test
    void testDeepCopy(){
        weighted_graph g = new WGraph_DS();
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g);
        assertTrue(ga.isConnected());
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.addNode(4);
        g.connect(1,2,13);
        g.connect(1,3,14);
        g.connect(1,4,15);
        g.connect(2,3,23);
        weighted_graph g1 = ga.copy();
        g1.removeNode(3);
        assertNotNull(g.getNode(3));
        assertEquals(14,g.getEdge(1,3));
        assertNull(g1.getNode(3));
        assertEquals(-1,g1.getEdge(1,3));
    }

    /**
     * Test isConnected graph algorithm.
     */
    @Test
    void testIsConnected(){
        weighted_graph g = new WGraph_DS();
        for(int i = 1; i <= 7; i++){
            g.addNode(i);
        }
        g.connect(1,2,20);
        g.connect(1,5,15);
        g.connect(2,3,20);
        g.connect(5,6,15);
        g.connect(3,4,20);
        g.connect(6,4,15);
        g.connect(1,7,2);
        g.connect(7,4,50);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g);
        assertTrue(ga.isConnected());
        g.removeEdge(7,4);
        g.removeEdge(6,4);
        g.removeEdge(3,4);
        assertFalse(ga.isConnected());
    }

    /**
     * Test shortest path distance algorithm.
     */
    @Test
    void testShortestPathDist(){
        weighted_graph g = new WGraph_DS();
        for(int i = 1; i <= 7; i++){
            g.addNode(i);
        }
        g.connect(1,2,20);
        g.connect(1,5,15);
        g.connect(2,3,20);
        g.connect(5,6,15);
        g.connect(3,4,20);
        g.connect(6,4,15);
        g.connect(1,7,2);
        g.connect(7,4,50);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g);
        assertTrue(Double.compare(45,ga.shortestPathDist(1,4)) == 0);
        assertEquals(0,ga.shortestPathDist(1,1));
        assertEquals(-1,ga.shortestPathDist(1,200));
    }

    /**
     * Test shortest path algorithm.
     */
    @Test
    void testShortestPath(){
        weighted_graph g = new WGraph_DS();
        for(int i = 1; i <= 7; i++){
            g.addNode(i);
        }
        g.connect(1,2,20);
        g.connect(1,5,15);
        g.connect(2,3,20);
        g.connect(5,6,15);
        g.connect(3,4,20);
        g.connect(6,4,15);
        g.connect(1,7,2);
        g.connect(7,4,1);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g);
        LinkedList<node_info> expectedPath = new LinkedList<>(Arrays.asList(g.getNode(1),g.getNode(7),g.getNode(4)));
        assertNull(ga.shortestPath(1,10));
        assertEquals(expectedPath,ga.shortestPath(1,4));
        assertEquals(1,ga.shortestPath(1,1).size());
    }

    /**
     * Test save and load functions.
     * In this test a graph object is saved to a file,
     * and then it's loaded back to object and compared by equals function with the original.
     */
    @Test
    void testSaveLoad(){
        weighted_graph g1 = new WGraph_DS();
        for(int i = 1; i <= 7; i++){
            g1.addNode(i);
        }
        g1.connect(1,2,20);
        g1.connect(1,5,15);
        g1.connect(2,3,20);
        g1.connect(5,6,15);
        g1.connect(3,4,20);
        g1.connect(6,4,15);
        g1.connect(1,7,2);
        g1.connect(7,4,1);
        weighted_graph_algorithms ga1 = new WGraph_Algo();
        ga1.init(g1);
        ga1.save("myFile.bin");
        weighted_graph g2 = new WGraph_DS();
        weighted_graph_algorithms ga2 = new WGraph_Algo();
        ga2.init(g2);
        ga2.load("myFile.bin");
        assertEquals(ga1.getGraph(),ga2.getGraph());
        g1.removeNode(1);
        assertNotEquals(ga1.getGraph(),ga2.getGraph());
        g1.addNode(1);
        g1.connect(1,2,20);
        g1.connect(1,5,15);
        g1.connect(1,7,2);
        assertEquals(ga1.getGraph(),ga2.getGraph());
        g1.removeNode(1);
        g1.addNode(8);
        g1.connect(8,2,20);
        g1.connect(8,5,15);
        g1.connect(8,7,2);
        assertNotEquals(ga1.getGraph(),ga2.getGraph());
    }

    /**
     * This test checks for possible exceptions.
     */
    @Test
    void testExceptions(){
        weighted_graph g = new WGraph_DS();
        weighted_graph_algorithms ga = new WGraph_Algo();
        assertDoesNotThrow(() -> ga.load("fileWhichDoesntExist.obj"));
    }

    @AfterAll
    static void afterAll(){
        System.out.println("--- End of WGraph_Algo class test ---");
        end = new Date().getTime();
        double dt = (end-start)/1000.0;
        System.out.println("--- Finished in "+dt+" seconds ---");
        System.out.println("\n");
    }

}
