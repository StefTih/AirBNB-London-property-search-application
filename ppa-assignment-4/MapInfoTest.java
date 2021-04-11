import org.junit.Test;

import static org.junit.Assert.*;

public class MapInfoTest {

    // Instance of the tested MapInfo class
    MapInfo TestMap = new MapInfo();


    /**
     * Test of the getPrefWidth() method.
     */
    @Test
    public void getPrefWidth() {
        double PrefWidth = TestMap.getPrefWidth();
        assertEquals(400, PrefWidth, 0.001);

    }

    /**
     * Test of the getNoProperty() method.
     */
    @Test
    public void getNoProperty() {
        String NoProperty = TestMap.getNoProperty();
        assertEquals("-fx-background-color:#D0D0D0", NoProperty);
    }

    /**
     * Test of the getLowVol() method.
     */
    @Test
    public void getLowVol() {
        String LowVol = TestMap.getLowVol();
        assertEquals("-fx-background-color:#fab1a0", LowVol);

    }

    /**
     * Test of the getMedVol() method.
     */
    @Test
    public void getMedVol() {
        String MedVol = TestMap.getMedVol();
        assertEquals("-fx-background-color:#ffeaa7", MedVol);
    }

    /**
     * Test of the getHighVol() method.
     */
    @Test
    public void getHighVol() {
        String HighVol = TestMap.getHighVol();
        assertEquals("-fx-background-color:#55efc4", HighVol);
    }


    /**
     * Test of the getNeighbourhood() and addAbbreviations() methods.
     */
    @Test
    public void getNeighbourhood() {

        TestMap.addAbbreviations("ENFI","Enfield");
        String getNeighbourhood = TestMap.getNeighbourhood("ENFI");
        assertEquals("Enfield",getNeighbourhood);

    }

    /**
     * Test of the getAbbreviation and addAbreviations methods.
     */
    @Test
    public void getAbbreviation() {

        TestMap.addAbbreviations("ENFI", "Enfield");
        String getAbbreviation = TestMap.getAbbreviation("Enfield");
        assertEquals("ENFI",getAbbreviation);

    }


    /**
     * Test of the propertyVolumeColour() method.
     */
    @Test
    public void propertyVolumeColour() {

        String propertyVolumeColour = TestMap.propertyVolumeColour("Enfield");
        assertEquals("-fx-background-color:#D0D0D0", propertyVolumeColour);
    }

    /**
     * Test of the getNumberOfOccurrences() method.
     */
    @Test
    public void getNumberOfOccurrences() {
        int getNumberOfOccurrences = TestMap.getNumberOfOccurrences("Enfield");
        assertEquals(0,getNumberOfOccurrences);

    }

    /**
     * Test of the convertInt() method.
     */
    @Test
    public void convertInt() {

        Integer convertInt = TestMap.convertInt("12345");
        Integer testInteger = 12345;
        assertEquals(testInteger, convertInt);

    }

}