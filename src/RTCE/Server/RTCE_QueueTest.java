package RTCE.Server;

import RTCE.Document.RTCE_Document;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Testing strategy: similar to that of RTCE_Request, except this time on a
 * document level. Once again we will test each operational transformation
 * case (outlined in RTCE_RequestTest.java), except this time making use of
 * the queue (specifically the resolveRequest and applyEdit methods) that
 * will be used the actual RTCE implementation. We must also pay particular
 * attention to cases (3)(c) and (6), which were not tested fully in the
 * RTCE_Request testing suite due to the need to manipulate the actual queue.
 * The following scenarios will be tested: (1) basic testing of the queue,
 * (2) all six operational transformation cases (consisting of two edits each),
 * and finally (3) a series of more than two edits.
 */
public class RTCE_QueueTest {
    /**
     * Single request resolution.
     */
    @Test
    public void basicQueueTest() {
        RTCE_Document doc = new RTCE_Document("doc.txt"); // empty document
        RTCE_Queue queue = new RTCE_Queue(doc);
        String expectedOutput = "Hello world!";

        RTCE_Request request = new RTCE_Request("insert", 0, "Hello world!", doc);
        queue.addRequest(request);
        queue.resolveRequest();
        assertEquals(expectedOutput, doc.getAllText());
    }

    /**
     * OT case (1): A edits text after B, no overlap.
     */
    @Test
    public void opTransform1Test() {
        RTCE_Document doc = new RTCE_Document("doc.txt"); // empty document
        RTCE_Queue queue = new RTCE_Queue(doc);
        doc.insert(0, "cd"); // initial text: "cd"
        String expectedOutput = "abcdef";

        queue.addRequest(new RTCE_Request("insert", 2, "ef", doc));
        queue.addRequest(new RTCE_Request("insert", 0, "ab", doc));
        queue.resolveRequest();
        queue.resolveRequest();
        assertEquals(expectedOutput, doc.getAllText());
    }

    /**
     * OT case (2): A edits text before B, no overlap.
     */
    @Test
    public void opTransform2Test() {
        RTCE_Document doc = new RTCE_Document("doc.txt"); // empty document
        RTCE_Queue queue = new RTCE_Queue(doc);
        doc.insert(0, "cd"); // initial text: "cd"
        String expectedOutput = "abcdef";

        queue.addRequest(new RTCE_Request("insert", 0, "ab", doc));
        queue.addRequest(new RTCE_Request("insert", 2, "ef", doc));
        queue.resolveRequest();
        queue.resolveRequest();
        assertEquals(expectedOutput, doc.getAllText());
    }

    /**
     * OT case (3)(a): A starts/ends before B, delete-delete.
     */
    @Test
    public void opTransform3ATest() {
        RTCE_Document doc = new RTCE_Document("doc.txt"); // empty document
        RTCE_Queue queue = new RTCE_Queue(doc);
        doc.insert(0, "jar of lava"); // initial text: "jar of lava"
        String expectedOutput = "java";

        queue.addRequest(new RTCE_Request("delete", 2, 6, doc));
        queue.addRequest(new RTCE_Request("delete", 4, 9, doc));
        queue.resolveRequest();
        queue.resolveRequest();
        assertEquals(expectedOutput, doc.getAllText());
    }

    /**
     * OT case (3)(b): A starts/ends after B, delete-delete.
     */
    @Test
    public void opTransform3BTest() {
        RTCE_Document doc = new RTCE_Document("doc.txt"); // empty document
        RTCE_Queue queue = new RTCE_Queue(doc);
        doc.insert(0, "jar of lava"); // initial text: "jar of lava"
        String expectedOutput = "java";

        queue.addRequest(new RTCE_Request("delete", 4, 9, doc));
        queue.addRequest(new RTCE_Request("delete", 2, 6, doc));
        queue.resolveRequest();
        queue.resolveRequest();
        assertEquals(expectedOutput, doc.getAllText());
    }

    /**
     * OT case (3)(c): B contains A, delete-delete.
     */
    @Test
    public void opTransform3CTest() {
        RTCE_Document doc = new RTCE_Document("doc.txt"); // empty document
        RTCE_Queue queue = new RTCE_Queue(doc);
        doc.insert(0, "google drive"); // initial text: "google drive"
        String expectedOutput = "google";

        queue.addRequest(new RTCE_Request("delete", 7, 9, doc));
        queue.addRequest(new RTCE_Request("delete", 5, 11, doc));
        queue.resolveRequest();
        queue.resolveRequest();
        assertEquals(expectedOutput, doc.getAllText());
    }

    /**
     * OT case (4): Insert-insert.
     */
    @Test
    public void opTransform4Test() {
        RTCE_Document doc = new RTCE_Document("doc.txt"); // empty document
        RTCE_Queue queue = new RTCE_Queue(doc);
        doc.insert(0, "xyzzy"); // initial text: "xyzzy"
        String expectedOutput = "xyzabclulzy";

        queue.addRequest(new RTCE_Request("insert", 3, "lul", doc));
        queue.addRequest(new RTCE_Request("insert", 3, "abc", doc));
        queue.resolveRequest();
        queue.resolveRequest();
        assertEquals(expectedOutput, doc.getAllText());
    }

    /**
     * OT case (5): Delete-insert.
     */
    @Test
    public void opTransform5Test() {
        RTCE_Document doc = new RTCE_Document("doc.txt"); // empty document
        RTCE_Queue queue = new RTCE_Queue(doc);
        doc.insert(0, "reminder"); // initial text: "reminder"
        String expectedOutput = "rejoinder";

        queue.addRequest(new RTCE_Request("delete", 2, 6, doc));
        queue.addRequest(new RTCE_Request("insert", 4, "joind", doc));
        queue.resolveRequest();
        queue.resolveRequest();
        assertEquals(expectedOutput, doc.getAllText());
    }

    /**
     * OT case (6): Insert-delete.
     */
    @Test
    public void opTransform6Test() {
        RTCE_Document doc = new RTCE_Document("doc.txt"); // empty document
        RTCE_Queue queue = new RTCE_Queue(doc);
        doc.insert(0, "reminder"); // initial text: "reminder"
        String expectedOutput = "rejoinder";

        queue.addRequest(new RTCE_Request("insert", 3, "jo", doc));
        queue.addRequest(new RTCE_Request("delete", 2, 3, doc));
        queue.resolveRequest();
        queue.resolveRequest();
        assertEquals(expectedOutput, doc.getAllText());
    }

    /**
     * Complex sequence of requests.
     */
    @Test
    public void opTransformMedleyTest() {
        RTCE_Document doc = new RTCE_Document("doc.txt"); // empty document
        RTCE_Queue queue = new RTCE_Queue(doc);
        doc.insert(0, "Hello world!"); // initial text: "Hello world!"
        String expectedOutput = "o n to the peace ?!";

        queue.addRequest(new RTCE_Request("insert", 11, " ?", doc)); // Hello world?!
        queue.addRequest(new RTCE_Request("insert", 11, " peace", doc)); // Hello world peace!
        queue.addRequest(new RTCE_Request("insert", 5, " to the", doc)); // Hello to the world!
        queue.addRequest(new RTCE_Request("delete", 5, 11, doc)); // Hello!
        queue.addRequest(new RTCE_Request("delete", 0, 4, doc)); // o world!
        queue.addRequest(new RTCE_Request("insert", 4, " n", doc)); // Hell no world!

        queue.resolveRequest();
        queue.resolveRequest();
        queue.resolveRequest();
        queue.resolveRequest();
        queue.resolveRequest();
        queue.resolveRequest();

        assertEquals(expectedOutput, doc.getAllText());
    }
}