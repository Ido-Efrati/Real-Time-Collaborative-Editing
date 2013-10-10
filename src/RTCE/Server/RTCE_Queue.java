package RTCE.Server;

import java.util.ArrayList;

import RTCE.Document.RTCE_Document;
/*
 * Thread safety argument: after initialization, the mutator methods
 * are all contained within synchronized blocks. The requestQueue
 * acquires a lock for the duration of any of these methods, i.e.
 * only one user can modify the queue at a time. This ensures that
 * there will be no race conditions and that the queue is completely
 * thread-safe.
 */
/**
 * Represents a queue of RTCE_Request objects. Has two methods:
 * addRequest, which adds a new RTCE_Request to the queue, and
 * resolveRequest, which resolves the request at the front of
 * the queue, applies any necessary operational transformations
 * to other requests in the queue. The rep invariant is the
 * RTCE_Document that this RTCE_Queue is associated with; there
 * is a one-to-one correspondence, and this assignment never
 * changes at any point.
 */
public class RTCE_Queue {
    public final RTCE_Document doc;
    private ArrayList<RTCE_Request> requestQueue = new ArrayList<RTCE_Request>();
    // used an ArrayList instead of a queue as we need to be able to get the index
    // of an element and insert at a certain index
    
    /**
     * Constructor method for a RTCE_Queue, which keeps track of edit
     * requests made for a particular document and applies them to the
     * server copy of the document (truth). Also responsible for telling
     * RTCE_Request objects when to apply operational transformations.
     * 
     * @param doc The document that this queue is associated with.
     */
    public RTCE_Queue(RTCE_Document doc) {
        this.doc = doc;
    }
    
    /**
     * Method to add a request to the end of the queue.
     * @param request The RTCE_Request to be added.
     */
    public void addRequest(RTCE_Request request) {
        synchronized(requestQueue) {
            requestQueue.add(request);
        }
    }
    
    /**
     * Method to add a request to the queue at a specified index.
     * @param request The RTCE_Request to be added.
     */
    public void addRequestAtIndex(int index, RTCE_Request request) {
        synchronized(requestQueue) {
            requestQueue.add(index, request);
        }
    }
    
    /**
     * Method to removes the first instance of a request from the queue. 
     * @param request The RTCE_Request to be removed.
     */
    public void removeRequest(RTCE_Request request) {
        synchronized(requestQueue) {
            requestQueue.remove(request);
        }
    }
    
    /**
     * Method to find the first instance of a request in the queue.
     * @param request The RTCE_Request to look for.
     * @return The index of the specified request.
     */
    public int findRequest(RTCE_Request request) {
        synchronized(requestQueue) {
            return requestQueue.indexOf(request);
        }
    }
    
    /**
     * Resolves the request at the front of the queue. This consists of
     * calling its applyEdit() method, iterating through the rest of the
     * queue to apply any necessary operational transforms.
     */
    public void resolveRequest() {
        synchronized(requestQueue) {
            RTCE_Request request = requestQueue.get(0);
            requestQueue.remove(0);
            request.applyEdit(); // make changes to doc
            if (!requestQueue.isEmpty()) {
                for (RTCE_Request req : requestQueue) {
                    req.applyTransform(request); // transform all successive
                                                 // edit requests based on
                                                 // this one
                }
            }
        }
    }
}