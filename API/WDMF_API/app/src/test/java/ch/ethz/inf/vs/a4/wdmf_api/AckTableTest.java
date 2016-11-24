package ch.ethz.inf.vs.a4.wdmf_api;

import org.junit.Test;

public class AckTableTest {

    @Test
    public void toStringWorks() throws Exception {

        AckTable ack1 = new AckTable("a");
        AckTable ack2 = new AckTable("b");
        AckTable ack3 = new AckTable("c");

        /*  ack1.insert("a", "b", 1);
        ack1.insert("b", "a", 2);
        ack1.insert("b", "b", -1);

        ack2.insert("b", "c", 3);
        ack2.insert("c", "b", 4);
        ack2.insert("c", "c", -1);
         */

        ack1.merge(ack2);
        ack3.merge(ack1);
        System.out.println("ack1 = " + ack1.toString());
        System.out.println("ack2 = " + ack2.toString());
        System.out.println("ack3 = " + ack3.toString());

        //assertEquals("", ack1.toString());
        //assertEquals("", ack2.toString());
    }
}
