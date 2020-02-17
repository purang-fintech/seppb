package com.pr.sepp.common;

import org.junit.Test;
import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class HtmlCreateTest {


    @Test
    public void test() throws IOException {
        /**
         *  /**
         *      * <table class="hovertable">
         *      * <tr>
         *      * 	<th>Info Header 1</th><th>Info Header 2</th><th>Info Header 3</th>
         *      * </tr>
         *      * <tr onmouseover="this.style.backgroundColor='#ffff66';" onmouseout="this.style.backgroundColor='#d4e3e5';">
         *      * 	<td>Item 1A</td><td>Item 1B</td><td>Item 1C</td>
         *      * </tr>
         *      * <tr onmouseover="this.style.backgroundColor='#ffff66';" onmouseout="this.style.backgroundColor='#d4e3e5';">
         *      * 	<td>Item 2A</td><td>Item 2B</td><td>Item 2C</td>
         *      * </tr>
         *      * <tr onmouseover="this.style.backgroundColor='#ffff66';" onmouseout="this.style.backgroundColor='#d4e3e5';">
         *      * 	<td>Item 3A</td><td>Item 3B</td><td>Item 3C</td>
         *      * </tr>
         *      * <tr onmouseover="this.style.backgroundColor='#ffff66';" onmouseout="this.style.backgroundColor='#d4e3e5';">
         *      * 	<td>Item 4A</td><td>Item 4B</td><td>Item 4C</td>
         *      * </tr>
         *      * <tr onmouseover="this.style.backgroundColor='#ffff66';" onmouseout="this.style.backgroundColor='#d4e3e5';">
         *      * 	<td>Item 5A</td><td>Item 5B</td><td>Item 5C</td>
         *      * </tr>
         *      * </table>
         *      * @throws IOException
         *      */

        HtmlCanvas html = new HtmlCanvas();
        html.html()
                .body()
                .h1().content("Hello Coder")
                ._body()
                ._html();
        HtmlAttributes attributes = new HtmlAttributes();
        HtmlCanvas htmlCanvas = html.table(attributes.add("class", "hovertable"))._table();

        htmlCanvas.style()._style();
        assertNotNull(htmlCanvas.toHtml());
    }
}
