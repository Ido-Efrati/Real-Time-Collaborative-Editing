package RTCE.Server;

import RTCE.Document.RTCE_Document;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Testing strategy - test that operational transforms are being applied correctly. For two
 * edits A and B, where A is applied before B, applyTransform must handle the following
 * scenarios:
 * 
 * (1) A edits text located after B (no overlap): no transformation on B.
 * 
 * (2) A edits text located before B (no overlap): simple shift on B.
 * 
 * (3) A overlaps with B for delete-delete: redundant chars omitted from B, with four cases.
 *                                          (a) A starts/ends after B: truncate B based on A.
 *                                          (b) A starts/ends before B: truncate B based on A.
 *                                          (c) B is contained in A: B is effectively ignored.
 *                                          (d) A is contained in B: subtract length of A from B.
 *                                                                   
 * (4) A overlaps with B for insert-insert: B is shifted to precede the text inserted by A.
 * 
 * (5) A overlaps with B for delete-insert: B is shifted to precede the text deleted by A.
 * 
 * (6) A overlaps with B for insert-delete: B is split into B1 & B2, which delete the two
 *                                          substrings on either side of the text inserted
 *                                          by A.
 *                                          
 * Additionally, we must test that the operational transformations are applied correctly
 * when more than two requests have been queued. For now we will use a dummy RTCE_Document
 * object because we are solely testing the applyTransform() method, which is independent of
 * the document. See RTCE_QueueTest.java for document-level test cases.
 */
public class RTCE_RequestTest {
    public final RTCE_Document doc = new RTCE_Document("doc.txt");

    /**
     * Tests for case (1) as outlined in the overall testing strategy, where the two-letter
     * acronym after testNoOverlapSucceeding represents the two request types in the order
     * of resolution (insert-insert, insert-delete, delete-delete, delete-insert).
     */
    @Test
    public void testNoOverlapSucceedingII() {
        String startText = "no change";
        String expectedOutput ="not changed";

        RTCE_Request editA = new RTCE_Request("insert", 9, "d", doc); // not changed
        RTCE_Request editB = new RTCE_Request("insert", 2, "t", doc); // not changed

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    @Test
    public void testNoOverlapSucceedingID() {
        String startText = "edits save";
        String expectedOutput ="edit saved";

        RTCE_Request editA = new RTCE_Request("insert", 10, "d", doc); // edits saved
        RTCE_Request editB = new RTCE_Request("delete", 4, 5, doc); // edit saved

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    @Test
    public void testNoOverlapSucceedingDD() {
        String startText = "static types";
        String expectedOutput ="stat type";

        RTCE_Request editA = new RTCE_Request("delete", 11, 12, doc); // static type
        RTCE_Request editB = new RTCE_Request("delete", 4, 6, doc); // stat types

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    @Test
    public void testNoOverlapSucceedingDI() {
        String startText = "test passed";
        String expectedOutput ="tests pass";

        RTCE_Request editA = new RTCE_Request("delete", 9, 11, doc); // tests pass
        RTCE_Request editB = new RTCE_Request("insert", 4, "s", doc); // edit saved

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    /**
     * Tests for case (2) as outlined in the overall testing strategy, where the two-letter
     * acronym after testNoOverlapPreceeding represents the two request types in the order
     * of resolution (insert-insert, insert-delete, delete-delete, delete-insert).
     */
    @Test
    public void testNoOverlapPrecedingII() {
        String startText = "google doc";
        String expectedOutput ="googleplex dock";

        RTCE_Request editA = new RTCE_Request("insert", 6, "plex", doc); // googleplex doc
        RTCE_Request editB = new RTCE_Request("insert", 10, "k", doc); // google dock

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    @Test
    public void testNoOverlapPrecedingID() {
        String startText = "op transform";
        String expectedOutput ="opera trans";

        RTCE_Request editA = new RTCE_Request("insert", 2, "era", doc); // opera transform
        RTCE_Request editB = new RTCE_Request("delete", 8, 12, doc); // op trans

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    @Test
    public void testNoOverlapPrecedingDD() {
        String startText = "hello goodbye";
        String expectedOutput ="hell good";

        RTCE_Request editA = new RTCE_Request("delete", 4, 5, doc); // hell goodbye
        RTCE_Request editB = new RTCE_Request("delete", 10, 13, doc); // hello good

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    @Test
    public void testNoOverlapPrecedingDI() {
        String startText = "unit test";
        String expectedOutput ="it testing";

        RTCE_Request editA = new RTCE_Request("delete", 0, 2, doc); // it test
        RTCE_Request editB = new RTCE_Request("insert", 9, "ing", doc); // it testing

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }
    /**
     * Tests for case (3) as outlined in the overall testing strategy.
     */
    @Test
    public void testOverlapDDAAfterB() {
        String startText = "abcdef";
        String expectedOutput ="af";

        RTCE_Request editA = new RTCE_Request("delete", 2, 5, doc); // abf
        RTCE_Request editB = new RTCE_Request("delete", 1, 4, doc); // aef

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    @Test
    public void testOverlapDDABeforeB() {
        String startText = "abcdef";
        String expectedOutput ="af";

        RTCE_Request editA = new RTCE_Request("delete", 1, 4, doc); // aef
        RTCE_Request editB = new RTCE_Request("delete", 2, 5, doc); // abf

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    /**
     * The applyTransform method for this case requires manipulation of the queue;
     * thus, we will only test the general idea of this operational transformation,
     * not the specific implementation. See RTCE_Queue.java for the full testing of
     * the method.
     */
    @Test
    public void testOverlapDDAContainsB() {
        String startText = "eclipse";
        String expectedOutput ="ee";

        RTCE_Request editA = new RTCE_Request("delete", 1, 6, doc); // ee

        String modText = editA.applyEditTesting(startText);

        assertEquals(expectedOutput, modText); // editB is removed entirely from queue
    }

    @Test
    public void testOverlapDDBContainsA() {
        String startText = "eclipse";
        String expectedOutput ="ee";

        RTCE_Request editA = new RTCE_Request("delete", 2, 5, doc); // ecse
        RTCE_Request editB = new RTCE_Request("delete", 1, 6, doc); // ee

        String modText = editA.applyEditTesting(startText);

        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    /**
     * Test for case (4) as outlined in the overall testing strategy.
     */
    @Test
    public void testOverlapII() {
        String startText = "harris";
        String expectedOutput ="neil patrick harris";

        RTCE_Request editA = new RTCE_Request("insert", 0, "patrick ", doc); // patrick harris
        RTCE_Request editB = new RTCE_Request("insert", 0, "neil ", doc); // neil harris

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    /**
     * Test for case (5) as outlined in the overall testing strategy.
     */
    @Test
    public void testOverlapDI() {
        String startText = "it's the beaver";
        String expectedOutput ="it's tim beaver";

        RTCE_Request editA = new RTCE_Request("delete", 4, 8, doc); // it's beaver
        RTCE_Request editB = new RTCE_Request("insert", 4, " tim", doc); // it's tim the beaver

        String modText = editA.applyEditTesting(startText);
        editB.applyTransform(editA);

        assertEquals(expectedOutput, editB.applyEditTesting(modText));
    }

    /**
     * Test for case (6) as outlined in the overall testing strategy.
     * 
     * The applyTransform method for this case requires manipulation of the queue;
     * thus, we will only test the general idea of this operational transformation,
     * not the specific implementation. See RTCE_Queue.java for the full testing of
     * the method.
     */
    @Test
    public void testOverlapID() {
        String startText = "abcde";
        String expectedOutput ="axe";

        RTCE_Request editA = new RTCE_Request("insert", 2, "x", doc); // abxcde

        String modText1 = editA.applyEditTesting(startText);

        RTCE_Request editB1 = new RTCE_Request("delete", 1, 2, doc); // startPos, other.getStartPos()
        RTCE_Request editB2 = new RTCE_Request("delete", 3, 5, doc); // other.getStartPos + shift, endPos + shift

        String modText2 = editB1.applyEditTesting(modText1);

        editB2.applyTransform(editB1);

        assertEquals(expectedOutput, editB2.applyEditTesting(modText2));
    }
}