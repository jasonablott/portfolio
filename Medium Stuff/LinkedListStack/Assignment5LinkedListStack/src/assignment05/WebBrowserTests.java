package assignment05;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class WebBrowserTests {

    SinglyLinkedList<URL> history;
    WebBrowser browser;
    URL linkA;
    URL linkB;
    URL linkC;
    URL linkD;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws MalformedURLException {
        // fill history list
        history = new SinglyLinkedList<URL>();
        linkA = new URL("https://a");
        linkB = new URL("https://b");
        linkC = new URL("https://c");
        linkD = new URL("https://d");
        history.insertFirst(linkC);
        history.insertFirst(linkB);
        history.insertFirst(linkA);

        browser = new WebBrowser<URL>(history);

    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        history = null;
        browser = null;
        linkA = null;
        linkB = null;
        linkC = null;
        linkD = null;
    }

    @org.junit.jupiter.api.Test
    void visit() {
        browser.visit(linkD);
        assertEquals(linkA.toString(), history.getFirst().toString());
        assertEquals(browser.currentPage.toString(), linkD.toString());
    }

    @org.junit.jupiter.api.Test
    void back() {
        assertEquals(browser.currentPage.toString(), linkA.toString());
        assertEquals(browser.back().toString(), linkB.toString());
        assertEquals(browser.currentPage.toString(), linkB.toString());
        assertEquals(browser.back().toString(), linkC.toString());
        assertEquals(browser.currentPage.toString(), linkC.toString());
        assertThrows(NoSuchElementException.class, browser::back);
    }

    @org.junit.jupiter.api.Test
    void forward() {
        browser.back();
        assertEquals(browser.currentPage.toString(), linkB.toString());
        browser.back();
        assertEquals(browser.currentPage.toString(), linkC.toString());
        assertEquals(browser.forward().toString(), linkB.toString());
        assertEquals(browser.currentPage.toString(), linkB.toString());
        assertEquals(browser.forward().toString(), linkA.toString());
        assertEquals(browser.currentPage.toString(), linkA.toString());
        assertThrows(NoSuchElementException.class, browser::forward);

    }

    @org.junit.jupiter.api.Test
    void history() {
        assertEquals(linkA.toString(), browser.history().getFirst().toString());
        assertEquals(linkB.toString(), browser.history().get(1).toString());
        assertEquals(linkC.toString(), browser.history().get(2).toString());

        // test from hw notes

        WebBrowser<URL> browser2 = new WebBrowser<URL>();
        browser2.visit(linkA);
        browser2.visit(linkB);
        browser2.visit(linkC);
        SinglyLinkedList<URL> list = browser2.history();     // list should be [ URL3, URL2, URL1 ]

        WebBrowser<URL> browser3 = new WebBrowser<URL>(list); // URL3 is the "current" webpage, with URL2 and URL1 in the history
        assertEquals(linkC.toString(), browser3.currentPage.toString());
        assertEquals(browser3.back().toString(), linkB.toString());             // should be URL2
        assertEquals(browser3.back().toString(), linkA.toString());           // should be URL1
        assertThrows(NoSuchElementException.class, browser3::back);
    }
}