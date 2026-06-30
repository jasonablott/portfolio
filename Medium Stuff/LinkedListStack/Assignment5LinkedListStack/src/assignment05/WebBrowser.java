package assignment05;

import java.net.URL;
import java.util.NoSuchElementException;

public class WebBrowser<URL> {


    // Member variables


    // simulates back button
    private LinkedListStack<URL> back;
    // simulates forward button
    private LinkedListStack<URL> forward;
    // track current webpage
    URL currentPage;

    // Constructors


    /**
     * Default, zero parameter constructor. This constructor creates a new web
     * browser with no previously-visited webpages and no webpages to visit next.
     */
    public WebBrowser() {
        back = new LinkedListStack<>();
        forward = new LinkedListStack<>();
        currentPage = null;
    }

    /**
     * This constructor creates a new web browser with a preloaded history of visited
     * webpages, given as a list of URLLinks to an external site. objects. The first webpage
     * in the list is the "current" webpage visited, and the remaining webpages are ordered
     * from most recently visited to least recently visited.
     */
    public WebBrowser(SinglyLinkedList<URL> history){
        back = new LinkedListStack<>();
        forward = new LinkedListStack<>();
        currentPage = history.getFirst();
        int size = history.size();
        for (int i = size-1; i > 0; i--) {
            back.push(history.get(i));
        }
//        for (URL url : history) {
//            back.push(url);
//        }
        //currentPage = back.peek();
    }


    // Other methods


    /**
     * This method simulates visiting a webpage, given as a URL. Note that calling this
     * method should clear the forward button stack, since there is no URL to visit next.
     */
    public void visit(URL webpage) {
        if (currentPage != null) {
            back.push(currentPage);
        }
        currentPage = webpage;
        forward.clear();
    }

    /**
     * This method simulates using the back button, returning the URL visited.
     * NoSuchElementExceptionLinks to an external site. is thrown if there is no
     * previously-visited URL.
     */
     public URL back() throws NoSuchElementException {
         forward.push(currentPage);
         currentPage = back.pop();
         return currentPage;
     }

    /**
     * This method simulates using the forward button, returning the URL visited.
     * NoSuchElementException is thrown if there is no URL to visit next.
     */
    public URL forward() throws NoSuchElementException{
        back.push(currentPage);
        currentPage = forward.pop();
        return currentPage;
    }

    /**
     * This method generates a history of URLs visited, as a list of URL objects ordered
     * from most recently visited to least recently visited (including the "current" webpage
     * visited), without altering subsequent behavior of this web browser. "Forward" URLs
     * are not included. The behavior of the method must be O(N), where N is the number of URLs.
     */
    public SinglyLinkedList<URL> history(){
        SinglyLinkedList<URL> history = new SinglyLinkedList<>();
        LinkedListStack<URL> temp = new LinkedListStack<>();
        int size = back.size();

        for (int i = 0; i < size; i++) {
            temp.push(back.pop());
        }
        for (int i = 0; i < size; i++) {
            URL item = temp.pop();
            history.insertFirst(item);
            back.push(item);
        }
        history.insertFirst(currentPage);
        return history;
    }
}
